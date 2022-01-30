package ru.otus.hw05.dao;

import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw05.domain.Author;
import ru.otus.hw05.exceptions.DaoIntegrityViolationException;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;


@JdbcTest
@Import(AuthorDaoJdbc.class)
@DisplayName("Класс AuthorDao должен:")
class AuthorDaoJdbcTest {
    @Autowired
    private AuthorDaoJdbc authorDaoJdbc;
    private static final int EXPECTED_AUTHORS_COUNT = 3;
    private static final long AUTHOR_ID_NOT_EXIST = 100L;
    private static final long AUTHOR_ID_WITHOUT_BOOKS = 3L;
    private static final long AUTHOR_ID_WITH_BOOKS = 1L;
    private static final long AUTHOR_ID_UPDATING = 2L;
    private static final Author EXPECTED_AUTHOR_BY_ID = new Author(1L, "Александр", "Сергеевич", "Пушкин");
    private static final Author EXPECTED_CREATE_AUTHOR = new Author("Иван", "Петрович", "Сидоров");
    private static final List<Author> EXPECTED_AUTHORS_LIST = List.of(
            new Author(1L, "Александр", "Сергеевич", "Пушкин"),
            new Author(3L, "Александр", "Иванович", "Кузнецов"),
            new Author(2L, "Франклин", "Патрик", "Герберт")
    );

    @Test
    @DisplayName("Возвращать ожидаемое количество авторов")
    void countTest() {
        int actualCount = authorDaoJdbc.count();
        assertThat(actualCount).isEqualTo(EXPECTED_AUTHORS_COUNT);
    }

    @Test
    @DisplayName("Возвращать ожидаемого автора по ID")
    void getByIdTestSuccess() {
        val actualAuthor = authorDaoJdbc.getById(EXPECTED_AUTHOR_BY_ID.getId());
        assertThat(actualAuthor.isPresent()).isEqualTo(true);
        assertThat(actualAuthor.orElseThrow(NoSuchElementException::new)).usingRecursiveComparison().isEqualTo(EXPECTED_AUTHOR_BY_ID);
    }

    @Test
    @DisplayName("Возвращать пустое значение,если автора с заданным ID не существует")
    void getByIdTestFail() {
        val actualAuthor = authorDaoJdbc.getById(AUTHOR_ID_NOT_EXIST);
        assertThat(actualAuthor.isEmpty()).isEqualTo(true);
    }

    @Test
    @DisplayName("Удалять автора без книг по заданному ID")
    void deleteByIdSuccess() {
        var author = authorDaoJdbc.getById(AUTHOR_ID_WITHOUT_BOOKS);
        assertThat(author.isPresent()).isEqualTo(true);
        authorDaoJdbc.deleteById(AUTHOR_ID_WITHOUT_BOOKS);
        author = authorDaoJdbc.getById(AUTHOR_ID_WITHOUT_BOOKS);
        assertThat(author.isPresent()).isEqualTo(false);
    }

    @Test
    @DisplayName("Не удалять автора, если у него есть книги, выбрасывать исключение")
    void deleteByIdFail() {
        assertThatThrownBy(() -> authorDaoJdbc.deleteById(AUTHOR_ID_WITH_BOOKS)).isInstanceOf(DaoIntegrityViolationException.class);
        val author = authorDaoJdbc.getById(AUTHOR_ID_WITH_BOOKS);
        assertThat(author.isPresent()).isEqualTo(true);
    }


    @Test
    @DisplayName("Возвращать ожидаемый список авторов")
    void getAllTest() {
        List<Author> actualAuthors = authorDaoJdbc.getAll();
        assertThat(actualAuthors)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactlyInAnyOrderElementsOf(EXPECTED_AUTHORS_LIST);
    }


    @Test
    @DisplayName("Обновлять автора")
    void updateTest() {
        Author author = authorDaoJdbc.getById(AUTHOR_ID_UPDATING).orElseThrow(NoSuchElementException::new);
        Author expectedAuthor = new Author(AUTHOR_ID_UPDATING, author.getFirstName() + "!", author.getMiddleName() + "!!", author.getLastName() + "!!!");
        int updCnt = authorDaoJdbc.update(expectedAuthor);
        assertThat(updCnt).isEqualTo(1);
        Author actualAuthor = authorDaoJdbc.getById(AUTHOR_ID_UPDATING).orElseThrow(NoSuchElementException::new);
        assertThat(actualAuthor).usingRecursiveComparison().isEqualTo(expectedAuthor);
    }

    @Test
    @DisplayName("Добавлять автора в БД")
    void createTest() {
        long id = authorDaoJdbc.create(EXPECTED_CREATE_AUTHOR);
        assertThat(id).isNotEqualTo(0L);
        Author expectedAuthorWithId = new Author(id, EXPECTED_CREATE_AUTHOR.getFirstName(), EXPECTED_CREATE_AUTHOR.getMiddleName(), EXPECTED_CREATE_AUTHOR.getLastName());
        assertThat(authorDaoJdbc.getById(id).orElseThrow(NoSuchElementException::new)).usingRecursiveComparison().isEqualTo(expectedAuthorWithId);
    }
}