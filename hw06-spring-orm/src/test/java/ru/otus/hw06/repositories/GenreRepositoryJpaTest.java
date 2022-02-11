package ru.otus.hw06.repositories;

import lombok.val;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw06.models.Genre;

import javax.persistence.PersistenceException;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import(GenreRepositoryJpa.class)
@DisplayName("Класс GenreRepositoryJpa должен:")
class GenreRepositoryJpaTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private GenreRepositoryJpa genreRepJpa;
    private static final int EXPECTED_GENRES_COUNT = 9;
    private static final long GENRE_ID_NOT_EXIST = 100L;
    private static final long GENRE_ID_WITHOUT_BOOKS = 3L;
    private static final long GENRE_ID_WITH_BOOKS = 1L;
    private static final long GENRE_ID_UPDATING = 2L;
    private static final int EXPECTED_QUERIES_COUNT_FINDALL = 1;
    private static final int EXPECTED_QUERIES_COUNT_FINDBYID = 1;
    @Test
    @DisplayName("Возвращать ожидаемое количество жанров")
    void countTest() {
        long actualCount = genreRepJpa.count();
        assertThat(actualCount).isEqualTo(EXPECTED_GENRES_COUNT);
    }

    @Test
    @DisplayName("Возвращать пустое значение,если жанра с заданным ID не существует")
    void getByIdTestFail() {
        val actualGenre = genreRepJpa.findById(GENRE_ID_NOT_EXIST);
        assertThat(actualGenre.isEmpty()).isEqualTo(true);
    }

    @Test
    @DisplayName("Удалять жанр без книг по заданному ID")
    void deleteByIdSuccess() {
        var genre = em.find(Genre.class,GENRE_ID_WITHOUT_BOOKS);
        assertThat(genre).isNotNull();
        genreRepJpa.deleteById(GENRE_ID_WITHOUT_BOOKS);
        em.flush();
        em.clear();
        val deletedGenre = em.find(Genre.class, GENRE_ID_WITHOUT_BOOKS);
        assertThat(deletedGenre).isNull();
    }

    @Test
    @DisplayName("Возвращать ожидаемый жанр по ID")
    void getByIdTestSuccess() {
        val actualGenreOpt = genreRepJpa.findById(GENRE_ID_WITH_BOOKS);
        assertThat(actualGenreOpt.isPresent()).isEqualTo(true);
        val expectedGenre = em.find(Genre.class, GENRE_ID_WITH_BOOKS);
        assertThat(actualGenreOpt.orElseThrow(NoSuchElementException::new)).usingRecursiveComparison().isEqualTo(expectedGenre);
    }


    @Test
    @DisplayName("Не удалять жанр, если у него есть книги, выбрасывать PersistenceException")
    void deleteByIdFail() {
        val genre = em.find(Genre.class,GENRE_ID_WITH_BOOKS);
        assertThat(genre).isNotNull();
        genreRepJpa.deleteById(GENRE_ID_WITH_BOOKS);
        assertThatThrownBy(() -> em.flush()).isInstanceOf(PersistenceException.class);
    }

    @Test
    @DisplayName("Возвращать список жанров c полной информацией")
    void getAllTest() {
        List<Genre> actualGenres = genreRepJpa.findAll();
        assertThat(actualGenres)
                .hasSize(EXPECTED_GENRES_COUNT)
                .allMatch(s -> !s.getName().isEmpty())
                .allMatch(s -> s.getId() != 0L);
    }

    @Test
    @DisplayName("Формировать ожидаемое количество запросов к БД при загрузке всех жанров")
    void getAllQueryCountTest() {
        SessionFactory sessionFactory = em.getEntityManager().getEntityManagerFactory()
                .unwrap(SessionFactory.class);
        sessionFactory.getStatistics().setStatisticsEnabled(true);
        sessionFactory.getStatistics().clear();
        genreRepJpa.findAll();
        assertThat(sessionFactory.getStatistics().getPrepareStatementCount()).isEqualTo(EXPECTED_QUERIES_COUNT_FINDALL);
    }

    @Test
    @DisplayName("Формировать ожидаемое количество запросов к БД при загрузке жанра по ID")
    void getByIdQueryCountTest() {
        SessionFactory sessionFactory = em.getEntityManager().getEntityManagerFactory()
                .unwrap(SessionFactory.class);
        sessionFactory.getStatistics().setStatisticsEnabled(true);
        sessionFactory.getStatistics().clear();
        genreRepJpa.findById(GENRE_ID_WITH_BOOKS);
        assertThat(sessionFactory.getStatistics().getPrepareStatementCount()).isEqualTo(EXPECTED_QUERIES_COUNT_FINDBYID);
    }

    @Test
    @DisplayName("Обновлять жанр")
    void updateTest() {
        val genre = em.find(Genre.class,GENRE_ID_UPDATING);
        val updatedGenre = new Genre(GENRE_ID_UPDATING, genre.getName() + "!");
        genreRepJpa.save(updatedGenre);
        em.flush();
        em.clear();
        val actualGenre = em.find(Genre.class, GENRE_ID_UPDATING);
        assertThat(actualGenre).usingRecursiveComparison().isEqualTo(updatedGenre);
    }

    @Test
    @DisplayName("Добавлять жанр в БД")
    void createTest() {
        val newGenre = new Genre("genreName");
        val savedGenre = genreRepJpa.save(newGenre);
        em.flush();
        em.clear();
        val actualGenre = em.find(Genre.class, savedGenre.getId());
        val expectedGenre = new Genre(savedGenre.getId(), newGenre.getName());
        assertThat(actualGenre).usingRecursiveComparison().isEqualTo(expectedGenre);
    }
}