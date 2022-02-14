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
import ru.otus.hw08.models.Genre;
import ru.otus.hw08.repositories.AuthorRepository;
import ru.otus.hw08.repositories.BookRepository;
import ru.otus.hw08.repositories.GenreRepository;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataMongoTest
@Import({AuthorServiceImpl.class})
@DisplayName("Класс AuthorServiceImpl должен:")
class AuthorServiceImplTest {
    @Autowired
    private AuthorServiceImpl authorService;
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private MongoTemplate mongoTemplate;


    private Author authorWithBooks;
    private Author authorWithoutBooks;

    @BeforeEach
    void setUp() {
        mongoTemplate.getDb().drop();
        List<Genre> genres = List.of(new Genre("testGenre0"),
                new Genre("testGenre1"),
                new Genre("testGenre2"),
                new Genre("testGenre3"),
                new Genre("testGenre4"),
                new Genre("testGenre5")
        );
        var savedGenresList = genreRepository.saveAll(genres);
        authorWithoutBooks = authorRepository.save(new Author("AuthorWithoutBooks", "middleName", "lastName"));
        authorWithBooks = authorRepository.save(new Author("AuthorWithBooks", "middleName", "lastName"));
        List<Book> books = List.of(
                new Book(authorWithBooks, "testTitle0", "testShortContent0", List.of(savedGenresList.get(0), savedGenresList.get(4), savedGenresList.get(5))),
                new Book(authorWithBooks, "testTitle1", "testShortContent1", List.of(savedGenresList.get(1), savedGenresList.get(2), savedGenresList.get(3))),
                new Book(authorWithBooks, "testTitle2", "testShortContent2", List.of(savedGenresList.get(5), savedGenresList.get(3), savedGenresList.get(4))),
                new Book(authorWithBooks, "testTitle3", "testShortContent3", List.of(savedGenresList.get(0), savedGenresList.get(4), savedGenresList.get(5)))
        );
        bookRepository.saveAll(books);
    }

    @Test
    @DisplayName("Возвращать пустое значение,если автора с заданным ID не существует")
    void getByIdTestFail() {
        val actualAuthor = authorService.findById("AUTHOR_ID_NOT_EXIST");
        assertThat(actualAuthor.isEmpty()).isEqualTo(true);
    }

    @Test
    @DisplayName("Удалять автора без книг по заданному ID")
    void deleteByIdSuccess() {
        var authorOpt = authorRepository.findById(authorWithoutBooks.getId());
        assertThat(authorOpt.isPresent()).isTrue();
        authorService.delete(authorOpt.get().getId());
        var deletedAuthorOpt = authorRepository.findById(authorWithoutBooks.getId());
        assertThat(deletedAuthorOpt.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("Возвращать ожидаемого автора по ID")
    void getByIdTestSuccess() {
        val expectedAuthor = authorWithBooks;
        val actualAuthorOpt = authorRepository.findById(authorWithBooks.getId());
        assertThat(actualAuthorOpt.isPresent()).isTrue();
        assertThat(actualAuthorOpt.get()).usingRecursiveComparison().isEqualTo(expectedAuthor);
    }


    @Test
    @DisplayName("Не удалять автора, если у него есть книги, выбрасывать DataIntegrityViolationException")
    void deleteByIdFail() {
        val deletingAuthor = authorRepository.findById(authorWithBooks.getId());
        assertThat(deletingAuthor.isPresent()).isTrue();
        assertThatThrownBy(() -> authorService.delete(deletingAuthor.get().getId()))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessage("Can't delete author. Author has book(s)");
    }

    @Test
    @DisplayName("Возвращать список авторов c полной информацией")
    void getAllTest() {
        List<Author> actualAuthors = authorService.findAll();
        assertThat(actualAuthors)
                .hasSize(2)
                .allMatch(s -> !s.getFirstName().isEmpty())
                .allMatch(s -> !s.getLastName().isEmpty())
                .allMatch(s -> !s.getMiddleName().isEmpty())
                .allMatch(s -> !"".equals(s.getId()));
    }


    @Test
    @DisplayName("Обновлять автора")
    void updateTest() {
        var author = authorRepository.findById(authorWithBooks.getId()).orElseThrow(NoSuchElementException::new);
        var updatedAuthor = new Author(author.getId(), author.getFirstName() + "!", author.getMiddleName() + "!!", author.getLastName() + "!!!");
        authorService.save(updatedAuthor);
        val actualAuthor = authorRepository.findById(author.getId()).orElseThrow(NoSuchElementException::new);
        assertThat(actualAuthor).usingRecursiveComparison().isEqualTo(updatedAuthor);
    }

    //
    @Test
    @DisplayName("Добавлять автора в БД")
    void createTest() {
        val newAuthor = new Author("newFirstName", "newMiddleName", "newLastName");
        val savedAuthor = authorService.save(newAuthor);
        val expectedAuthor = new Author(savedAuthor.getId(), newAuthor.getFirstName(), newAuthor.getMiddleName(), newAuthor.getLastName());
        val actualAuthor = authorRepository.findById(savedAuthor.getId()).orElseThrow(NoSuchElementException::new);
        assertThat(actualAuthor).usingRecursiveComparison().isEqualTo(expectedAuthor);
    }

}