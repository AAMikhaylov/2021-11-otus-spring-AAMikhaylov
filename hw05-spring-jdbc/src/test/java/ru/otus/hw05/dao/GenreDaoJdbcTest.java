package ru.otus.hw05.dao;

import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw05.domain.Genre;
import ru.otus.hw05.exceptions.DaoIntegrityViolationException;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
@Import(GenreDaoJdbc.class)
@DisplayName("Класс GenreDao должен:")
class GenreDaoJdbcTest {
    @Autowired
    private GenreDaoJdbc genreDaoJdbc;
    private static final int EXPECTED_GENRES_COUNT = 3;
    private static final long GENRE_ID_NOT_EXIST = 100L;
    private static final long GENRE_ID_WITHOUT_BOOKS = 3L;
    private static final long GENRE_ID_WITH_BOOKS = 1L;
    private static final long GENRE_ID_UPDATING = 2L;

    private static final Genre EXPECTED_GENRE_BY_ID = new Genre(1L, "Роман");
    private static final Genre EXPECTED_CREATE_GENRE = new Genre("Testing genre");
    private static final List<Genre> EXPECTED_GENRES_LIST = List.of(
            new Genre(1L, "Роман"),
            new Genre(2L, "Фантастика"),
            new Genre(3L, "Без жанра")
    );

    @Test
    @DisplayName("Возвращать ожидаемое количество жанров")
    void countTest() {
        int actualCount = genreDaoJdbc.count();
        assertThat(actualCount).isEqualTo(EXPECTED_GENRES_COUNT);
    }

    @Test
    @DisplayName("Возвращать ожидаемый жанр по ID")
    void getByIdTestSuccess() {
        val actualAuthor = genreDaoJdbc.getById(EXPECTED_GENRE_BY_ID.getId());
        assertThat(actualAuthor.isPresent()).isEqualTo(true);
        assertThat(actualAuthor.orElseThrow(NoSuchElementException::new)).usingRecursiveComparison().isEqualTo(EXPECTED_GENRE_BY_ID);
    }

    @Test
    @DisplayName("Возвращать пустое значение,если жанр с заданным ID не существует")
    void getByIdTestFail() {
        val actualAuthor = genreDaoJdbc.getById(GENRE_ID_NOT_EXIST);
        assertThat(actualAuthor.isEmpty()).isEqualTo(true);
    }

    @Test
    @DisplayName("Удалять жанр без книг по заданному ID")
    void deleteByIdSuccess() {
        var author = genreDaoJdbc.getById(GENRE_ID_WITHOUT_BOOKS);
        assertThat(author.isPresent()).isEqualTo(true);
        genreDaoJdbc.deleteById(GENRE_ID_WITHOUT_BOOKS);
        author = genreDaoJdbc.getById(GENRE_ID_WITHOUT_BOOKS);
        assertThat(author.isPresent()).isEqualTo(false);
    }

    @Test
    @DisplayName("Не удалять жанр, если у него есть книги, выбрасывать исключение")
    void deleteByIdFail() {
        assertThatThrownBy(() -> genreDaoJdbc.deleteById(GENRE_ID_WITH_BOOKS)).isInstanceOf(DaoIntegrityViolationException.class);
        val author = genreDaoJdbc.getById(GENRE_ID_WITH_BOOKS);
        assertThat(author.isPresent()).isEqualTo(true);
    }


    @Test
    @DisplayName("Возвращать ожидаемый список жанров")
    void getAllTest() {
        List<Genre> actualAuthors = genreDaoJdbc.getAll();
        assertThat(actualAuthors)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactlyInAnyOrderElementsOf(EXPECTED_GENRES_LIST);
    }


    @Test
    @DisplayName("Обновлять жанр")
    void updateTest() {
        Genre genre = genreDaoJdbc.getById(GENRE_ID_UPDATING).orElseThrow(NoSuchElementException::new);
        Genre expectedGenre = new Genre(GENRE_ID_UPDATING, genre.getName() + "_test");
        int updCnt = genreDaoJdbc.update(expectedGenre);
        assertThat(updCnt).isEqualTo(1);
        Genre actualGenre = genreDaoJdbc.getById(GENRE_ID_UPDATING).orElseThrow(NoSuchElementException::new);
        assertThat(actualGenre).usingRecursiveComparison().isEqualTo(expectedGenre);
    }

    @Test
    @DisplayName("Добавлять жанр в БД")
    void createTest() {
        long id = genreDaoJdbc.create(EXPECTED_CREATE_GENRE);
        assertThat(id).isNotEqualTo(0L);
        Genre expectedGenreWithId = new Genre(id, EXPECTED_CREATE_GENRE.getName());
        assertThat(genreDaoJdbc.getById(id).orElseThrow(NoSuchElementException::new)).usingRecursiveComparison().isEqualTo(expectedGenreWithId);
    }

}