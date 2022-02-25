package ru.otus.hw07.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw07.io.IOMessageService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@DataJpaTest
@Import({AuthorLibraryService.class, AuthorServiceImpl.class})
@DisplayName("Класс AuthorLibraryService должен:")
class AuthorLibraryServiceTest {
    @Autowired
    private AuthorLibraryService authorLibraryService;
    @MockBean
    IOMessageService ioMessageService;
    private final static List<String> printedStrings = new ArrayList<>();
    private final static List<String> EXPECTED_READ_ALL = List.of("1", "Пушкин Александр Сергеевич",
            "2", "Герберт Франклин Патрик",
            "3", "Кузнецов Александр Иванович"
    );
    private static final String AUTHOR_ID_NOT_EXISTS = "100";
    private static final String AUTHOR_ID_WITHOUT_BOOKS = "3";
    private static final String AUTHOR_ID_WITH_BOOKS = "1";
    private static final String AUTHOR_ID_UPDATING = "3";
    private static final String AUTHOR_ID_WRONG = "1sd";


    private static final String MESSAGE_CREATE_AUTHOR_COMPLETE = "messages.author.create.complete";
    private static final String MESSAGE_DELETE_AUTHOR_COMPLETE = "messages.author.delete.complete";
    private static final String MESSAGE_UPDATE_AUTHOR_COMPLETE = "messages.author.update.complete";
    private static final String MESSAGE_ENTER_AUTHOR_ID = "messages.author.id";
    private static final String MESSAGE_CANCEL_OPERATION = "messages.cancelOperation";

    private static final List<String> EXPECTED_CREATED_AUTHOR = List.of("lastName firstName middleName");
    private static final List<String> EXPECTED_WITH_BOOK_AUTHOR = List.of("1", "Пушкин Александр Сергеевич");
    private static final List<String> EXPECTED_UPDATED_AUTHOR = List.of("3", "lastName firstName middleName");

    private static final String ERROR_NOT_FOUND = "messages.author.read.error.notFound";
    private static final String ERROR_INTEGRITY_VIOLATION = "messages.author.delete.error.integrityViolation";
    private static final String ERROR_AUTHOR_ID_FORMAT = "messages.author.error.idFormat";


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
    @DisplayName("Считывать список авторов")
    void readAllAuthorsSuccessTest() {
        authorLibraryService.outputAllAuthors();
        assertThat(printedStrings).containsAll(EXPECTED_READ_ALL);
    }

    @Test
    @DisplayName("Считывать автора по ID")
    void readByIdAuthorSuccessTest() {
        when(ioMessageService.read()).thenReturn(AUTHOR_ID_WITH_BOOKS);
        authorLibraryService.outputAuthorById();
        assertThat(printedStrings).containsAll(EXPECTED_WITH_BOOK_AUTHOR);
    }

    @Test
    @DisplayName("Считывать автора по ID. Ошибка, если автора не существует")
    void readByIdAuthorWrongTest() {
        when(ioMessageService.read()).thenReturn(AUTHOR_ID_NOT_EXISTS, "q");
        authorLibraryService.outputAuthorById();
        assertThat(printedStrings).contains(ERROR_NOT_FOUND);
    }

    @Test
    @DisplayName("Создавать автора")
    void createAuthorSuccessTest() {
        when(ioMessageService.read()).thenReturn("firstName", "middleName", "lastName");
        authorLibraryService.createAuthor();
        assertThat(printedStrings).contains(MESSAGE_CREATE_AUTHOR_COMPLETE);
        String newAuthorId = printedStrings.get(printedStrings.size() - 1);
        when(ioMessageService.read()).thenReturn(newAuthorId);
        printedStrings.clear();
        authorLibraryService.outputAuthorById();
        assertThat(printedStrings).containsAll(EXPECTED_CREATED_AUTHOR);
    }

    @Test
    @DisplayName("Удалять автора. Книги отсутствуют")
    void deleteAuthorSuccessTest() {
        when(ioMessageService.read()).thenReturn(AUTHOR_ID_WITHOUT_BOOKS);
        authorLibraryService.deleteAuthor();
        assertThat(printedStrings).contains(MESSAGE_DELETE_AUTHOR_COMPLETE);
        when(ioMessageService.read()).thenReturn(AUTHOR_ID_WITHOUT_BOOKS, "q");
        printedStrings.clear();
        authorLibraryService.outputAuthorById();
        assertThat(printedStrings).contains(ERROR_NOT_FOUND);
    }

    @Test
    @DisplayName("Не удалять автора. У автора есть книги")
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void deleteAuthorWithBookTest() {
        when(ioMessageService.read()).thenReturn(AUTHOR_ID_WITH_BOOKS);
        authorLibraryService.deleteAuthor();
        assertThat(printedStrings).contains(ERROR_INTEGRITY_VIOLATION);
        when(ioMessageService.read()).thenReturn(AUTHOR_ID_WITH_BOOKS);
        printedStrings.clear();
        authorLibraryService.outputAuthorById();
        assertThat(printedStrings).containsAll(EXPECTED_WITH_BOOK_AUTHOR);
    }

    @Test
    @DisplayName("Обновить автора")
    void updateAuthorSuccessTest() {
        when(ioMessageService.read()).thenReturn(AUTHOR_ID_UPDATING, "firstName", "middleName", "lastName");
        authorLibraryService.updateAuthor();
        assertThat(printedStrings).contains(MESSAGE_UPDATE_AUTHOR_COMPLETE);
        when(ioMessageService.read()).thenReturn(AUTHOR_ID_UPDATING);
        printedStrings.clear();
        authorLibraryService.outputAuthorById();
        assertThat(printedStrings).containsAll(EXPECTED_UPDATED_AUTHOR);
    }


    @Test
    @DisplayName("Обновить автора. Завершение, если автора не существует")
    void updateAuthorWrongTest() {
        when(ioMessageService.read()).thenReturn(AUTHOR_ID_NOT_EXISTS, AUTHOR_ID_UPDATING, "genreName");
        authorLibraryService.updateAuthor();
        assertThat(printedStrings).contains(ERROR_NOT_FOUND).contains(MESSAGE_UPDATE_AUTHOR_COMPLETE);
    }


    @Test
    @DisplayName("Обновить автора. Повторный запрос ID автора, если ID автора некорректно")
    void updateAuthorWrongIdTest() {
        when(ioMessageService.read()).thenReturn(AUTHOR_ID_WRONG, AUTHOR_ID_UPDATING, "firstName", "middleName", "lastName");
        authorLibraryService.updateAuthor();

        assertThat(printedStrings).contains(ERROR_AUTHOR_ID_FORMAT);
        assertThat(printedStrings)
                .contains(ERROR_AUTHOR_ID_FORMAT)
                .contains(MESSAGE_UPDATE_AUTHOR_COMPLETE)
                .filteredOn(e -> e.equals(MESSAGE_ENTER_AUTHOR_ID)).hasSize(2);
        when(ioMessageService.read()).thenReturn(AUTHOR_ID_UPDATING);
        printedStrings.clear();
        authorLibraryService.outputAuthorById();
        assertThat(printedStrings).containsAll(EXPECTED_UPDATED_AUTHOR);
    }

    @Test
    @DisplayName("Удалить автора. Повторный запрос ID автора, если ID автора некорректно")
    void deleteAuthorWrongIdTest() {
        when(ioMessageService.read()).thenReturn(AUTHOR_ID_WRONG, AUTHOR_ID_WITHOUT_BOOKS);
        authorLibraryService.deleteAuthor();
        assertThat(printedStrings)
                .contains(ERROR_AUTHOR_ID_FORMAT)
                .contains(MESSAGE_DELETE_AUTHOR_COMPLETE)
                .filteredOn(e -> e.equals(MESSAGE_ENTER_AUTHOR_ID)).hasSize(2);
        when(ioMessageService.read()).thenReturn(AUTHOR_ID_WITHOUT_BOOKS, "q");
        printedStrings.clear();
        authorLibraryService.outputAuthorById();
        assertThat(printedStrings).contains(ERROR_NOT_FOUND);
    }

    @Test
    @DisplayName("Считывать автора по ID.  Повторный запрос ID автора, если ID автора некорректно")
    void readByIdAuthorWrongIdTest() {
        when(ioMessageService.read()).thenReturn(AUTHOR_ID_WRONG, AUTHOR_ID_WITH_BOOKS);
        authorLibraryService.outputAuthorById();
        assertThat(printedStrings)
                .contains(ERROR_AUTHOR_ID_FORMAT)
                .filteredOn(e -> e.equals(MESSAGE_ENTER_AUTHOR_ID)).hasSize(2);
        assertThat(printedStrings).containsAll(EXPECTED_WITH_BOOK_AUTHOR);
    }

    @Test
    @DisplayName("Создавать автора. Отмена операции при вводе <q>")
    void createAuthorCancelOperationTest() {
        when(ioMessageService.read()).thenReturn("q");
        authorLibraryService.createAuthor();
        assertThat(printedStrings).contains(MESSAGE_CANCEL_OPERATION).hasSize(3);
        printedStrings.clear();
        when(ioMessageService.read()).thenReturn("first name", "q");
        authorLibraryService.createAuthor();
        assertThat(printedStrings).contains(MESSAGE_CANCEL_OPERATION).hasSize(4);
        printedStrings.clear();
        when(ioMessageService.read()).thenReturn("first name", "middle name", "q");
        authorLibraryService.createAuthor();
        assertThat(printedStrings).contains(MESSAGE_CANCEL_OPERATION).hasSize(5);
    }

    @Test
    @DisplayName("Удалить автора. Отмена операции при вводе <q>")
    void deleteAuthorCancelOperationTest() {
        when(ioMessageService.read()).thenReturn("q");
        authorLibraryService.deleteAuthor();
        assertThat(printedStrings).contains(MESSAGE_CANCEL_OPERATION).hasSize(3);
    }

    @Test
    @DisplayName("Обновить автора. Отмена операции при вводе <q>")
    void updateAuthorCancelOperationTest() {
        when(ioMessageService.read()).thenReturn("q");
        authorLibraryService.updateAuthor();
        assertThat(printedStrings).contains(MESSAGE_CANCEL_OPERATION).hasSize(3);
        printedStrings.clear();
        when(ioMessageService.read()).thenReturn(AUTHOR_ID_WITH_BOOKS, "q");
        authorLibraryService.updateAuthor();
        assertThat(printedStrings).contains(MESSAGE_CANCEL_OPERATION).hasSize(5);
        printedStrings.clear();
        when(ioMessageService.read()).thenReturn(AUTHOR_ID_WITH_BOOKS, "first name", "q");
        authorLibraryService.updateAuthor();
        assertThat(printedStrings).contains(MESSAGE_CANCEL_OPERATION).hasSize(7);
        printedStrings.clear();
        when(ioMessageService.read()).thenReturn(AUTHOR_ID_WITH_BOOKS, "first name", "middle name", "q");
        authorLibraryService.updateAuthor();
        assertThat(printedStrings).contains(MESSAGE_CANCEL_OPERATION).hasSize(9);
    }

}