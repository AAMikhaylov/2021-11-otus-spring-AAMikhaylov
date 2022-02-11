package ru.otus.hw06.services;

import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import ru.otus.hw06.io.IOMessageService;
import ru.otus.hw06.repositories.AuthorRepositoryJpa;
import ru.otus.hw06.repositories.BookRepositoryJpa;
import ru.otus.hw06.repositories.GenreRepositoryJpa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@DataJpaTest
@Import({BookLibraryService.class, BookServiceImpl.class,
        BookRepositoryJpa.class,
        AuthorLibraryService.class,
        AuthorServiceImpl.class,
        AuthorRepositoryJpa.class,
        GenreLibraryService.class,
        GenreServiceImpl.class,
        GenreRepositoryJpa.class

})
@DisplayName("Класс BookLibraryService должен:")
class BookLibraryServiceTest {
    @Autowired
    private BookLibraryService bookLibraryService;

    @MockBean
    IOMessageService ioMessageService;
    private final static List<String> printedStrings = new ArrayList<>();

    private final static List<String> EXPECTED_READ_ALL = List.of(
            "1", "А.С.Пушкин", "Евгений Онегин", "Роман", "Краткое содержание романа...",
            "2", "Ф.П.Герберт", "Дюна", "Фантастика, Приключения", "Действие «Дюны» происходит в далеком будущем – через 20 тысяч лет от условно нашего времени...."
    );
    private static final String BOOK_ID_READING = "1";
    private static final String BOOK_ID_DELETING = "2";
    private static final String BOOK_ID_UPDATING = "1";
    private static final String BOOK_ID_NOT_EXISTS = "100";
    private static final String AUTHOR_ID_NOT_EXISTS = "100";
    private static final String GENRE_ID_NOT_EXISTS = "100";
    private static final String AUTHOR_ID_WRONG = "ws";
    private static final String GENRE_ID_WRONG = "as";
    private static final String BOOK_ID_WRONG = "ws";

    private static final List<String> EXPECTED_READING_BOOK = List.of("1", "М.А.Булгаков", "Мастер и Маргарита", "Фантастика, Приключения", "Краткое содержание книги Мастер и Маргарита...");
    private static final List<String> EXPECTED_CREATED_BOOK = List.of("А.П.Чехов", "Book title", "Фантастика, Приключения", "short content");
    private static final List<String> EXPECTED_UPDATED_BOOK = List.of("1", "А.П.Чехов", "Book title", "Путешествия", "short content");
    private static final List<String> EXPECTED_AUTHORS = List.of("1", "Пушкин Александр Сергеевич", "2", "Герберт Франклин Патрик", "3", "Кузнецов Александр Иванович");
    private static final List<String> EXPECTED_GENRES = List.of("1", "Роман", "2", "Фантастика", "3", "Без жанра");

    private static final String MESSAGE_ENTER_AUTHOR_ID = "messages.author.id";
    private static final String MESSAGE_ENTER_GENRE_ID = "messages.book.genreId";
    private static final String MESSAGE_ENTER_BOOK_ID = "messages.book.id";
    private static final String MESSAGE_CANCEL_OPERATION = "messages.cancelOperation";
    private static final String MESSAGE_CREATE_BOOK_COMPLETE = "messages.book.create.complete";
    private static final String MESSAGE_DELETE_BOOK_COMPLETE = "messages.book.delete.complete";
    private static final String MESSAGE_UPDATE_BOOK_COMPLETE = "messages.book.update.complete";


    private static final String ERROR_BOOK_NOT_FOUND = "messages.book.read.error.notFound";
    private static final String ERROR_AUTHOR_NOT_FOUND = "messages.author.read.error.notFound";
    private static final String ERROR_GENRE_NOT_FOUND = "messages.genre.read.error.notFound";
    private static final String ERROR_AUTHOR_ID_FORMAT = "messages.author.error.idFormat";
    private static final String ERROR_GENRE_ID_FORMAT = "messages.genre.error.idFormat";
    private static final String ERROR_BOOK_ID_FORMAT = "messages.book.error.idFormat";


    @BeforeEach
    void setUp() {
        printedStrings.clear();
        doAnswer(invocationOnMock -> {
            List<Object> args = Arrays.asList(invocationOnMock.getArguments());
            args.forEach((e) -> printedStrings.add(e.toString().trim()));
            return null;
        }).when(ioMessageService).writeLocal(anyString(), any());
    }


    @Test
    @DisplayName("Считывать ожидаемый список книг")
    void readAllBooksTest() {
        bookLibraryService.outputAllBooks();
        assertThat(printedStrings).containsAll(EXPECTED_READ_ALL);
    }

    @Test
    @DisplayName("Считывать книгу по ID")
    void readByIdEBookSuccessTest() {
        when(ioMessageService.read()).thenReturn(BOOK_ID_READING);
        bookLibraryService.outputBookById();
        assertThat(printedStrings).containsAll(EXPECTED_READING_BOOK);
    }

    @Test
    @DisplayName("Считывать книгу по ID. Ошибка, если книги не существует")
    void readByIdBookWrongTest() {
        when(ioMessageService.read()).thenReturn(BOOK_ID_NOT_EXISTS,"q");
        bookLibraryService.outputBookById();
        assertThat(printedStrings).contains(ERROR_BOOK_NOT_FOUND);
    }

    @Test
    @DisplayName("Считывать книгу по ID. Повторный запрос ID при неверном формате ID книги")
    void readByIdBookWrongIdTest() {
        when(ioMessageService.read()).thenReturn(BOOK_ID_WRONG, BOOK_ID_READING);
        bookLibraryService.outputBookById();
        assertThat(printedStrings)
                .contains(ERROR_BOOK_ID_FORMAT)
                .filteredOn(e -> e.equals(MESSAGE_ENTER_BOOK_ID)).hasSize(2);
        assertThat(printedStrings).containsAll(EXPECTED_READING_BOOK);
    }


    @Test
    @DisplayName("Создавать книгу")
    void createBookSuccessTest() {
        when(ioMessageService.read()).thenReturn("Book title", "3", "1", "2", "f", "short content");
        bookLibraryService.createBook();
        assertThat(printedStrings).contains(MESSAGE_CREATE_BOOK_COMPLETE);
        String newBookId = printedStrings.get(printedStrings.size() - 1);
        when(ioMessageService.read()).thenReturn(newBookId);
        printedStrings.clear();
        bookLibraryService.outputBookById();
        List<String> expectedBook = new ArrayList<>(EXPECTED_CREATED_BOOK);
        expectedBook.add(newBookId);
        assertThat(printedStrings).containsAll(expectedBook);
    }

    @Test
    @DisplayName("Создавать книгу. Повторный запрос ID автора, если автор не существует")
    void createBookWithWrongAuthorTest() {
        when(ioMessageService.read()).thenReturn("Book title", AUTHOR_ID_NOT_EXISTS, "3", "1", "2", "f", "short content");
        bookLibraryService.createBook();
        assertThat(printedStrings)
                .contains(ERROR_AUTHOR_NOT_FOUND)
                .contains(MESSAGE_CREATE_BOOK_COMPLETE)
                .filteredOn(e -> e.equals(MESSAGE_ENTER_AUTHOR_ID)).hasSize(2);
        val bookId = printedStrings.get(printedStrings.size() - 1);
        when(ioMessageService.read()).thenReturn(bookId);
        printedStrings.clear();
        bookLibraryService.outputBookById();
        List<String> expectedBook = new ArrayList<>(EXPECTED_CREATED_BOOK);
        expectedBook.add(bookId);
        assertThat(printedStrings).containsAll(expectedBook);

    }

    @Test
    @DisplayName("Создавать книгу. Повторный запрос ID жанра, если жанра не существует")
    void createBookWithWrongGenreTest() {
        when(ioMessageService.read()).thenReturn("Book title", "3", GENRE_ID_NOT_EXISTS, "1", "2", "f", "short content");
        bookLibraryService.createBook();
        assertThat(printedStrings)
                .contains(ERROR_GENRE_NOT_FOUND)
                .contains(MESSAGE_CREATE_BOOK_COMPLETE)
                .filteredOn(e -> e.equals(MESSAGE_ENTER_GENRE_ID)).hasSize(4);
        val bookId = printedStrings.get(printedStrings.size() - 1);
        when(ioMessageService.read()).thenReturn(bookId);
        printedStrings.clear();
        List<String> expectedBook = new ArrayList<>(EXPECTED_CREATED_BOOK);
        bookLibraryService.outputBookById();
        expectedBook.add(bookId);
        assertThat(printedStrings).containsAll(expectedBook);
    }


    @Test
    @DisplayName("Создавать книгу. Повторный запрос ID автора, если ID автора некорректно")
    void createBookWithWrongAuthorIdTest() {
        when(ioMessageService.read()).thenReturn("Book title", AUTHOR_ID_WRONG, "3", "1", "2", "f", "short content");
        bookLibraryService.createBook();
        assertThat(printedStrings)
                .contains(ERROR_AUTHOR_ID_FORMAT)
                .contains(MESSAGE_CREATE_BOOK_COMPLETE)
                .filteredOn(e -> e.equals(MESSAGE_ENTER_AUTHOR_ID)).hasSize(2);
        val bookId = printedStrings.get(printedStrings.size() - 1);
        when(ioMessageService.read()).thenReturn(bookId);
        printedStrings.clear();
        bookLibraryService.outputBookById();
        List<String> expectedBook = new ArrayList<>(EXPECTED_CREATED_BOOK);
        expectedBook.add(bookId);
        assertThat(printedStrings).containsAll(expectedBook);

    }

    @Test
    @DisplayName("Создавать книгу. Повторный запрос ID жанра, если ID жанра некорректно")
    void createBookWithWrongGenreIdTest() {
        when(ioMessageService.read()).thenReturn("Book title", "3", GENRE_ID_WRONG, "1", "2", "f", "short content");
        bookLibraryService.createBook();
        assertThat(printedStrings)
                .contains(ERROR_GENRE_ID_FORMAT)
                .contains(MESSAGE_CREATE_BOOK_COMPLETE)
                .filteredOn(e -> e.equals(MESSAGE_ENTER_GENRE_ID)).hasSize(4);
        val bookId = printedStrings.get(printedStrings.size() - 1);
        when(ioMessageService.read()).thenReturn(bookId);
        printedStrings.clear();
        bookLibraryService.outputBookById();
        List<String> expectedBook = new ArrayList<>(EXPECTED_CREATED_BOOK);
        expectedBook.add(bookId);
        assertThat(printedStrings).containsAll(expectedBook);

    }

    @Test
    @DisplayName("Создавать книгу. Отмена операции при вводе <q>")
    void createBookCancelOperationTest() {
        when(ioMessageService.read()).thenReturn("q");
        bookLibraryService.createBook();
        assertThat(printedStrings).contains(MESSAGE_CANCEL_OPERATION).hasSize(3);
        when(ioMessageService.read()).thenReturn("Book title", "q");
        printedStrings.clear();
        bookLibraryService.createBook();
        assertThat(printedStrings).contains(MESSAGE_CANCEL_OPERATION).hasSize(4);
        when(ioMessageService.read()).thenReturn("Book title", "3", "1", "2", "f", "q");
        printedStrings.clear();
        bookLibraryService.createBook();
        assertThat(printedStrings).contains(MESSAGE_CANCEL_OPERATION).hasSize(11);
    }

    @Test
    @DisplayName("Удалять книгу")
    void deleteBookSuccessTest() {
        when(ioMessageService.read()).thenReturn(BOOK_ID_DELETING);
        bookLibraryService.deleteBook();
        assertThat(printedStrings).contains(MESSAGE_DELETE_BOOK_COMPLETE);
        when(ioMessageService.read()).thenReturn(BOOK_ID_DELETING,"q");
        printedStrings.clear();
        bookLibraryService.outputBookById();
        assertThat(printedStrings).contains(ERROR_BOOK_NOT_FOUND);
    }

    @Test
    @DisplayName("Удалять книгу. Повторный запрос ID книги, если ID книги некорректно")
    void deleteBookWrongIdTest() {
        when(ioMessageService.read()).thenReturn(BOOK_ID_WRONG, BOOK_ID_DELETING);
        bookLibraryService.deleteBook();
        assertThat(printedStrings)
                .contains(ERROR_BOOK_ID_FORMAT)
                .contains(MESSAGE_DELETE_BOOK_COMPLETE)
                .filteredOn(e -> e.equals(MESSAGE_ENTER_BOOK_ID)).hasSize(2);
        when(ioMessageService.read()).thenReturn(BOOK_ID_DELETING,"q");
        printedStrings.clear();
        bookLibraryService.outputBookById();
        assertThat(printedStrings).contains(ERROR_BOOK_NOT_FOUND);
    }

    @Test
    @DisplayName("Удалить книгу. Отмена операции при вводе <q>")
    void deleteBookCancelOperationTest() {
        when(ioMessageService.read()).thenReturn("q");
        bookLibraryService.deleteBook();
        assertThat(printedStrings)
                .contains(MESSAGE_CANCEL_OPERATION)
                .hasSize(3);
    }


    @Test
    @DisplayName("Обновить книгу")
    void updateBookSuccessTest() {
        when(ioMessageService.read()).thenReturn(BOOK_ID_UPDATING, "Book title", "y", "3", "y", "3", "f", "short content");
        bookLibraryService.updateBook();
        assertThat(printedStrings).contains(MESSAGE_UPDATE_BOOK_COMPLETE);
        when(ioMessageService.read()).thenReturn(BOOK_ID_UPDATING);
        printedStrings.clear();
        bookLibraryService.outputBookById();
        assertThat(printedStrings).containsAll(EXPECTED_UPDATED_BOOK);
    }


    @Test
    @DisplayName("Обновить книгу. Повторный запрос ID автора, если автор не существует")
    void updateBookWithWrongAuthorTest() {
        when(ioMessageService.read()).thenReturn(BOOK_ID_UPDATING, "Book title", "y", AUTHOR_ID_NOT_EXISTS, "3", "y", "3", "f", "short content");
        bookLibraryService.updateBook();
        assertThat(printedStrings)
                .contains(ERROR_AUTHOR_NOT_FOUND)
                .contains(MESSAGE_UPDATE_BOOK_COMPLETE)
                .filteredOn(e -> e.equals(MESSAGE_ENTER_AUTHOR_ID)).hasSize(2);
        when(ioMessageService.read()).thenReturn(BOOK_ID_UPDATING);
        printedStrings.clear();
        bookLibraryService.outputBookById();
        assertThat(printedStrings).containsAll(EXPECTED_UPDATED_BOOK);
    }

    @Test
    @DisplayName("Обновить книгу. Повторный запрос ID жанра, если жанр не существует")
    void updateBookWithWrongGenreTest() {
        when(ioMessageService.read()).thenReturn(BOOK_ID_UPDATING, "Book title", "y", "3", "y", GENRE_ID_NOT_EXISTS, "3", "f", "short content");
        bookLibraryService.updateBook();
        assertThat(printedStrings)
                .contains(ERROR_GENRE_NOT_FOUND)
                .contains(MESSAGE_UPDATE_BOOK_COMPLETE)
                .filteredOn(e -> e.equals(MESSAGE_ENTER_GENRE_ID)).hasSize(3);
        when(ioMessageService.read()).thenReturn(BOOK_ID_UPDATING);
        printedStrings.clear();
        bookLibraryService.outputBookById();
        assertThat(printedStrings).containsAll(EXPECTED_UPDATED_BOOK);
    }


    @Test
    @DisplayName("Обновить книгу. Повторный запрос ID автора, если ID автора некорректно")
    void updateBookWithWrongAuthorIdTest() {
        when(ioMessageService.read()).thenReturn(BOOK_ID_UPDATING, "Book title", "y", AUTHOR_ID_WRONG, "3", "y", "3", "f", "short content");
        bookLibraryService.updateBook();
        assertThat(printedStrings)
                .contains(ERROR_AUTHOR_ID_FORMAT)
                .contains(MESSAGE_UPDATE_BOOK_COMPLETE)
                .filteredOn(e -> e.equals(MESSAGE_ENTER_AUTHOR_ID)).hasSize(2);
        when(ioMessageService.read()).thenReturn(BOOK_ID_UPDATING);
        printedStrings.clear();
        bookLibraryService.outputBookById();
        assertThat(printedStrings).containsAll(EXPECTED_UPDATED_BOOK);
    }

    @Test
    @DisplayName("Обновить книгу. Повторный запрос ID жанра, если ID жанра некорректно")
    void updateBookWithWrongGenreIdTest() {
        when(ioMessageService.read()).thenReturn(BOOK_ID_UPDATING, "Book title", "y", "3", "y", GENRE_ID_WRONG, "3", "f", "short content");
        bookLibraryService.updateBook();
        assertThat(printedStrings)
                .contains(ERROR_GENRE_ID_FORMAT)
                .contains(MESSAGE_UPDATE_BOOK_COMPLETE)
                .filteredOn(e -> e.equals(MESSAGE_ENTER_GENRE_ID)).hasSize(3);
        when(ioMessageService.read()).thenReturn(BOOK_ID_UPDATING);
        printedStrings.clear();
        bookLibraryService.outputBookById();
        assertThat(printedStrings).containsAll(EXPECTED_UPDATED_BOOK);
    }

    @Test
    @DisplayName("Обновить книгу. Завершение, если книги не существует")
    void updateBookWrongTest() {
        when(ioMessageService.read()).thenReturn(BOOK_ID_NOT_EXISTS, BOOK_ID_UPDATING, "Book title", "y", "3", "short content", "y", "3", "f");
        bookLibraryService.updateBook();
        assertThat(printedStrings).contains(ERROR_BOOK_NOT_FOUND).contains(MESSAGE_UPDATE_BOOK_COMPLETE);
    }


    @Test
    @DisplayName("Обновить книгу. Повторный запрос ID книги, если ID книги некорректно")
    void updateBookWrongIdTest() {
        when(ioMessageService.read()).thenReturn(BOOK_ID_WRONG, BOOK_ID_UPDATING, "Book title", "y", "3", "y", "3", "f", "short content");
        bookLibraryService.updateBook();
        assertThat(printedStrings).contains(ERROR_BOOK_ID_FORMAT);
        assertThat(printedStrings)
                .contains(ERROR_BOOK_ID_FORMAT)
                .contains(MESSAGE_UPDATE_BOOK_COMPLETE)
                .filteredOn(e -> e.equals(MESSAGE_ENTER_BOOK_ID)).hasSize(2);
        when(ioMessageService.read()).thenReturn(BOOK_ID_UPDATING);
        printedStrings.clear();
        bookLibraryService.outputBookById();
        assertThat(printedStrings).containsAll(EXPECTED_UPDATED_BOOK);
    }

    @Test
    @DisplayName("Обновить книгу. Оставить книгу без изменений, если введены пустые строки и <n>")
    void updateBookWithSameTest() {
        when(ioMessageService.read()).thenReturn(BOOK_ID_READING, "", "n", "n", "");
        bookLibraryService.updateBook();
        when(ioMessageService.read()).thenReturn(BOOK_ID_READING);
        printedStrings.clear();
        bookLibraryService.outputBookById();
        assertThat(printedStrings).containsAll(EXPECTED_READING_BOOK);
    }

    @Test
    @DisplayName("Обновить книгу. Выводить списки доступных авторов и жанров при вводе <l>")
    void updateBookListsAuthorsAndGenresTest() {
        when(ioMessageService.read()).thenReturn(BOOK_ID_READING, "", "y", "l", "1", "y", "l", "1", "f", "");
        bookLibraryService.updateBook();
        assertThat(printedStrings).containsAll(EXPECTED_AUTHORS);
        assertThat(printedStrings).containsAll(EXPECTED_GENRES);
    }

    @Test
    @DisplayName("Обновить книгу. Отмена операции при вводе <q>")
    void updateBookCancelOperationTest() {
        when(ioMessageService.read()).thenReturn("q");
        bookLibraryService.updateBook();
        assertThat(printedStrings).contains(MESSAGE_CANCEL_OPERATION).hasSize(3);


        when(ioMessageService.read()).thenReturn(BOOK_ID_READING, "q");
        printedStrings.clear();
        bookLibraryService.updateBook();
        assertThat(printedStrings).contains(MESSAGE_CANCEL_OPERATION).hasSize(5);


        when(ioMessageService.read()).thenReturn(BOOK_ID_READING, "Book title", "y", "q");
        printedStrings.clear();
        bookLibraryService.updateBook();
        assertThat(printedStrings).contains(MESSAGE_CANCEL_OPERATION).hasSize(8);

        when(ioMessageService.read()).thenReturn(BOOK_ID_READING, "Book title", "y", "1", "y", "1", "f", "q");
        printedStrings.clear();
        bookLibraryService.updateBook();
        assertThat(printedStrings).contains(MESSAGE_CANCEL_OPERATION).hasSize(17);

    }


}

