package ru.otus.hw14.config;

import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.otus.hw14.models.mongo.AuthorMongo;
import ru.otus.hw14.models.mongo.BookMongo;
import ru.otus.hw14.models.mongo.CommentMongo;
import ru.otus.hw14.models.mongo.GenreMongo;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBatchTest
@SpringBootTest
@DisplayName("Приложение миграции данных должно:")
class MigrationJobTest {
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;
    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final List<GenreMongo> EXPECTED_GENRES = List.of(
            new GenreMongo("genre1"),
            new GenreMongo("genre2"),
            new GenreMongo("genre3")
    );
    private static final List<AuthorMongo> EXPECTED_AUTHORS = List.of(
            new AuthorMongo("author11", "author12", "author13"),
            new AuthorMongo("author21", "author22", "author23"),
            new AuthorMongo("author31", "author32", "author33")
    );
    private static final List<BookMongo> EXPECTED_BOOKS = List.of(
            new BookMongo(EXPECTED_AUTHORS.get(1), "bookTitle1", "bookShortContent1",
                    List.of(EXPECTED_GENRES.get(0))),
            new BookMongo(EXPECTED_AUTHORS.get(0), "bookTitle2", "bookShortContent2",
                    List.of(EXPECTED_GENRES.get(0), EXPECTED_GENRES.get(1))),
            new BookMongo(EXPECTED_AUTHORS.get(2), "bookTitle3", "bookShortContent3",
                    List.of(EXPECTED_GENRES.get(1)))
    );
    private static final List<CommentMongo> EXPECTED_COMMENTS = List.of(
            new CommentMongo("User1", EXPECTED_BOOKS.get(0), "CommentBook11"),
            new CommentMongo("User2", EXPECTED_BOOKS.get(1), "CommentBook21"),
            new CommentMongo("User3", EXPECTED_BOOKS.get(1), "CommentBook22")
    );

    @BeforeEach
    void clearMetaData() throws Exception {
        jobRepositoryTestUtils.removeJobExecutions();
        val resultJob = jobLauncherTestUtils.launchJob();
        assertThat(resultJob.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);

    }


    @Test
    @DisplayName("Копировать авторов из H2 в Mongo")
    void jobTestMigrationAuthors() {
        val actualAuthors = mongoTemplate.findAll(AuthorMongo.class);
        assertThat(actualAuthors)
                .usingRecursiveComparison()
                .ignoringFields("id", "h2Id")
                .isEqualTo(EXPECTED_AUTHORS);
    }

    @Test
    @DisplayName("Копировать жанры из H2 в Mongo")
    void jobTestMigrationGenres() {
        val actualGenres = mongoTemplate.findAll(GenreMongo.class);
        assertThat(actualGenres)
                .usingRecursiveComparison()
                .ignoringFields("id", "h2Id")
                .isEqualTo(EXPECTED_GENRES);
    }

    @Test
    @DisplayName("Копировать книги из H2 в Mongo со всеми связями")
    void jobTestMigrationBooks() {
        val actualBooks = mongoTemplate.findAll(BookMongo.class);
        assertThat(actualBooks)
                .usingRecursiveComparison()
                .ignoringFields("id", "h2Id", "author.h2Id", "author.id", "genres.id", "genres.h2Id")
                .isEqualTo(EXPECTED_BOOKS);
    }

    @Test
    @DisplayName("Копировать комментарии из H2 в Mongo со всеми связями")
    void jobTestMigrationComments() {
        val actualComments = mongoTemplate.findAll(CommentMongo.class);
        assertThat(actualComments)
                .usingRecursiveComparison()
                .ignoringFields("id", "book.id", "book.h2Id", "book.author.h2Id",
                        "book.author.id", "book.genres.id", "book.genres.h2Id", "commentDate")
                .isEqualTo(EXPECTED_COMMENTS);

    }

}