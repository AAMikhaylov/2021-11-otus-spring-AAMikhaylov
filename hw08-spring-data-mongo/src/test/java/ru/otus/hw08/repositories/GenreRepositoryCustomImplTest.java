package ru.otus.hw08.repositories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.otus.hw08.models.Author;
import ru.otus.hw08.models.Book;
import ru.otus.hw08.models.Genre;
import java.util.*;
import java.util.stream.Collectors;
import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@Import({GenreRepositoryCustomImpl.class})
@DisplayName("Класс GenreRepositoryCustom должен:")
class GenreRepositoryCustomImplTest {
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private MongoTemplate mongoTemplate;

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
        var author = authorRepository.save(new Author("firstName", "middleName", "lastName"));
        List<Book> books = List.of(
                new Book(author, "testTitle0", "testShortContent0", List.of(savedGenresList.get(0), savedGenresList.get(4), savedGenresList.get(5))),
                new Book(author, "testTitle1", "testShortContent1", List.of(savedGenresList.get(1), savedGenresList.get(2), savedGenresList.get(3))),
                new Book(author, "testTitle2", "testShortContent2", List.of(savedGenresList.get(5), savedGenresList.get(3), savedGenresList.get(4))),
                new Book(author, "testTitle3", "testShortContent3", List.of(savedGenresList.get(0), savedGenresList.get(4), savedGenresList.get(5)))
        );
        bookRepository.saveAll(books);
    }

    @Test
    @DisplayName("Каскадно обновлять жанры во всех книгах")
    void cascadeUpdateTest() {
        var genreForUpdate = genreRepository.findByName("testGenre3").get(0);
        var books = bookRepository.findAll();
        List<Genre> genresInBooks = new ArrayList<>();
        books.forEach(b -> genresInBooks.addAll(b.getGenres()));
        var expectedGenre = new Genre(genreForUpdate.getId(), "newGenreName");
        List<Genre> expectedGenresInBooks = genresInBooks.stream().map(g -> g.getId().equals(genreForUpdate.getId()) ? expectedGenre : g).collect(Collectors.toList());
        genreRepository.cascadeUpdate(expectedGenre);
        var actualGenre = genreRepository.findById(genreForUpdate.getId()).orElseThrow(NoSuchElementException::new);
        assertThat(actualGenre).usingRecursiveComparison().isEqualTo(expectedGenre);
        books = bookRepository.findAll();
        List<Genre> actualGenresInBooks = new ArrayList<>();
        books.forEach(b -> actualGenresInBooks.addAll(b.getGenres()));
        assertThat(actualGenresInBooks)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactlyInAnyOrderElementsOf(expectedGenresInBooks)
                .hasSize(12)
                .filteredOn(g -> g.getName().equals(expectedGenre.getName()))
                .hasSize(2);
    }
}