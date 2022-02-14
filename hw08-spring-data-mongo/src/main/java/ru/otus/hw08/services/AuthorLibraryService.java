package ru.otus.hw08.services;

import ru.otus.hw08.models.Author;

import java.util.Optional;

public interface AuthorLibraryService {
    void createAuthor();

    void outputAuthorById();


    void outputAllAuthors();

    void updateAuthor();

    void deleteAuthor();

    Optional<Author> selectAuthor();

}






