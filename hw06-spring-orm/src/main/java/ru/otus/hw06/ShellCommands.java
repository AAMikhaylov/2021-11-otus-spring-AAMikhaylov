package ru.otus.hw06;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw06.services.AuthorLibraryService;
import ru.otus.hw06.services.BookLibraryService;
import ru.otus.hw06.services.CommentLibraryService;
import ru.otus.hw06.services.GenreLibraryService;

@ShellComponent
@RequiredArgsConstructor
public class ShellCommands {
    private final AuthorLibraryService authorLibraryService;
    private final GenreLibraryService genreLibraryService;
    private final BookLibraryService bookLibraryService;
    private final CommentLibraryService commentLibraryService;
//    private final BookService bookService;

    @ShellMethod(value = "Create new author", key = {"ca"})
    void addAuthor() {
        authorLibraryService.createAuthor();
    }

    @ShellMethod(value = "Read all authors", key = {"ra"})
    void showAllAuthors() {
        authorLibraryService.outputAllAuthors();
    }

    @ShellMethod(value = "Read author by id", key = {"rai"})
    void showAuthorById() {
        authorLibraryService.outputAuthorById();
    }


    @ShellMethod(value = "Update author", key = {"ua"})
    void updateAuthor() {
        authorLibraryService.updateAuthor();
    }

    @ShellMethod(value = "Delete author", key = {"da"})
    void deleteAuthor() {
        authorLibraryService.deleteAuthor();
    }

    @ShellMethod(value = "Create new genre", key = {"cg"})
    void addGenre() {
        genreLibraryService.createGenre();
    }

    @ShellMethod(value = "Read all genres", key = {"rg"})
    void showAllGenre() {
        genreLibraryService.outputAllGenres();
    }

    @ShellMethod(value = "Update genre", key = {"ug"})
    void updateGenre() {
        genreLibraryService.updateGenre();
    }

    @ShellMethod(value = "Delete genre", key = {"dg"})
    void deleteGenre() {
        genreLibraryService.deleteGenre();
    }

    @ShellMethod(value = "Read all books", key = {"rb"})
    void showAllBooks() {
        bookLibraryService.outputAllBooks();
    }

    @ShellMethod(value = "Read book by id", key = {"rbi"})
    void showBookById() {
        bookLibraryService.outputBookById();
    }


    @ShellMethod(value = "Delete book", key = {"db"})
    void deleteBook() {
        bookLibraryService.deleteBook();
    }

    @ShellMethod(value = "Create new book", key = {"cb"})
    void createBook() {
        bookLibraryService.createBook();
    }

    @ShellMethod(value = "Update  book", key = {"ub"})
    void updateBook() {
        bookLibraryService.updateBook();
    }

    @ShellMethod(value = "Read all comments", key = {"rc"})
    void showAllComments() {
        commentLibraryService.outputAllComments();
    }

    @ShellMethod(value = "Read comment by id", key = {"rci"})
    void showCommentById() {
        commentLibraryService.outputCommentById();
    }

    @ShellMethod(value = "Delete comment", key = {"dc"})
    void deleteComment() {
        commentLibraryService.deleteComment();
    }

    @ShellMethod(value = "Create new comment", key = {"cc"})
    void addComment() {
        commentLibraryService.createComment();
    }

    @ShellMethod(value = "Update comment", key = {"uc"})
    void updateComment() {
        commentLibraryService.updateComment();
    }

}
