package ru.otus.hw08.services;

import ru.otus.hw08.models.Book;

import java.util.List;
import java.util.Optional;

public interface BookService {
    void delete(String id);

    Book save(Book book);

    Optional<Book> findById(String id);

    List<Book> findAll();


}
