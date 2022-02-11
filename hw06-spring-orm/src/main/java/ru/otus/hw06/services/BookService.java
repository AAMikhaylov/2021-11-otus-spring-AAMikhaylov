package ru.otus.hw06.services;

import ru.otus.hw06.models.Book;

import java.util.List;
import java.util.Optional;

public interface BookService {
    void delete(long id);
    Book save(Book book);

    Optional<Book> findByIdLazy(long id);

    Optional<Book> findByIdEager(long id);

    List<Book> findAll();


}
