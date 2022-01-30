package ru.otus.hw05.dao;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.hw05.domain.Author;
import ru.otus.hw05.exceptions.DaoException;
import ru.otus.hw05.exceptions.DaoIntegrityViolationException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AuthorDaoJdbc implements AuthorDao {

    private final NamedParameterJdbcOperations namedParameterJdbcOperations;

    @Override
    public Optional<Author> getById(long id) {
        try {
            val params = Map.of("id", id);
            return Optional.ofNullable(namedParameterJdbcOperations.queryForObject("select ID, FIRST_NAME, MIDDLE_NAME, LAST_NAME from AUTHORS where ID=:id", params, new AuthorMapper()));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        } catch (Exception e) {
            throw new DaoException(e.getMessage(), e);
        }
    }

    @Override
    public List<Author> getAll() {
        try {
            return namedParameterJdbcOperations.query("select ID, FIRST_NAME, MIDDLE_NAME, LAST_NAME from AUTHORS", new AuthorMapper());
        } catch (Exception e) {
            throw new DaoException(e.getMessage(), e);
        }

    }

    @Override
    public int deleteById(long id) {
        try {
            val params = Map.of("id", id);
            return namedParameterJdbcOperations.update("delete from AUTHORS where ID=:id", params);
        } catch (DataIntegrityViolationException e) {
            throw new DaoIntegrityViolationException("messages.author.delete.error.integrityViolation", e);
        } catch (Exception e) {
            throw new DaoException(e.getMessage(), e);
        }
    }

    @Override
    public int update(Author author) {
        try {
            val params = Map.of(
                    "id", author.getId(),
                    "first_name", author.getFirstName(),
                    "middle_name", author.getMiddleName(),
                    "last_name", author.getLastName()
            );
            return namedParameterJdbcOperations.update("update AUTHORS set FIRST_NAME=:first_name, MIDDLE_NAME=:middle_name, LAST_NAME=:last_name where ID=:id", params);
        } catch (DataIntegrityViolationException e) {
            throw new DaoIntegrityViolationException("messages.author.update.error.integrityViolation", e);
        } catch (Exception e) {
            throw new DaoException(e.getMessage(), e);
        }
    }

    @Override
    public long create(Author author) {
        try {
            val params = new MapSqlParameterSource();
            params.addValue("first_name", author.getFirstName())
                    .addValue("middle_name", author.getMiddleName())
                    .addValue("last_name", author.getLastName());
            KeyHolder kh = new GeneratedKeyHolder();
            namedParameterJdbcOperations.update("insert into AUTHORS (FIRST_NAME,MIDDLE_NAME,LAST_NAME) values (:first_name,:middle_name,:last_name)", params, kh);
            Optional<Number> key = Optional.ofNullable(kh.getKey());
            return key.orElseThrow(() -> new DaoException("messages.author.create.error.getId")).longValue();
        } catch (DaoException e) {
            throw e;
        } catch (Exception e) {
            throw new DaoException(e.getMessage(), e);
        }
    }

    @Override
    public int count() {
        try {
            val cntInt = namedParameterJdbcOperations.getJdbcOperations().queryForObject("select count(*) from AUTHORS", Integer.class);
            return cntInt == null ? 0 : cntInt;
        } catch (Exception e) {
            throw new DaoException(e.getMessage(), e);
        }

    }

    private static class AuthorMapper implements RowMapper<Author> {
        @Override
        public Author mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Author(rs.getLong("ID"), rs.getString("FIRST_NAME"), rs.getString("MIDDLE_NAME"), rs.getString("LAST_NAME"));
        }
    }

}
