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

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@DataMongoTest
@Import({GenreServiceImpl.class})
@DisplayName("Класс GenreServiceImpl должен:")
class GenreServiceImplTest {
    @Autowired
    private GenreServiceImpl genreService;
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private MongoTemplate mongoTemplate;

    private Genre genreWithBooks;
    private Genre genreWithoutBooks;
    private Genre genreForCascadeUpdate;

    @BeforeEach
    void setUp() {
        mongoTemplate.getDb().drop();
        List<Genre> genres = List.of(
                new Genre("testGenre0"),
                new Genre("testGenre1"),
                new Genre("testGenre2"),
                new Genre("testGenre3"),
                new Genre("testGenre4"),
                new Genre("testGenre5")
        );
        genreWithBooks = genreRepository.save(new Genre("testGenreWithBook"));
        genreWithoutBooks = genreRepository.save(new Genre("testGenreWithoutBook"));
        genreForCascadeUpdate = genreRepository.save(new Genre("testGenreCascadeUpdate"));
        var savedGenresList = genreRepository.saveAll(genres);
        var author = authorRepository.save(new Author("AuthorWithoutBooks", "middleName", "lastName"));
        List<Book> books = List.of(
                new Book(author, "testTitle0", "testShortContent0", List.of(genreForCascadeUpdate, savedGenresList.get(4), savedGenresList.get(5))),
                new Book(author, "testTitle1", "testShortContent1", List.of(genreWithBooks, savedGenresList.get(2), savedGenresList.get(3))),
                new Book(author, "testTitle2", "testShortContent2", List.of(savedGenresList.get(5), savedGenresList.get(3), savedGenresList.get(4), genreWithBooks)),
                new Book(author, "testTitle3", "testShortContent3", List.of(savedGenresList.get(4), genreForCascadeUpdate, savedGenresList.get(5))),
                new Book(author, "testTitle4", "testShortContent4", List.of(genreForCascadeUpdate))
        );
        bookRepository.saveAll(books);
    }

    @Test
    @DisplayName("Удалять жанр без книг по заданному ID")
    void deleteByIdSuccess() {
        var genre = genreRepository.findById(genreWithoutBooks.getId()).orElseThrow(NoSuchElementException::new);
        genreService.delete(genre.getId());
        val deletedGenre = genreRepository.findById(genre.getId());
        assertThat(deletedGenre.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("Возвращать ожидаемый жанр по ID")
    void getByIdTestSuccess() {
        val expectedGenre = genreWithBooks;
        val actualGenreOpt = genreRepository.findById(genreWithBooks.getId());
        assertThat(actualGenreOpt.isPresent()).isTrue();
        assertThat(actualGenreOpt.get()).usingRecursiveComparison().isEqualTo(expectedGenre);

    }

    @Test
    @DisplayName("Не удалять жанр, если у него есть книги, выбрасывать DataIntegrityViolationException")
    void deleteByIdFail() {
        val deletingGenre = genreRepository.findById(genreWithBooks.getId());
        assertThat(deletingGenre.isPresent()).isTrue();
        assertThatThrownBy(() -> genreService.delete(deletingGenre.get().getId()))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessage("Can't delete genre. Genre has book(s)");
    }

    @Test
    @DisplayName("Возвращать список жанров c полной информацией")
    void getAllTest() {
        List<Genre> actualGenres = genreRepository.findAll();
        assertThat(actualGenres)
                .hasSize(9)
                .allMatch(g -> !g.getName().isEmpty())
                .allMatch(g -> !"".equals(g.getId()));
    }

    @Test
    @DisplayName("Обновлять жанр во всех книгах")
    void updateTest() {
        var books = bookRepository.findAll();
        List<Genre> genresInBooks = new ArrayList<>();
        books.forEach(b -> genresInBooks.addAll(b.getGenres()));
        var expectedGenre = new Genre(genreForCascadeUpdate.getId(), "newGenreName");
        List<Genre> expectedGenresInBooks = genresInBooks.stream().map(g -> g.getId().equals(expectedGenre.getId()) ? expectedGenre : g).collect(Collectors.toList());
        genreRepository.cascadeUpdate(expectedGenre);
        var actualGenre = genreRepository.findById(genreForCascadeUpdate.getId()).orElseThrow(NoSuchElementException::new);
        assertThat(actualGenre).usingRecursiveComparison().isEqualTo(expectedGenre);
        books = bookRepository.findAll();
        List<Genre> actualGenresInBooks = new ArrayList<>();
        books.forEach(b -> actualGenresInBooks.addAll(b.getGenres()));
        assertThat(actualGenresInBooks)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactlyInAnyOrderElementsOf(expectedGenresInBooks)
                .hasSize(14)
                .filteredOn(g -> g.getName().equals(expectedGenre.getName()))
                .hasSize(3);
    }
    @Test
    @DisplayName("Добавлять жанр в БД")
    void createTest() {
        val newGenre = new Genre("newGenreName");
        val savedGenre = genreService.save(newGenre);
        val expectedGenre = new Genre(savedGenre.getId(), newGenre.getName());
        val actualGenre = genreRepository.findById(savedGenre.getId()).orElseThrow(NoSuchElementException::new);
        assertThat(actualGenre).usingRecursiveComparison().isEqualTo(expectedGenre);
    }
}