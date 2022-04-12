package ru.otus.hw11.controllers.rest;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import ru.otus.hw11.domain.Author;
import ru.otus.hw11.domain.Book;
import ru.otus.hw11.domain.Genre;
import ru.otus.hw11.dto.AuthorDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureDataMongo
@WebFluxTest(AuthorRestController.class)
@DisplayName("Класс AuthorRestController должен:")
class AuthorRestControllerTest {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private WebTestClient client;

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
        List<Genre> savedGenres = new ArrayList<>();
        genres.forEach(g -> savedGenres.add(mongoTemplate.save(g)));
        authorWithoutBooks = mongoTemplate.save(new Author("AuthorWithoutBooks", "middleName", "lastName"));
        authorWithBooks = mongoTemplate.save(new Author("AuthorWithBooks", "middleName", "lastName"));
        List<Book> books = List.of(
                new Book(authorWithBooks, "testTitle0", "testShortContent0", List.of(savedGenres.get(0), savedGenres.get(4), savedGenres.get(5))),
                new Book(authorWithBooks, "testTitle1", "testShortContent1", List.of(savedGenres.get(1), savedGenres.get(2), savedGenres.get(3))),
                new Book(authorWithBooks, "testTitle2", "testShortContent2", List.of(savedGenres.get(5), savedGenres.get(3), savedGenres.get(4))),
                new Book(authorWithBooks, "testTitle3", "testShortContent3", List.of(savedGenres.get(0), savedGenres.get(4), savedGenres.get(5)))
        );
        books.forEach(mongoTemplate::save);
    }

    @Test
    @DisplayName("Обновлять автора")
    void updateAuthorSuccess()  {
        val expectedAuthor = new Author(authorWithBooks.getId(), "firstNameUpd", "middleNameUpd", "lastNameUpd");
        val updatedId = client.put()
                .uri("/api/authors/" + authorWithBooks.getId())
                .body(Mono.just(AuthorDto.fromEntity(expectedAuthor)), AuthorDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();
        assertThat(updatedId).isNotNull();
        val actualAuthor = mongoTemplate.findById(updatedId, Author.class);
        assertThat(actualAuthor)
                .usingRecursiveComparison()
                .isEqualTo(expectedAuthor);
    }

    @Test
    @DisplayName("Добавлять нового автора")
    void addAuthorSuccess() {
        val addingAuthor = new Author(null, "firstNameNew", "middleNameNew", "lastNameNew");
        val addedId = client.post()
                .uri("/api/authors")
                .body(Mono.just(AuthorDto.fromEntity(addingAuthor)), AuthorDto.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();
        assertThat(addedId).isNotNull();
        val actualAuthor = mongoTemplate.findById(addedId, Author.class);
        val expectedAuthor = new Author(addedId, addingAuthor.getFirstName(), addingAuthor.getMiddleName(), addingAuthor.getLastName());
        assertThat(actualAuthor)
                .usingRecursiveComparison()
                .isEqualTo(expectedAuthor);
    }

    @Test
    @DisplayName("Формировать ошибки заполнения полей при редактировании/создании автора")
    void addAuthorFieldsError()  {
        val postingAuthor = new AuthorDto(null, "", "1", "2");
        val expectedErrorCodes = List.of("Size.firstName", "NotBlank.firstName", "Size.middleName", "Size.lastName");
        val result = client.post()
                .uri("/api/authors")
                .body(Mono.just(postingAuthor), AuthorDto.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();
        assertThat(result).contains(expectedErrorCodes);
    }

    @Test
    @DisplayName("Не удалять автора с книгами - бросать IntegrityViolationException.")
    void deleteAuthorFail() {
        val result = client.delete()
                .uri("/api/authors/" + authorWithBooks.getId())
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();
        assertThat(result).contains(List.of("IntegrityViolationException", "Нельзя удалить автора c ID=" + authorWithBooks.getId() + ". У автора есть книги."));
        val deletedAuthor = mongoTemplate.findById(authorWithBooks.getId(), Author.class);
        assertThat(deletedAuthor).as("Author didn't delete")
                .matches(a -> Objects.equals(a.getId(), authorWithBooks.getId()));
    }

    @Test
    @DisplayName("Удалять автора с указанным ID")
    void deleteAuthorSuccess() {
        client.delete()
                .uri("/api/authors/" + authorWithoutBooks.getId())
                .exchange()
                .expectStatus().isOk();
        val deletedAuthor = mongoTemplate.findById(authorWithoutBooks.getId(), Author.class);
        assertThat(deletedAuthor).as("Author deleted").isNull();
    }

    @Test
    @DisplayName("Возвращать автора, при запросе автора")
    void author() {
        val expectedAuthor = authorWithBooks;
        val actualAuthorDto = client.get()
                .uri("/api/authors/" + authorWithBooks.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(AuthorDto.class)
                .returnResult()
                .getResponseBody();
        assertThat(actualAuthorDto).isNotNull();
        val actualAuthor = actualAuthorDto.toEntity();
        assertThat(actualAuthor)
                .usingRecursiveComparison()
                .isEqualTo(expectedAuthor);
    }

    @Test
    @DisplayName("Выводить всех авторов, при запросе списка авторов")
    void authors() {
        val expectedAuthors = Stream.of(authorWithBooks, authorWithoutBooks)
                .map(AuthorDto::fromEntity)
                .collect(Collectors.toList());
        val actualAuthors = client.get()
                .uri("/api/authors")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(AuthorDto.class)
                .hasSize(2)
                .returnResult()
                .getResponseBody();
        assertThat(actualAuthors)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedAuthors);
    }
}