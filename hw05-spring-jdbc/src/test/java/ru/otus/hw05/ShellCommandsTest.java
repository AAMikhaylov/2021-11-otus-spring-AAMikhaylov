package ru.otus.hw05;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.shell.Shell;
import ru.otus.hw05.services.AuthorService;
import ru.otus.hw05.services.BookService;
import ru.otus.hw05.services.GenreService;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@SpringBootTest(classes = {ShellCommands.class})
@EnableAutoConfiguration
@DisplayName("Класс ShellCommands должен:")
class ShellCommandsTest {
    @Autowired
    private Shell shell;

    @MockBean
    private AuthorService authorService;
    @MockBean
    private GenreService genreService;
    @MockBean
    private BookService bookService;


    private static final String COMMAND_CREATE_AUTHOR = "ca";
    private static final String COMMAND_CREATE_BOOK = "cb";
    private static final String COMMAND_CREATE_GENRE = "cg";
    private static final String COMMAND_DELETE_AUTHOR = "da";
    private static final String COMMAND_DELETE_BOOK = "db";
    private static final String COMMAND_DELETE_GENRE = "dg";
    private static final String COMMAND_READ_ALL_AUTHORS = "ra";
    private static final String COMMAND_READ_BY_ID_AUTHOR = "rai";
    private static final String COMMAND_READ_ALL_BOOKS = "rb";
    private static final String COMMAND_READ_BOOK_BY_ID = "rbi";
    private static final String COMMAND_READ_ALL_GENRES = "rg";
    private static final String COMMAND_UPDATE_AUTHOR = "ua";
    private static final String COMMAND_UPDATE_BOOK = "ub";
    private static final String COMMAND_UPDATE_GENRE = "ug";

    @Test
    @DisplayName("Запускать команду добавления нового автора")
    void addAuthor() {
        shell.evaluate(() -> COMMAND_CREATE_AUTHOR);
        verify(authorService, times(1)).create();
    }

    @Test
    @DisplayName("Запускать команду вывода всех авторов")
    void showAllAuthors() {
        shell.evaluate(() -> COMMAND_READ_ALL_AUTHORS);
        verify(authorService, times(1)).readAll();
    }

    @Test
    @DisplayName("Запускать команду вывода автора по ID")
    void showAuthorById() {
        shell.evaluate(() -> COMMAND_READ_BY_ID_AUTHOR);
        verify(authorService, times(1)).readById();
    }

    @Test
    @DisplayName("Запускать команду обновления автора")
    void updateAuthor() {
        shell.evaluate(() -> COMMAND_UPDATE_AUTHOR);
        verify(authorService, times(1)).update();
    }

    @Test
    @DisplayName("Запускать команду удаления автора")
    void deleteAuthor() {
        shell.evaluate(() -> COMMAND_DELETE_AUTHOR);
        verify(authorService, times(1)).delete();
    }

    @Test
    @DisplayName("Запускать команду добавления жанра")
    void addGenre() {
        shell.evaluate(() -> COMMAND_CREATE_GENRE);
        verify(genreService, times(1)).create();
    }

    @Test
    @DisplayName("Запускать команду вывода всех жанров")
    void showAllGenre() {
        shell.evaluate(() -> COMMAND_READ_ALL_GENRES);
        verify(genreService, times(1)).readAll();
    }

    @Test
    @DisplayName("Запускать команду обновления жанра")
    void updateGenre() {
        shell.evaluate(() -> COMMAND_UPDATE_GENRE);
        verify(genreService, times(1)).update();
    }

    @Test
    @DisplayName("Запускать команду удаления жанра")
    void deleteGenre() {
        shell.evaluate(() -> COMMAND_DELETE_GENRE);
        verify(genreService, times(1)).delete();

    }

    @Test
    @DisplayName("Запускать команду вывода всех книг")
    void showAllBooks() {
        shell.evaluate(() -> COMMAND_READ_ALL_BOOKS);
        verify(bookService, times(1)).readAll();
    }


    @Test
    @DisplayName("Запускать команду вывода книги по ID")
    void showBookById() {
        shell.evaluate(() -> COMMAND_READ_BOOK_BY_ID);
        verify(bookService, times(1)).readById();

    }

    @Test
    @DisplayName("Запускать команду удаления книги")
    void deleteBook() {
        shell.evaluate(() -> COMMAND_DELETE_BOOK);
        verify(bookService, times(1)).delete();

    }

    @Test
    @DisplayName("Запускать команду добавления книги")
    void createBook() {
        shell.evaluate(() -> COMMAND_CREATE_BOOK);
        verify(bookService, times(1)).create();

    }

    @Test
    @DisplayName("Запускать команду обновления книги")
    void updateBook() {
        shell.evaluate(() -> COMMAND_UPDATE_BOOK);
        verify(bookService, times(1)).update();

    }
}