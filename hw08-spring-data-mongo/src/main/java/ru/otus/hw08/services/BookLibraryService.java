package ru.otus.hw08.services;

import ru.otus.hw08.models.Book;

import java.util.*;

public interface BookLibraryService {
    void createBook();

    void outputAllBooks();

    void outputBookById();

    void updateBook();

    void deleteBook();

    Optional<Book> selectBook();

}
