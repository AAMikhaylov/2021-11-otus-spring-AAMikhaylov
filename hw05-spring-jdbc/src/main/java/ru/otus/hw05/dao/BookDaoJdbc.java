package ru.otus.hw05.dao;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.EmptySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw05.dao.ext.BookAuthorResultSetExtractor;
import ru.otus.hw05.dao.ext.BookGenreRelation;
import ru.otus.hw05.domain.Author;
import ru.otus.hw05.domain.Book;
import ru.otus.hw05.domain.Genre;
import ru.otus.hw05.exceptions.DaoException;
import ru.otus.hw05.exceptions.DaoIntegrityViolationException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class BookDaoJdbc implements BookDao {
    private final NamedParameterJdbcOperations namedJdbcOperations;
    private final GenreDao genreDao;

    @Override
    public int count() {
        try {
            val count = namedJdbcOperations.queryForObject("select count(*) from BOOKS", new EmptySqlParameterSource(), Integer.class);
            return count == null ? 0 : count;
        } catch (Exception e) {
            throw new DaoException(e.getMessage(), e);
        }
    }

    private void batchInsertGenreRelations(long bookId, List<Genre> genres) {
        try {
            List<Genre> uniqueGenres = genres.stream().distinct().collect(Collectors.toList());
            MapSqlParameterSource[] batchParams = new MapSqlParameterSource[uniqueGenres.size()];
            for (int i = 0; i < uniqueGenres.size(); i++) {
                batchParams[i] = new MapSqlParameterSource();
                batchParams[i].addValue("book_id", bookId).addValue("genre_id", uniqueGenres.get(i).getId());
            }
            namedJdbcOperations.batchUpdate("insert into BOOKS_GENRES (BOOK_ID,GENRE_ID) values (:book_id,:genre_id)", batchParams);
        } catch (DataIntegrityViolationException e) {
            throw new DaoIntegrityViolationException("messages.book.update.error.genre.integrityViolation", e);
        } catch (Exception e) {
            throw new DaoException(e.getMessage(), e);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public long create(Book book) {
        try {
            val params = new MapSqlParameterSource();
            Author author = book.getAuthor();
            params.addValue("title", book.getTitle())
                    .addValue("short_content", book.getShortContent())
                    .addValue("author_id", author.getId());
            KeyHolder kh = new GeneratedKeyHolder();
            namedJdbcOperations.update("insert into BOOKS (AUTHOR_ID,TITLE,SHORT_CONTENT) values (:author_id,:title,:short_content) ", params, kh);
            Optional<Number> key = Optional.ofNullable(kh.getKey());
            long bookId = key.orElseThrow(() -> new DaoException("messages.book.create.error.getId")).longValue();
            batchInsertGenreRelations(bookId, book.getGenres());
            return bookId;
        } catch (DaoException e) {
            throw e;
        } catch (DataIntegrityViolationException e) {
            throw new DaoIntegrityViolationException("messages.book.update.error.author.integrityViolation", e);
        } catch (Exception e) {
            throw new DaoException(e.getMessage(), e);
        }
    }

    private void mergeBooksGenres(Map<Long, Book> allBooks, List<Genre> usedGenres, List<BookGenreRelation> bookGenreRelations) {
        Map<Long, Genre> usedGenresMap = usedGenres.stream().collect(Collectors.toMap(Genre::getId, Function.identity()));
        for (BookGenreRelation bookGenreRelation : bookGenreRelations) {
            Book book = allBooks.get(bookGenreRelation.getBookId());
            Genre genre = usedGenresMap.get(bookGenreRelation.getGenreId());
            if (book != null && genre != null) {
                book.getGenres().add(genre);
            }
        }
    }

    private List<BookGenreRelation> getAllBookGenreRelation() {
        return namedJdbcOperations.getJdbcOperations().query(
                "select BOOK_ID, GENRE_ID from BOOKS_GENRES",
                (rs, i) -> new BookGenreRelation(rs.getLong("BOOK_ID"), rs.getLong("GENRE_ID")));
    }

    @Override
    public List<Book> getAll() {
        try {
            val bookAuthorExtractor = new BookAuthorResultSetExtractor();
            Map<Long, Book> allBooks = namedJdbcOperations.getJdbcOperations().query(
                    "select BOOKS.ID, TITLE, SHORT_CONTENT, AUTHOR_ID, FIRST_NAME, MIDDLE_NAME, LAST_NAME " +
                            "from BOOKS join AUTHORS on BOOKS.AUTHOR_ID = AUTHORS.ID",
                    bookAuthorExtractor);
            List<BookGenreRelation> bookGenreRelations = getAllBookGenreRelation();
            List<Genre> usedGenres = genreDao.getAllUsed();
            mergeBooksGenres(allBooks, usedGenres, bookGenreRelations);
            return new ArrayList<>(Objects.requireNonNull(allBooks).values());
        } catch (Exception e) {
            throw new DaoException(e.getMessage(), e);
        }
    }

    @Override
    public Optional<Book> getById(long id) {
        try {
            val params = Map.of("id", id);
            List<Genre> genres = namedJdbcOperations.query(
                    "select ID, NAME " +
                            "FROM GENRES join BOOKS_GENRES BG on GENRES.ID = BG.GENRE_ID and BG.BOOK_ID=:id",
                    params,
                    (rs, rowNum) -> new Genre(rs.getLong("ID"), rs.getString("NAME")));
            return
                    Optional.ofNullable(namedJdbcOperations.queryForObject(
                            "select BOOKS.ID, AUTHOR_ID, TITLE, SHORT_CONTENT, FIRST_NAME, MIDDLE_NAME, LAST_NAME " +
                                    "from BOOKS join AUTHORS on BOOKS.AUTHOR_ID = AUTHORS.ID and BOOKS.ID=:id",
                            params, new BookWithGenresMapper(genres)));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        } catch (Exception e) {
            throw new DaoException(e.getMessage(), e);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public int update(Book book) {
        try {
            val params = Map.of(
                    "id", book.getId(),
                    "author_id", book.getAuthor().getId(),
                    "title", book.getTitle(),
                    "short_content", book.getShortContent());
            int updCount = namedJdbcOperations.update("update BOOKS set AUTHOR_ID=:author_id, TITLE=:title, SHORT_CONTENT=:short_content where ID=:id", params);
            namedJdbcOperations.update("delete from BOOKS_GENRES where BOOK_ID=:id", params);
            batchInsertGenreRelations(book.getId(), book.getGenres());
            return updCount;
        } catch (DaoIntegrityViolationException e) {
            throw e;
        } catch (DataIntegrityViolationException e) {
            throw new DaoIntegrityViolationException("messages.book.update.error.author.integrityViolation", e);
        } catch (Exception e) {
            throw new DaoException(e.getMessage(), e);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public int deleteById(long id) {
        try {
            val params = Map.of("id", id);
            return namedJdbcOperations.update("delete from BOOKS where ID=:id", params);
        } catch (DataIntegrityViolationException e) {
            throw new DaoIntegrityViolationException("messages.book.delete.error.integrityViolation", e);
        } catch (Exception e) {
            throw new DaoException(e.getMessage(), e);
        }
    }

    private static class BookWithGenresMapper implements RowMapper<Book> {
        private final List<Genre> genres;

        private BookWithGenresMapper(List<Genre> genres) {
            this.genres = genres;
        }

        @Override
        public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
            Author author = new Author(rs.getLong("AUTHOR_ID"),
                    rs.getString("FIRST_NAME"),
                    rs.getString("MIDDLE_NAME"),
                    rs.getString("LAST_NAME"));
            return new Book(rs.getLong("ID"),
                    author,
                    rs.getString("TITLE"),
                    rs.getString("SHORT_CONTENT"),
                    genres);
        }
    }
}
