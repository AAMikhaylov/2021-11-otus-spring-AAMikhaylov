package ru.otus.hw06.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw06.io.IOMessageService;
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
@Import({GenreLibraryService.class, GenreServiceImpl.class, GenreRepositoryJpa.class})
@DisplayName("Класс GenreLibraryService должен:")
class GenreLibraryServiceTest {
    @Autowired
    private GenreLibraryService genreLibraryService;
    @MockBean
    IOMessageService ioMessageService;

    private final static List<String> printedStrings = new ArrayList<>();
    private final static List<String> EXPECTED_READ_ALL = List.of("1", "Роман", "2", "Фантастика", "3", "Без жанра");
    private static final String GENRE_ID_NOT_EXISTS = "100";
    private static final String GENRE_ID_WITHOUT_BOOKS = "9";
    private static final String GENRE_ID_WITH_BOOKS = "1";
    private static final String GENRE_ID_UPDATING = "3";
    private static final String GENRE_ID_WRONG = "1sd";

    private static final String MESSAGE_CREATE_GENRE_COMPLETE = "messages.genre.create.complete";
    private static final String MESSAGE_DELETE_GENRE_COMPLETE = "messages.genre.delete.complete";
    private static final String MESSAGE_UPDATE_GENRE_COMPLETE = "messages.genre.update.complete";
    private static final String MESSAGE_ENTER_GENRE_ID = "messages.genre.id";
    private static final String MESSAGE_CANCEL_OPERATION = "messages.cancelOperation";

    private static final List<String> EXPECTED_CREATED_GENRE = List.of( "genreName");
    private static final List<String> EXPECTED_WITH_BOOK_GENRE = List.of("1", "Фантастика");
    private static final List<String> EXPECTED_UPDATED_GENRE = List.of("3", "genreName");

    private static final String ERROR_NOT_FOUND = "messages.genre.read.error.notFound";
    private static final String ERROR_INTEGRITY_VIOLATION = "messages.genre.delete.error.integrityViolation";
    private static final String ERROR_GENRE_ID_FORMAT = "messages.genre.error.idFormat";


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
    @DisplayName("Считывать список жанров")
    void readAllGenresSuccessTest() {
        genreLibraryService.outputAllGenres();
        assertThat(printedStrings).containsAll(EXPECTED_READ_ALL);
    }

    @Test
    @DisplayName("Считывать жанр по ID")
    void readByIdGenreSuccessTest() {
        when(ioMessageService.read()).thenReturn(GENRE_ID_WITH_BOOKS);
        genreLibraryService.outputGenreById();
        assertThat(printedStrings).containsAll(EXPECTED_WITH_BOOK_GENRE);
    }

    @Test
    @DisplayName("Считывать жанр по ID. Ошибка, если жанра не существует")
    void readByIdGenreWrongTest() {
        when(ioMessageService.read()).thenReturn(GENRE_ID_NOT_EXISTS,"q");
        genreLibraryService.outputGenreById();
        assertThat(printedStrings).contains(ERROR_NOT_FOUND);
    }

    @Test
    @DisplayName("Создавать жанр")
    void createGenreSuccessTest() {
        when(ioMessageService.read()).thenReturn("genreName");
        genreLibraryService.createGenre();
        assertThat(printedStrings).contains(MESSAGE_CREATE_GENRE_COMPLETE);
        String newGenreId = printedStrings.get(printedStrings.size() - 1);
        when(ioMessageService.read()).thenReturn(newGenreId);
        printedStrings.clear();
        genreLibraryService.outputGenreById();
        assertThat(printedStrings).containsAll(EXPECTED_CREATED_GENRE);
    }

    @Test
    @DisplayName("Удалять жанр. Книги отсутствуют")
    void deleteGenreSuccessTest() {
        when(ioMessageService.read()).thenReturn(GENRE_ID_WITHOUT_BOOKS);
        genreLibraryService.deleteGenre();
        assertThat(printedStrings).contains(MESSAGE_DELETE_GENRE_COMPLETE);
        when(ioMessageService.read()).thenReturn(GENRE_ID_WITHOUT_BOOKS,"q");
        printedStrings.clear();
        genreLibraryService.outputGenreById();
        assertThat(printedStrings).contains(ERROR_NOT_FOUND);
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @DisplayName("Не удалять жанр. У жанра есть книги")
    void deleteGenreWithBookTest() {
        when(ioMessageService.read()).thenReturn(GENRE_ID_WITH_BOOKS);
        genreLibraryService.deleteGenre();
        assertThat(printedStrings).contains(ERROR_INTEGRITY_VIOLATION);
        when(ioMessageService.read()).thenReturn(GENRE_ID_WITH_BOOKS);
        printedStrings.clear();
        genreLibraryService.outputGenreById();
        assertThat(printedStrings).containsAll(EXPECTED_WITH_BOOK_GENRE);
    }

    @Test
    @DisplayName("Обновить жанр")
    void updateGenreSuccessTest() {
        when(ioMessageService.read()).thenReturn(GENRE_ID_UPDATING, "genreName");
        genreLibraryService.updateGenre();
        assertThat(printedStrings).contains(MESSAGE_UPDATE_GENRE_COMPLETE);
        when(ioMessageService.read()).thenReturn(GENRE_ID_UPDATING);
        printedStrings.clear();
        genreLibraryService.outputGenreById();
        assertThat(printedStrings).containsAll(EXPECTED_UPDATED_GENRE);
    }

    @Test
    @DisplayName("Обновить жанр. Завершение, если жанра не существует")
    void updateGenreWrongTest() {
        when(ioMessageService.read()).thenReturn(GENRE_ID_NOT_EXISTS, GENRE_ID_UPDATING, "genreName");
        genreLibraryService.updateGenre();
        assertThat(printedStrings).contains(ERROR_NOT_FOUND).contains(MESSAGE_UPDATE_GENRE_COMPLETE);
    }


    @Test
    @DisplayName("Обновить жанр. Повторный запрос ID жанра, если ID жанра некорректно")
    void updateGenreWrongIdTest() {
        when(ioMessageService.read()).thenReturn(GENRE_ID_WRONG, GENRE_ID_UPDATING, "genreName");
        genreLibraryService.updateGenre();

        assertThat(printedStrings).contains(ERROR_GENRE_ID_FORMAT);
        assertThat(printedStrings)
                .contains(ERROR_GENRE_ID_FORMAT)
                .contains(MESSAGE_UPDATE_GENRE_COMPLETE)
                .filteredOn(e -> e.equals(MESSAGE_ENTER_GENRE_ID)).hasSize(2);
        when(ioMessageService.read()).thenReturn(GENRE_ID_UPDATING);
        printedStrings.clear();
        genreLibraryService.outputGenreById();
        assertThat(printedStrings).containsAll(EXPECTED_UPDATED_GENRE);
    }

    @Test
    @DisplayName("Удалить жанр. Повторный запрос ID жанра, если ID жанра некорректно")
    void deleteGenreWrongIdTest() {
        when(ioMessageService.read()).thenReturn(GENRE_ID_WRONG, GENRE_ID_WITHOUT_BOOKS);
        genreLibraryService.deleteGenre();
        assertThat(printedStrings)
                .contains(ERROR_GENRE_ID_FORMAT)
                .contains(MESSAGE_DELETE_GENRE_COMPLETE)
                .filteredOn(e -> e.equals(MESSAGE_ENTER_GENRE_ID)).hasSize(2);
        when(ioMessageService.read()).thenReturn(GENRE_ID_WITHOUT_BOOKS,"q");
        printedStrings.clear();
        genreLibraryService.outputGenreById();
        assertThat(printedStrings).contains(ERROR_NOT_FOUND);
    }

    @Test
    @DisplayName("Считывать жанр по ID.  Повторный запрос ID жанра, если ID жанра некорректно")
    void readByIdGenreWrongIdTest() {
        when(ioMessageService.read()).thenReturn(GENRE_ID_WRONG, GENRE_ID_WITH_BOOKS);
        genreLibraryService.outputGenreById();
        assertThat(printedStrings)
                .contains(ERROR_GENRE_ID_FORMAT)
                .filteredOn(e -> e.equals(MESSAGE_ENTER_GENRE_ID)).hasSize(2);
        assertThat(printedStrings).containsAll(EXPECTED_WITH_BOOK_GENRE);
    }

    @Test
    @DisplayName("Создавать жанр. Отмена операции при вводе <q>")
    void createGenreCancelOperationTest() {
        when(ioMessageService.read()).thenReturn("q");
        genreLibraryService.createGenre();
        assertThat(printedStrings).contains(MESSAGE_CANCEL_OPERATION).hasSize(3);
    }

    @Test
    @DisplayName("Удалить жанр. Отмена операции при вводе <q>")
    void deleteGenreCancelOperationTest() {
        when(ioMessageService.read()).thenReturn("q");
        genreLibraryService.deleteGenre();
        assertThat(printedStrings).contains(MESSAGE_CANCEL_OPERATION).hasSize(3);
    }

    @Test
    @DisplayName("Обновить жанр. Отмена операции при вводе <q>")
    void updateGenreCancelOperationTest() {
        when(ioMessageService.read()).thenReturn("q");
        genreLibraryService.updateGenre();
        assertThat(printedStrings).contains(MESSAGE_CANCEL_OPERATION).hasSize(3);
        printedStrings.clear();
        when(ioMessageService.read()).thenReturn(GENRE_ID_WITH_BOOKS, "q");
        genreLibraryService.updateGenre();
        assertThat(printedStrings).contains(MESSAGE_CANCEL_OPERATION).hasSize(5);
    }
}

