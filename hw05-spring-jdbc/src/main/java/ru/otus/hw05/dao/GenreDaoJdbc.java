package ru.otus.hw05.dao;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.hw05.domain.Genre;
import ru.otus.hw05.exceptions.DaoException;
import ru.otus.hw05.exceptions.DaoIntegrityViolationException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class GenreDaoJdbc implements GenreDao {
    private final NamedParameterJdbcOperations namedParameterJdbcOperations;

    @Override
    public int count() {
        try {
            val result = namedParameterJdbcOperations.getJdbcOperations().queryForObject("select count(*) from GENRES", Integer.class);
            return result == null ? 0 : result;
        } catch (Exception e) {
            throw new DaoException(e.getMessage(), e);
        }
    }

    @Override
    public Optional<Genre> getById(long id) {
        try {
            val params = Map.of("id", id);
            return Optional.ofNullable(namedParameterJdbcOperations.queryForObject("select ID, NAME from GENRES where ID=:id", params,
                    (rs, i) -> new Genre(rs.getLong("id"), rs.getString("name"))));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        } catch (Exception e) {
            throw new DaoException(e.getMessage(), e);
        }
    }

    @Override
    public List<Genre> getAll() {
        try {
            return namedParameterJdbcOperations.query("select ID, NAME from GENRES",
                    (rs, i) -> new Genre(rs.getLong("ID"), rs.getString("NAME")));
        } catch (Exception e) {
            throw new DaoException(e.getMessage(), e);
        }
    }

    @Override
    public List<Genre> getAllUsed() {
        try {
            return namedParameterJdbcOperations.query("select ID, NAME FROM GENRES join BOOKS_GENRES BG on GENRES.ID = BG.GENRE_ID group by ID",
                    (rs, i) -> new Genre(rs.getLong("ID"), rs.getString("NAME")));
        } catch (Exception e) {
            throw new DaoException(e.getMessage(), e);
        }
    }


    @Override
    public int deleteById(long id) {
        try {
            val params = Map.of("id", id);
            return namedParameterJdbcOperations.update("delete from GENRES where ID=:id", params);
        } catch (DataIntegrityViolationException e) {
            throw new DaoIntegrityViolationException("messages.genre.delete.error.integrityViolation", e);
        } catch (Exception e) {
            throw new DaoException(e.getMessage(), e);
        }
    }

    @Override
    public int update(Genre genre) {
        try {
            val params = Map.of("id", genre.getId(),
                    "name", genre.getName()
            );
            return namedParameterJdbcOperations.update("update GENRES set NAME=:name where ID=:id", params);
        } catch (DataIntegrityViolationException e) {
            throw new DaoIntegrityViolationException("messages.genre.update.error.integrityViolation", e);
        } catch (Exception e) {
            throw new DaoException(e.getMessage(), e);
        }
    }

    @Override
    public long create(Genre genre) {
        try {
            val params = new MapSqlParameterSource();
            params.addValue("name", genre.getName());
            KeyHolder kh = new GeneratedKeyHolder();
            namedParameterJdbcOperations.update("insert into GENRES (NAME) values (:name)", params, kh);
            Optional<Number> key = Optional.ofNullable(kh.getKey());
            return key.orElseThrow(() -> new DaoException("messages.genre.create.error.getId")).longValue();
        } catch (DaoException e) {
            throw e;
        } catch (Exception e) {
            throw new DaoException(e.getMessage(), e);
        }
    }
}
