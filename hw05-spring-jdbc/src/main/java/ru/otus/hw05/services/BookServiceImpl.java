package ru.otus.hw05.services;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Component;
import ru.otus.hw05.dao.AuthorDao;
import ru.otus.hw05.dao.BookDao;
import ru.otus.hw05.dao.GenreDao;
import ru.otus.hw05.domain.Author;
import ru.otus.hw05.domain.Book;
import ru.otus.hw05.domain.Genre;
import ru.otus.hw05.exceptions.DaoException;
import ru.otus.hw05.exceptions.ExceptionHandler;
import ru.otus.hw05.io.IOMessageService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookDao bookDao;
    private final AuthorService authorService;
    private final AuthorDao authorDao;
    private final GenreService genreService;
    private final GenreDao genreDao;
    private final IOMessageService ioMessageService;
    private final ExceptionHandler exceptionHandler;

    @Override
    public void readAll() {
        try {
            ioMessageService.writeLocal("messages.book.read.title");
            ioMessageService.writeLocal("messages.book.read.tableHeaders");
            val books = bookDao.getAll();
            books.forEach(this::printBookInfo);
        } catch (Exception e) {
            exceptionHandler.handle(e);
        }
    }

    @Override
    public void create() {
        try {
            ioMessageService.writeLocal("messages.book.create.title");
            ioMessageService.writeLocal("messages.book.create.titleBook");
            val titleBook = ioMessageService.read();
            if (titleBook.equalsIgnoreCase("q")) {
                ioMessageService.writeLocal("messages.cancelOperation");
                return;
            }
            val author = getInputAuthor();
            if (author.isEmpty())
                return;
            val genres = getInputGenres();
            ioMessageService.writeLocal("messages.book.create.shortContent");
            val shortContent = ioMessageService.read();
            if (shortContent.equalsIgnoreCase("q")) {
                ioMessageService.writeLocal("messages.cancelOperation");
                return;
            }
            val book = new Book(author.get(), titleBook, shortContent, genres);
            long id = bookDao.create(book);
            ioMessageService.writeLocal("messages.book.create.complete", Long.toString(id));
        } catch (Exception e) {
            exceptionHandler.handle(e);
        }
    }

    @Override
    public void delete() {
        try {
            ioMessageService.writeLocal("messages.book.delete.title");
            val id = getInputId();
            if (id == 0L) {
                return;
            }
            val delCnt = bookDao.deleteById(id);
            ioMessageService.writeLocal("messages.book.delete.complete", Integer.toString(delCnt));
        } catch (Exception e) {
            exceptionHandler.handle(e);
        }
    }

    @Override
    public void update() {
        try {
            ioMessageService.writeLocal("messages.book.update.title");
            val id = getInputId();
            if (id == 0L)
                return;
            val book = bookDao.getById(id).orElseThrow(() -> new DaoException("messages.book.read.error.notFound"));
            ioMessageService.writeLocal("messages.book.update.titleBook", book.getTitle());
            var titleBook = ioMessageService.read();
            if (titleBook.equalsIgnoreCase("q")) {
                ioMessageService.writeLocal("messages.cancelOperation");
                return;
            }
            titleBook = titleBook.trim().isEmpty() ? book.getTitle() : titleBook;
            var author = book.getAuthor();
            ioMessageService.writeLocal("messages.book.update.changeAuthor", author.getShortName());
            if (ioMessageService.read().equalsIgnoreCase("y")) {
                val authorOpt = getInputAuthor();
                if (authorOpt.isEmpty())
                    return;
                author = authorOpt.get();
            }
            List<Genre> genres = book.getGenres();
            ioMessageService.writeLocal("messages.book.update.changeGenres", genres.stream().map(Genre::getName).collect(Collectors.joining(", ")));
            if (ioMessageService.read().equalsIgnoreCase("y")) {
                genres = getInputGenres();
            }
            ioMessageService.writeLocal("messages.book.update.shortContent", book.getShortContent());
            var shortContent = ioMessageService.read();
            if (shortContent.equalsIgnoreCase("q")) {
                ioMessageService.writeLocal("messages.cancelOperation");
                return;
            }
            shortContent = shortContent.trim().isEmpty() ? book.getShortContent() : shortContent;
            val updatedBook = new Book(book.getId(), author, titleBook, shortContent, genres);
            var updCnt = 0;
            if (!updatedBook.equals(book))
                updCnt = bookDao.update(updatedBook);
            ioMessageService.writeLocal("messages.book.update.complete", Integer.toString(updCnt));
        } catch (Exception e) {
            exceptionHandler.handle(e);
        }
    }

    @Override
    public void readById() {
        try {
            ioMessageService.writeLocal("messages.book.readById.title");
            val id = getInputId();
            if (id == 0L)
                return;
            val book = bookDao.getById(id).orElseThrow(() -> new DaoException("messages.book.read.error.notFound"));
            ioMessageService.writeLocal("messages.book.read.tableHeaders");
            printBookInfo(book);
        } catch (Exception e) {
            exceptionHandler.handle(e);
        }
    }

    private Optional<Author> getInputAuthor() {
        while (true) {
            try {
                ioMessageService.writeLocal("messages.book.authorId");
                val strAuthorId = ioMessageService.read();
                if (strAuthorId.trim().equalsIgnoreCase("q")) {
                    ioMessageService.writeLocal("messages.cancelOperation");
                    return Optional.empty();
                }
                if (strAuthorId.trim().equalsIgnoreCase("l")) {
                    authorService.readAll();
                    continue;
                }
                try {
                    long authorId = Long.parseLong(strAuthorId);
                    Author author = authorDao.getById(authorId).orElseThrow(() -> new DaoException("messages.author.read.error.notFound"));
                    return Optional.of(author);
                } catch (NumberFormatException e) {
                    throw new NumberFormatException("messages.author.error.idFormat");
                }
            } catch (Exception e) {
                exceptionHandler.handle(e);
            }
        }
    }

    private List<Genre> getInputGenres() {
        ioMessageService.writeLocal("messages.book.addGenres");
        List<Genre> genres = new ArrayList<>();
        var strGenreId = "";
        while (!strGenreId.equalsIgnoreCase("f")) {
            try {
                ioMessageService.writeLocal("messages.book.genreId");
                strGenreId = ioMessageService.read();
                if (strGenreId.equalsIgnoreCase("f")) {
                    break;
                }
                if (strGenreId.equalsIgnoreCase("l")) {
                    genreService.readAll();
                    continue;
                }
                try {
                    long genreId = Long.parseLong(strGenreId);
                    val genre = genreDao.getById(genreId).orElseThrow(() -> new DaoException("messages.genre.read.error.notFound"));
                    genres.add(genre);
                } catch (NumberFormatException e) {
                    throw new NumberFormatException("messages.genre.error.idFormat");
                }
            } catch (Exception e) {
                exceptionHandler.handle(e);
            }
        }
        genres = genres.stream().distinct().collect(Collectors.toList());
        ioMessageService.writeLocal("messages.book.genresAdded", Integer.toString(genres.size()));
        return genres;
    }

    private long getInputId() {
        while (true) {
            try {
                ioMessageService.writeLocal("messages.book.id");
                val strId = ioMessageService.read();
                if (strId.trim().equalsIgnoreCase("q")) {
                    ioMessageService.writeLocal("messages.cancelOperation");
                    return 0L;
                }
                if (strId.trim().equalsIgnoreCase("l")) {
                    readAll();
                    continue;
                }
                try {
                    return Long.parseLong(strId);
                } catch (NumberFormatException e) {
                    throw new NumberFormatException("messages.book.error.idFormat");
                }
            } catch (Exception e) {
                exceptionHandler.handle(e);
            }
        }
    }

    private void printBookInfo(Book book) {
        ioMessageService.writeLocal(
                "messages.book.info",
                Long.toString(book.getId()),
                book.getAuthor().getShortName(),
                book.getTitle(),
                book.getGenres().stream().map(Genre::getName).collect(Collectors.joining(", ")),
                book.getShortContent());
    }
}
