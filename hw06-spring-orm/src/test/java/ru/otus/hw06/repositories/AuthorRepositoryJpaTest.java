package ru.otus.hw06.repositories;

import lombok.val;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw06.models.Author;

import javax.persistence.PersistenceException;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@Import(AuthorRepositoryJpa.class)
@DisplayName("Класс AuthorRepositoryJpa должен:")
class AuthorRepositoryJpaTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private AuthorRepositoryJpa authorRepJpa;

    private static final int EXPECTED_AUTHORS_COUNT = 6;
    private static final long AUTHOR_ID_NOT_EXIST = 100L;
    private static final long AUTHOR_ID_WITHOUT_BOOKS = 3L;
    private static final long AUTHOR_ID_WITH_BOOKS = 1L;
    private static final long AUTHOR_ID_UPDATING = 2L;
    private static final int EXPECTED_QUERIES_COUNT_FINDALL = 1;
    private static final int EXPECTED_QUERIES_COUNT_FINDBYID = 1;

    @Test
    @DisplayName("Возвращать ожидаемое количество авторов")
    void countTest() {
        long actualCount = authorRepJpa.count();
        assertThat(actualCount).isEqualTo(EXPECTED_AUTHORS_COUNT);
    }

    @Test
    @DisplayName("Возвращать пустое значение,если автора с заданным ID не существует")
    void getByIdTestFail() {
        val actualAuthor = authorRepJpa.findById(AUTHOR_ID_NOT_EXIST);
        assertThat(actualAuthor.isEmpty()).isEqualTo(true);
    }

    @Test
    @DisplayName("Удалять автора без книг по заданному ID")
    void deleteByIdSuccess() {
        val author = em.find(Author.class,AUTHOR_ID_WITHOUT_BOOKS);
        assertThat(author).isNotNull();
        authorRepJpa.deleteById(AUTHOR_ID_WITHOUT_BOOKS);
        em.flush();
        em.clear();
        val deletedAuthor = em.find(Author.class, AUTHOR_ID_WITHOUT_BOOKS);
        assertThat(deletedAuthor).isNull();
    }

    @Test
    @DisplayName("Возвращать ожидаемого автора по ID")
    void getByIdTestSuccess() {
        val actualAuthorOpt = authorRepJpa.findById(AUTHOR_ID_WITH_BOOKS);
        assertThat(actualAuthorOpt.isPresent()).isEqualTo(true);
        val expectedAuthor = em.find(Author.class, AUTHOR_ID_WITH_BOOKS);
        assertThat(actualAuthorOpt.orElseThrow(NoSuchElementException::new)).usingRecursiveComparison().isEqualTo(expectedAuthor);
    }


    @Test
    @DisplayName("Не удалять автора, если у него есть книги, выбрасывать PersistenceException")
    void deleteByIdFail() {
        val author = em.find(Author.class,AUTHOR_ID_WITH_BOOKS);
        assertThat(author).isNotNull();
        authorRepJpa.deleteById(AUTHOR_ID_WITH_BOOKS);
        assertThatThrownBy(() -> em.flush()).isInstanceOf(PersistenceException.class);
    }

    @Test
    @DisplayName("Возвращать список авторов c полной информацией")
    void getAllTest() {
        List<Author> actualAuthors = authorRepJpa.findAll();
        assertThat(actualAuthors)
                .hasSize(EXPECTED_AUTHORS_COUNT)
                .allMatch(s -> !s.getFirstName().isEmpty())
                .allMatch(s -> !s.getLastName().isEmpty())
                .allMatch(s -> s.getId() != 0L);
    }

    @Test
    @DisplayName("Формировать ожидаемое количество запросов к БД при загрузке всех авторов")
    void getAllQueryCountTest() {
        SessionFactory sessionFactory = em.getEntityManager().getEntityManagerFactory()
                .unwrap(SessionFactory.class);
        sessionFactory.getStatistics().setStatisticsEnabled(true);
        sessionFactory.getStatistics().clear();
        authorRepJpa.findAll();
        assertThat(sessionFactory.getStatistics().getPrepareStatementCount()).isEqualTo(EXPECTED_QUERIES_COUNT_FINDALL);
    }

    @Test
    @DisplayName("Формировать ожидаемое количество запросов к БД при загрузке автора по ID")
    void getByIdQueryCountTest() {
        SessionFactory sessionFactory = em.getEntityManager().getEntityManagerFactory()
                .unwrap(SessionFactory.class);
        sessionFactory.getStatistics().setStatisticsEnabled(true);
        sessionFactory.getStatistics().clear();
        authorRepJpa.findById(AUTHOR_ID_WITH_BOOKS);
        assertThat(sessionFactory.getStatistics().getPrepareStatementCount()).isEqualTo(EXPECTED_QUERIES_COUNT_FINDBYID);
    }

    @Test
    @DisplayName("Обновлять автора")
    void updateTest() {
        val author = em.find(Author.class,AUTHOR_ID_UPDATING);
        val updatedAuthor = new Author(AUTHOR_ID_UPDATING, author.getFirstName() + "!", author.getMiddleName() + "!!", author.getLastName() + "!!!");
        authorRepJpa.save(updatedAuthor);
        em.flush();
        em.clear();
        val actualAuthor = em.find(Author.class, AUTHOR_ID_UPDATING);
        assertThat(actualAuthor).usingRecursiveComparison().isEqualTo(updatedAuthor);
    }

    @Test
    @DisplayName("Добавлять автора в БД")
    void createTest() {
        val newAuthor = new Author("firstName", "middleName", "lastName");
        val savedAuthor = authorRepJpa.save(newAuthor);
        val expectedAuthor = new Author(savedAuthor.getId(), newAuthor.getFirstName(), newAuthor.getMiddleName(), newAuthor.getLastName());
        em.flush();
        em.clear();
        val actualAuthor = em.find(Author.class, savedAuthor.getId());
        assertThat(actualAuthor).usingRecursiveComparison().isEqualTo(expectedAuthor);
    }


}
