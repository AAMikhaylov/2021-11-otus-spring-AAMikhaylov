package ru.otus.hw08.services.impl;

import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.otus.hw08.models.Author;
import ru.otus.hw08.models.Book;
import ru.otus.hw08.models.Comment;
import ru.otus.hw08.models.Genre;
import ru.otus.hw08.repositories.AuthorRepository;
import ru.otus.hw08.repositories.BookRepository;
import ru.otus.hw08.repositories.CommentRepository;
import ru.otus.hw08.repositories.GenreRepository;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@DataMongoTest
@Import({BookServiceImpl.class})
@DisplayName("Класс BookServiceImpl должен:")
class BookServiceImplTest {
    @Autowired
    private BookServiceImpl bookService;
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private MongoTemplate mongoTemplate;

    private List<Book> savedBooks;

    @BeforeEach
    void setUp() {
        mongoTemplate.getDb().drop();
        var author = authorRepository.save(new Author("firstName", "middleName", "lastName"));
        var genres1 = genreRepository.saveAll(List.of(new Genre("genreName1"), new Genre("genreName2")));
        var genres2 = genreRepository.saveAll(List.of(new Genre("genreName3")));
        savedBooks = bookRepository.saveAll(List.of(
                new Book(author, "testTitle1", "testShortContent1", genres1),
                new Book(author, "testTitle2", "testShortContent2", genres2)
        ));
        var date = new Date();
        commentRepository.saveAll(List.of(
                new Comment("User1", savedBooks.get(0), "Comment1", date),
                new Comment("User2", savedBooks.get(0), "Comment2", date),
                new Comment("User3", savedBooks.get(0), "Comment3", date)
        ));
        commentRepository.saveAll(List.of(
                new Comment("User4", savedBooks.get(1), "Comment4", date),
                new Comment("User5", savedBooks.get(1), "Comment5", date)
        ));

    }

    @Test
    @DisplayName("Возвращать пустое значение,если книги с заданным ID не существует")
    void getByIdTestFail() {
        val actualBook = bookRepository.findById("BOOK_ID_NOT_EXIST");
        assertThat(actualBook.isEmpty()).isEqualTo(true);
    }

    @Test
    @DisplayName("Удалять книгу по заданному ID со всеми комментариями")
    void deleteByIdSuccess() {
        val book = bookRepository.findAll().get(0);
        var commentCount = commentRepository.findAllByBookId(book.getId()).size();
        assertThat(commentCount).isEqualTo(3);
        bookService.delete(book.getId());
        val deletedBook = bookRepository.findById(book.getId());
        assertThat(deletedBook.isEmpty()).isTrue();
        commentCount = commentRepository.findAllByBookId(book.getId()).size();
        assertThat(commentCount).isEqualTo(0);
    }

    @Test
    @DisplayName("Возвращать ожидаемую книгу по ID со всеми данными")
    void getByIdTestSuccess() {
        val actualBookOpt = bookService.findById(savedBooks.get(0).getId());
        assertThat(actualBookOpt.isPresent()).isTrue();
        var book = actualBookOpt.get();
        assertThat(book)
                .matches(b -> !"".equals(b.getId()), "ID is correct")
                .matches(b -> !b.getTitle().isEmpty(), "title of book not empty")
                .matches(b -> !b.getShortContent().isEmpty(), "short book content not empty")
                .matches(b -> b.getAuthor() != null, "author present")
                .matches(b -> b.getGenres().size() > 0, "list of genres not empty");
    }

    @Test
    @DisplayName("Возвращать список книг c полной информацией")
    void getAllTest() {
        List<Book> actualBooks = bookService.findAll();
        assertThat(actualBooks)
                .hasSize(2)
                .allMatch(b -> !"".equals(b.getId()))
                .allMatch(b -> b.getGenres().size() > 0)
                .allMatch(b -> !b.getTitle().isEmpty())
                .allMatch(b -> b.getAuthor() != null)
                .allMatch(b -> !b.getShortContent().isEmpty());
    }

    @Test
    @DisplayName("Обновлять книгу")
    void updateTest() {
        var book = bookRepository.findAll().get(1);
        var newAuthor = authorRepository.save(new Author("first", "middle", "last"));
        List<Genre> newGenres = genreRepository.saveAll(List.of(new Genre("newGenre1"), new Genre("newGenre2")));
        var updatedBook = new Book(book.getId(), newAuthor, "newTitle", "newShortContent", newGenres);
        bookService.save(updatedBook);
        var actualBook = bookRepository.findById(book.getId()).orElseThrow(NoSuchElementException::new);
        assertThat(actualBook).usingRecursiveComparison().isEqualTo(updatedBook);
    }

    @Test
    @DisplayName("Не обновлять книгу и бросать DataIntegrityViolationException, если автора нет в БД")
    void updateWithNotExistAuthorTest() {
        var book = bookRepository.findAll().get(1);
        var author = book.getAuthor();
        var newAuthor = new Author("not_exist_author", author.getFirstName(), author.getMiddleName(), author.getLastName());
        List<Genre> newGenres = genreRepository.saveAll(List.of(new Genre("newGenre1"), new Genre("newGenre2")));
        var updatedBook = new Book(book.getId(), newAuthor, "newTitle", "newShortContent", newGenres);
        assertThatThrownBy(() -> bookService.save(updatedBook))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessage("Author isn't present");
    }

    @Test
    @DisplayName("Не обновлять книгу и бросать DataIntegrityViolationException, если жанра нет в БД")
    void updateWithNotExistGenreTest() {
        var book = bookRepository.findAll().get(1);
        var newAuthor = authorRepository.save(new Author("first", "middle", "last"));
        List<Genre> newGenres = List.of(new Genre("not_exist_genre", "newGenre1"));
        var updatedBook = new Book(book.getId(), newAuthor, "newTitle", "newShortContent", newGenres);
        assertThatThrownBy(() -> bookService.save(updatedBook))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessage("Genre isn't present");
    }

    @Test
    @DisplayName("Создавать новую книгу с указанными автором и  жанрами")
    void createBookTest() {
        var newAuthor = authorRepository.save(new Author("first", "middle", "last"));
        List<Genre> newGenres = genreRepository.saveAll(List.of(new Genre("newGenre1"), new Genre("newGenre2")));
        val book = new Book(newAuthor, "testTitle", "testShortContent", newGenres);
        val savedBook = bookService.save(book);
        val expectedBook = new Book(savedBook.getId(), book.getAuthor(), book.getTitle(), book.getShortContent(), book.getGenres());
        val actualBook = bookRepository.findById(savedBook.getId()).orElseThrow(NoSuchElementException::new);
        assertThat(actualBook).usingRecursiveComparison().isEqualTo(expectedBook);
    }

    @Test
    @DisplayName("Бросать ожидаемое исключение при попытке добавить книгу с отсутствующим в БД автором")
    void createBookWithNotExistAuthorTest() {
        var newAuthor = new Author("not_exist_author", "f", "m", "l");
        List<Genre> newGenres = genreRepository.saveAll(List.of(new Genre("newGenre1"), new Genre("newGenre2")));
        val book = new Book(newAuthor, "testTitle", "testShortContent", newGenres);
        assertThatThrownBy(() -> bookService.save(book))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessage("Author isn't present");
    }

    @Test
    @DisplayName("Бросать ожидаемое исключение при попытке добавить книгу с отсутствующим в БД жанром")
    void createBookWithNotExistGenreTest() {
        var newAuthor = authorRepository.save(new Author("first", "middle", "last"));
        List<Genre> newGenres = List.of(new Genre("not_exist_genre", "newGenre1"));
        val book = new Book(newAuthor, "testTitle", "testShortContent", newGenres);
        assertThatThrownBy(() -> bookService.save(book))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessage("Genre isn't present");
    }


}