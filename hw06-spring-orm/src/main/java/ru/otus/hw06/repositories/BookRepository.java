package ru.otus.hw06.repositories;

import ru.otus.hw06.models.Book;

import java.util.List;
import java.util.Optional;

public interface BookRepository {

    long count();

    Book save(Book book);

    Optional<Book> findById(long id);

    List<Book> findAll();

    void deleteById(long id);

}
