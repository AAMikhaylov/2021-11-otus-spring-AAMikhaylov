package ru.otus.hw05.dao;

import ru.otus.hw05.domain.Book;

import java.util.List;
import java.util.Optional;

public interface BookDao {

    int count();

    long create(Book book);

    Optional<Book> getById(long id);

    List<Book> getAll();

    int update(Book book);

    int deleteById(long id);
}
