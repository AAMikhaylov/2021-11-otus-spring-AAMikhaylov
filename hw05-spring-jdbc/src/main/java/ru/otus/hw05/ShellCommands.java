package ru.otus.hw05;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw05.services.AuthorService;
import ru.otus.hw05.services.BookService;
import ru.otus.hw05.services.GenreService;


@ShellComponent
@RequiredArgsConstructor
public class ShellCommands {
    private final AuthorService authorService;
    private final GenreService genreService;
    private final BookService bookService;

    @ShellMethod(value = "Create new author", key = {"ca"})
    void addAuthor() {
        authorService.create();
    }

    @ShellMethod(value = "Read all authors", key = {"ra"})
    void showAllAuthors() {
        authorService.readAll();
    }
    @ShellMethod(value = "Read author by id", key = {"rai"})
    void showAuthorById() {
        authorService.readById();
    }


    @ShellMethod(value = "Update author", key = {"ua"})
    void updateAuthor() {
        authorService.update();
    }

    @ShellMethod(value = "Delete author", key = {"da"})
    void deleteAuthor() {
        authorService.delete();
    }

    @ShellMethod(value = "Create new genre", key = {"cg"})
    void addGenre() {
        genreService.create();
    }

    @ShellMethod(value = "Read all genres", key = {"rg"})
    void showAllGenre() {
        genreService.readAll();
    }

    @ShellMethod(value = "Update genre", key = {"ug"})
    void updateGenre() {
        genreService.update();
    }

    @ShellMethod(value = "Delete genre", key = {"dg"})
    void deleteGenre() {
        genreService.delete();
    }

    @ShellMethod(value = "Read all books", key = {"rb"})
    void showAllBooks() {
        bookService.readAll();
    }

    @ShellMethod(value = "Read book by id", key = {"rbi"})
    void showBookById() {
        bookService.readById();
    }


    @ShellMethod(value = "Delete book", key = {"db"})
    void deleteBook() {
        bookService.delete();
    }

    @ShellMethod(value = "Create new book", key = {"cb"})
    void createBook() {
        bookService.create();
    }

    @ShellMethod(value = "Update  book", key = {"ub"})
    void updateBook() {
        bookService.update();
    }

}
