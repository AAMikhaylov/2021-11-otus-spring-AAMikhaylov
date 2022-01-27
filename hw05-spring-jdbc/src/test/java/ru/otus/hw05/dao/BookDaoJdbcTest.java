package ru.otus.hw05.dao;

import lombok.val;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw05.domain.Author;
import ru.otus.hw05.domain.Book;
import ru.otus.hw05.domain.Genre;
import ru.otus.hw05.exceptions.DaoIntegrityViolationException;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@JdbcTest
@Import({BookDaoJdbc.class, GenreDaoJdbc.class})
@DisplayName("Класс BookDao должен:")
class BookDaoJdbcTest {
    @Autowired
    private BookDaoJdbc bookDaoJdbc;


    private static final int EXPECTED_BOOKS_COUNT = 2;
    private static final long BOOK_ID_DELETING = 2L;
    private static final long BOOK_ID_UPDATING = 1L;
    private static final long AUTHOR_ID_NOT_EXIST = 100L;
    private static final long GENRE_ID_NOT_EXIST = 100L;
    private static final long BOOK_ID_NOT_EXIST = 100L;


    private static final Book EXPECTED_BOOK_BY_ID = new Book(
            2,
            new Author(2, "Франклин", "Патрик", "Герберт"),
            "Дюна", "Действие «Дюны» происходит в далеком будущем – через 20 тысяч лет от условно нашего времени....",
            List.of(new Genre(2, "Фантастика"), new Genre(1, "Роман"))
    );
    private static final Book EXPECTED_CREATING_BOOK = new Book(
            new Author(3, "Александр", "Иванович", "Кузнецов"),
            "Test title",
            "test short content",
            List.of(new Genre(2, "Фантастика"), new Genre(1, "Роман"), new Genre(3, "Без жанра"))
    );

    private static final List<Book> EXPECTED_BOOKS_LIST = List.of(
            new Book(1,
                    new Author(1, "Александр", "Сергеевич", "Пушкин"),
                    "Евгений Онегин", "Краткое содержание романа...",
                    List.of(new Genre(1, "Роман"))
            ),
            EXPECTED_BOOK_BY_ID
    );

    private final Author UPDATED_BOOK_AUTHOR = new Author(3, "Александр", "Иванович", "Кузнецов");
    private final List<Genre> UPDATED_BOOK_GENRES = List.of(new Genre(2, "Фантастика"), new Genre(3, "Без жанра"));
    private final Book UPDATED_BOOK = new Book(UPDATED_BOOK_AUTHOR, "test title", "test content", UPDATED_BOOK_GENRES);

    @Test
    @DisplayName("Возвращать ожидаемое количество книг")
    void countTest() {
        int actualCount = bookDaoJdbc.count();
        assertThat(actualCount).isEqualTo(EXPECTED_BOOKS_COUNT);
    }

    @Test
    @DisplayName("Возвращать ожидаемую книгу по ID")
    void getByIdTestSuccess() {
        val actualBook = bookDaoJdbc.getById(EXPECTED_BOOK_BY_ID.getId());
        assertThat(actualBook.isPresent()).isEqualTo(true);
        assertThat(actualBook.orElseThrow(NoSuchElementException::new))
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(EXPECTED_BOOK_BY_ID);
    }


    @Test
    @DisplayName("Возвращать пустое значение, если книги с заданным ID не существует")
    void getByIdTestFail() {
        val actualBook = bookDaoJdbc.getById(BOOK_ID_NOT_EXIST);
        assertThat(actualBook.isEmpty()).isEqualTo(true);
    }

    @Test
    @DisplayName("Удалять книгу по заданному ID")
    void deleteById() {
        var book = bookDaoJdbc.getById(BOOK_ID_DELETING);
        assertThat(book.isPresent()).isEqualTo(true);
        bookDaoJdbc.deleteById(BOOK_ID_DELETING);
        book = bookDaoJdbc.getById(BOOK_ID_DELETING);
        assertThat(book.isPresent()).isEqualTo(false);
    }

    @Test
    @DisplayName("Создавать новую книгу с указанными автором и  жанрами")
    void CreateTestSuccess() {
        long id = bookDaoJdbc.create(EXPECTED_CREATING_BOOK);
        assertThat(id).isNotEqualTo(0L);
        Book actualBook = bookDaoJdbc.getById(id).orElseThrow(NoSuchElementException::new);
        Book expectedBookWithId = new Book(id,
                EXPECTED_CREATING_BOOK.getAuthor(),
                EXPECTED_CREATING_BOOK.getTitle(),
                EXPECTED_CREATING_BOOK.getShortContent(),
                EXPECTED_CREATING_BOOK.getGenres()
        );
        assertThat(actualBook)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedBookWithId);
    }


    @Test
    @DisplayName("Создавать новую книгу с указанными автором и уникальными жанрами")
    void CreateBookWithNotUniqueGenresTest() {
        List<Genre> genres = new ArrayList<>(EXPECTED_CREATING_BOOK.getGenres());
        genres.addAll(EXPECTED_CREATING_BOOK.getGenres());
        Book nonUniqueGenresBook = new Book(EXPECTED_CREATING_BOOK.getAuthor(),
                EXPECTED_CREATING_BOOK.getTitle(),
                EXPECTED_CREATING_BOOK.getShortContent(),
                genres
        );
        long id = bookDaoJdbc.create(nonUniqueGenresBook);
        assertThat(id).isNotEqualTo(0L);
        Book actualBook = bookDaoJdbc.getById(id).orElseThrow(NoSuchElementException::new);
        Book expectedBookWithId = new Book(id,
                EXPECTED_CREATING_BOOK.getAuthor(),
                EXPECTED_CREATING_BOOK.getTitle(),
                EXPECTED_CREATING_BOOK.getShortContent(),
                EXPECTED_CREATING_BOOK.getGenres()
        );
        assertThat(actualBook)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedBookWithId);
    }

    @Test
    @DisplayName("Бросать ожидаемое исключение при попытке добавить книгу с отсутствующим в БД автором")
    void CreateBookWithNotExistAuthorTest() {
        Book nonExistAuthorBook = new Book(
                new Author(AUTHOR_ID_NOT_EXIST, "test1", "test2", "test3"),
                EXPECTED_CREATING_BOOK.getTitle(),
                EXPECTED_CREATING_BOOK.getShortContent(),
                EXPECTED_CREATING_BOOK.getGenres()
        );
        assertThatThrownBy(() -> bookDaoJdbc.create(nonExistAuthorBook)).isInstanceOf(DaoIntegrityViolationException.class);
        assertThat(bookDaoJdbc.count()).isEqualTo(EXPECTED_BOOKS_COUNT);
    }

    @Test
    @DisplayName("Бросать ожидаемое исключение при попытке добавить книгу с отсутствующим в БД жанром")
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void CreateBookWithNotExistGenreTest() {
        List<Genre> genres = new ArrayList<>(EXPECTED_CREATING_BOOK.getGenres());
        genres.add(new Genre(GENRE_ID_NOT_EXIST, "test"));
        Book nonExistGenreBook = new Book(
                EXPECTED_CREATING_BOOK.getAuthor(),
                EXPECTED_CREATING_BOOK.getTitle(),
                EXPECTED_CREATING_BOOK.getShortContent(),
                genres
        );
        assertThatThrownBy(() -> bookDaoJdbc.create(nonExistGenreBook)).isInstanceOf(DaoIntegrityViolationException.class);
        assertThat(bookDaoJdbc.count()).isEqualTo(EXPECTED_BOOKS_COUNT);
    }

    @Test
    @DisplayName("Возвращать ожидаемый список книг")
    void getAllTest() {
        List<Book> actualBooks = bookDaoJdbc.getAll();
        RecursiveComparisonConfiguration configuration = RecursiveComparisonConfiguration.builder()
                .withIgnoreCollectionOrder(true)
                .build();
        assertThat(actualBooks)
                .usingRecursiveFieldByFieldElementComparator(configuration)
                .containsExactlyInAnyOrderElementsOf(EXPECTED_BOOKS_LIST);
    }

    @Test
    @DisplayName("Обновлять книгу")
    void updateSuccessTest() {
        Book beforeUpdateBook = bookDaoJdbc.getById(BOOK_ID_UPDATING).orElseThrow(NoSuchElementException::new);
        long updateBookId = beforeUpdateBook.getId();
        Book expectedBook = new Book(updateBookId, UPDATED_BOOK.getAuthor(), UPDATED_BOOK.getTitle(), UPDATED_BOOK.getShortContent(), UPDATED_BOOK.getGenres());
        bookDaoJdbc.update(expectedBook);
        Book afterUpdateBook = bookDaoJdbc.getById(updateBookId).orElseThrow(NoSuchElementException::new);
        assertThat(afterUpdateBook)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedBook);
    }

    @Test
    @DisplayName("Не обновлять книгу и бросать ожидаемое исключение, если жанра нет в БД")
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void UpdateWithNotExistGenreTest() {

        Book beforeUpdateBook = bookDaoJdbc.getById(BOOK_ID_UPDATING).orElseThrow(NoSuchElementException::new);
        long updateBookId = beforeUpdateBook.getId();
        Book updatedBook = new Book(updateBookId, UPDATED_BOOK.getAuthor(), UPDATED_BOOK.getTitle(), UPDATED_BOOK.getShortContent(), new ArrayList<>(UPDATED_BOOK.getGenres()));
        updatedBook.getGenres().add(new Genre(GENRE_ID_NOT_EXIST, ""));
        assertThatThrownBy(() -> bookDaoJdbc.update(updatedBook)).isInstanceOf(DaoIntegrityViolationException.class);
        Book afterUpdateBook = bookDaoJdbc.getById(updateBookId).orElseThrow(NoSuchElementException::new);
        assertThat(afterUpdateBook)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(beforeUpdateBook);
    }

    @Test
    @DisplayName("Не обновлять книгу и бросать ожидаемое исключение, если автора нет в БД")
    void UpdateWithNotExistAuthorTest() {
        Book beforeUpdateBook = bookDaoJdbc.getById(BOOK_ID_UPDATING).orElseThrow(NoSuchElementException::new);
        long updateBookId = beforeUpdateBook.getId();
        Author notExistAuthor = new Author(AUTHOR_ID_NOT_EXIST, UPDATED_BOOK_AUTHOR.getFirstName(), UPDATED_BOOK_AUTHOR.getMiddleName(), UPDATED_BOOK_AUTHOR.getLastName());
        Book updatedBook = new Book(updateBookId, notExistAuthor, UPDATED_BOOK.getTitle(), UPDATED_BOOK.getShortContent(), UPDATED_BOOK.getGenres());
        assertThatThrownBy(() -> bookDaoJdbc.update(updatedBook)).isInstanceOf(DaoIntegrityViolationException.class);
        Book afterUpdateBook = bookDaoJdbc.getById(updateBookId).orElseThrow(NoSuchElementException::new);
        assertThat(afterUpdateBook)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(beforeUpdateBook);
    }
}