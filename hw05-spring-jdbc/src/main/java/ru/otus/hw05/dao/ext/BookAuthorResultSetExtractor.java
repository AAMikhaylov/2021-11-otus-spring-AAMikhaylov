package ru.otus.hw05.dao.ext;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import ru.otus.hw05.domain.Author;
import ru.otus.hw05.domain.Book;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BookAuthorResultSetExtractor implements ResultSetExtractor<Map<Long, Book>> {
    @Override
    public Map<Long, Book> extractData(ResultSet rs) throws SQLException, DataAccessException {
        final Map<Long, Book> allBook = new HashMap<>();
        final Map<Long, Author> usedAuthors = new HashMap<>();
        while (rs.next()) {
            long authorId = rs.getLong("AUTHOR_ID");
            Author author = usedAuthors.get(authorId);
            if (author == null) {
                author = new Author(authorId, rs.getString("FIRST_NAME"), rs.getString("MIDDLE_NAME"), rs.getString("LAST_NAME"));
                usedAuthors.put(authorId, author);
            }
            Book book = new Book(rs.getLong("ID"), author, rs.getString("TITLE"), rs.getString("SHORT_CONTENT"), new ArrayList<>());
            allBook.put(book.getId(), book);
        }
        return allBook;
    }
}
