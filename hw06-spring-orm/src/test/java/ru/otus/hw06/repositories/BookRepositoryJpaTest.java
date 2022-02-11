package ru.otus.hw06.repositories;

import lombok.val;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw06.models.Author;
import ru.otus.hw06.models.Book;
import ru.otus.hw06.models.Comment;
import ru.otus.hw06.models.Genre;

import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@DataJpaTest
@Import(BookRepositoryJpa.class)
@DisplayName("Класс BookRepositoryJpa должен:")
class BookRepositoryJpaTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private BookRepositoryJpa bookRepJpa;
    private static final int EXPECTED_BOOKS_COUNT = 5;
    private static final long BOOK_ID_NOT_EXIST = 100L;
    private static final long BOOK_ID_DELETING = 2L;
    private static final long BOOK_ID_UPDATING = 1L;
    private static final long BOOK_ID_READING = 3L;
    private static final int EXPECTED_QUERIES_COUNT_FINDALL = 1;
    private static final int EXPECTED_QUERIES_COUNT_FINDBYID = 1;
    private static final long AUTHOR_ID_WITHOUT_BOOKS = 3L;
    private static final long AUTHOR_ID_WITH_BOOKS = 3L;
    private static final long GENRE_ID_WITHOUT_BOOKS = 9L;
    private static final long GENRE_ID_WITH_BOOKS = 2L;


    @Test
    @DisplayName("Возвращать ожидаемое количество книг")
    void countTest() {
        long actualCount = bookRepJpa.count();
        assertThat(actualCount).isEqualTo(EXPECTED_BOOKS_COUNT);
    }

    @Test
    @DisplayName("Возвращать пустое значение,если книги с заданным ID не существует")
    void getByIdTestFail() {
        val actualBook = bookRepJpa.findById(BOOK_ID_NOT_EXIST);
        assertThat(actualBook.isEmpty()).isEqualTo(true);
    }

    @Test
    @DisplayName("Удалять книгу по заданному ID со всеми комментариями и не удалять жанры")
    void deleteByIdSuccess() {
        val bookOpt = bookRepJpa.findById(BOOK_ID_DELETING);
        assertThat(bookOpt.isPresent()).isEqualTo(true);
        val book = bookOpt.orElseThrow(NoSuchElementException::new);
        bookRepJpa.deleteById(BOOK_ID_DELETING);
        em.flush();
        em.clear();
        val deletedBook = em.find(Book.class, BOOK_ID_DELETING);
        assertThat(deletedBook).isNull();
        book.getComments().forEach(c -> assertThat(em.find(Comment.class, c.getId())).isNull());
    }

    @Test
    @DisplayName("Возвращать ожидаемую книгу по ID")
    void getByIdTestSuccess() {
        val actualBookOpt = bookRepJpa.findById(BOOK_ID_READING);
        assertThat(actualBookOpt.isPresent()).isEqualTo(true);
        val expectedBook = em.find(Book.class, BOOK_ID_READING);
        assertThat(actualBookOpt.orElseThrow(NoSuchElementException::new)).usingRecursiveComparison().isEqualTo(expectedBook);
    }


    @Test
    @DisplayName("Возвращать список книг c полной информацией")
    void getAllTest() {
        List<Book> actualBooks = bookRepJpa.findAll();
        assertThat(actualBooks)
                .hasSize(EXPECTED_BOOKS_COUNT)
                .allMatch(b -> b.getId() != 0L)
                .allMatch(b -> b.getGenres().size() > 0)
                .allMatch(b -> !b.getTitle().isEmpty())
                .allMatch(b -> b.getAuthor() != null)
                .allMatch(b -> !b.getShortContent().isEmpty());
    }

    @Test
    @DisplayName("Формировать ожидаемое количество запросов к БД при загрузке всех книг")
    void getAllQueryCountTest() {
        SessionFactory sessionFactory = em.getEntityManager().getEntityManagerFactory()
                .unwrap(SessionFactory.class);
        sessionFactory.getStatistics().setStatisticsEnabled(true);
        sessionFactory.getStatistics().clear();
        bookRepJpa.findAll();
        assertThat(sessionFactory.getStatistics().getPrepareStatementCount()).isEqualTo(EXPECTED_QUERIES_COUNT_FINDALL);
    }

    @Test
    @DisplayName("Формировать ожидаемое количество запросов к БД при загрузке книги по ID")
    void getByIdQueryCountTest() {
        SessionFactory sessionFactory = em.getEntityManager().getEntityManagerFactory()
                .unwrap(SessionFactory.class);
        sessionFactory.getStatistics().setStatisticsEnabled(true);
        sessionFactory.getStatistics().clear();
        bookRepJpa.findById(BOOK_ID_READING);
        assertThat(sessionFactory.getStatistics().getPrepareStatementCount()).isEqualTo(EXPECTED_QUERIES_COUNT_FINDBYID);
    }

    //
    @Test
    @DisplayName("Обновлять книгу")
    void updateTest() {
        val book = em.find(Book.class, BOOK_ID_UPDATING);
        val newAuthor = em.find(Author.class, AUTHOR_ID_WITH_BOOKS);
        List<Genre> newGenres = new ArrayList<>();
        newGenres.add(em.find(Genre.class, GENRE_ID_WITH_BOOKS));
        newGenres.add(em.find(Genre.class, GENRE_ID_WITHOUT_BOOKS));
        val updatedBook = new Book(BOOK_ID_UPDATING, newAuthor, "testTitle", "testShortContent", newGenres, book.getComments());
        bookRepJpa.save(updatedBook);
        em.flush();
        em.clear();
        val actualBook = em.find(Book.class, BOOK_ID_UPDATING);
        assertThat(actualBook).usingRecursiveComparison().ignoringFields("comments").isEqualTo(updatedBook);
    }

    @Test
    @DisplayName("Не обновлять книгу и бросать PersistenceException, если автора нет в БД")
    void updateWithNotExistAuthorTest() {
        val book = em.find(Book.class, BOOK_ID_UPDATING);
        val newAuthor = em.find(Author.class, AUTHOR_ID_WITHOUT_BOOKS);
        List<Genre> newGenres = new ArrayList<>();
        newGenres.add(em.find(Genre.class, GENRE_ID_WITH_BOOKS));
        newGenres.add(em.find(Genre.class, GENRE_ID_WITHOUT_BOOKS));
        val updatedBook = new Book(BOOK_ID_UPDATING, newAuthor, "testTitle", "testShortContent", newGenres, book.getComments());
        em.remove(newAuthor);
        em.flush();
        em.clear();
        assertThatThrownBy(() -> bookRepJpa.save(updatedBook)).isInstanceOf(PersistenceException.class);
    }

    @Test
    @DisplayName("Не обновлять книгу и бросать PersistenceException, если жанра нет в БД")
    void updateWithNotExistGenreTest() {
        val book = em.find(Book.class, BOOK_ID_UPDATING);
        val newAuthor = em.find(Author.class, AUTHOR_ID_WITH_BOOKS);
        List<Genre> newGenres = new ArrayList<>();
        newGenres.add(em.find(Genre.class, GENRE_ID_WITH_BOOKS));
        newGenres.add(em.find(Genre.class, GENRE_ID_WITHOUT_BOOKS));
        val updatedBook = new Book(BOOK_ID_UPDATING, newAuthor, "testTitle", "testShortContent", newGenres, book.getComments());
        em.remove(em.find(Genre.class, GENRE_ID_WITHOUT_BOOKS));
        em.flush();
        em.clear();
        assertThatThrownBy(() -> bookRepJpa.save(updatedBook)).isInstanceOf(PersistenceException.class);
    }

    @Test
    @DisplayName("Создавать новую книгу с указанными автором и  жанрами")
    void createBookTest() {
        val author = em.find(Author.class, AUTHOR_ID_WITH_BOOKS);
        List<Genre> genres = new ArrayList<>();
        genres.add(em.find(Genre.class, GENRE_ID_WITH_BOOKS));
        genres.add(em.find(Genre.class, GENRE_ID_WITHOUT_BOOKS));
        val book = new Book(author, "testTitle", "testShortContent", genres, new ArrayList<>());
        val savedBook = bookRepJpa.save(book);
        val expectedBook = new Book(savedBook.getId(), savedBook.getAuthor(), savedBook.getTitle(), savedBook.getShortContent(), savedBook.getGenres(), savedBook.getComments());
        em.flush();
        em.clear();
        val actualBook = em.find(Book.class, savedBook.getId());
        assertThat(actualBook).usingRecursiveComparison().isEqualTo(expectedBook);

    }

    @Test
    @DisplayName("Бросать ожидаемое исключение при попытке добавить книгу с отсутствующим в БД автором")
    void createBookWithNotExistAuthorTest() {
        val author = em.find(Author.class, AUTHOR_ID_WITHOUT_BOOKS);
        List<Genre> genres = new ArrayList<>();
        genres.add(em.find(Genre.class, GENRE_ID_WITH_BOOKS));
        genres.add(em.find(Genre.class, GENRE_ID_WITHOUT_BOOKS));
        val book = new Book(author, "testTitle", "testShortContent", genres, new ArrayList<>());
        em.remove(author);
        em.flush();
        em.clear();
        assertThatThrownBy(() -> bookRepJpa.save(book)).isInstanceOf(PersistenceException.class);
    }

    @Test
    @DisplayName("Бросать ожидаемое исключение при попытке добавить книгу с отсутствующим в БД жанром")
    void createBookWithNotExistGenreTest() {
        val author = em.find(Author.class, AUTHOR_ID_WITHOUT_BOOKS);
        List<Genre> genres = new ArrayList<>();
        genres.add(em.find(Genre.class, GENRE_ID_WITH_BOOKS));
        genres.add(em.find(Genre.class, GENRE_ID_WITHOUT_BOOKS));
        val book = new Book(author, "testTitle", "testShortContent", genres, new ArrayList<>());
        em.remove(em.find(Genre.class, GENRE_ID_WITHOUT_BOOKS));
        em.flush();
        em.clear();
        bookRepJpa.save(book);
        assertThatThrownBy(() -> em.flush()).isInstanceOf(PersistenceException.class);
    }
}