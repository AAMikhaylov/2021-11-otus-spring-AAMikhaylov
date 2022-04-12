package ru.otus.hw11.changelogs;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.otus.hw11.domain.Author;
import ru.otus.hw11.domain.Book;
import ru.otus.hw11.domain.Comment;
import ru.otus.hw11.domain.Genre;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@ChangeUnit(id = "db-initializer", order = "000", runAlways = true, author = "Mikhaylov")
public class DbInitializer {
    private final List<Author> savedAuthors = new ArrayList<>();
    private final List<Genre> savedGenres = new ArrayList<>();
    private final List<Book> savedBooks = new ArrayList<>();

    private final List<Author> authorsToSave = List.of(
            new Author("Александр", "Сергеевич", "Пушкин"),
            new Author("Михаил", "Афанасьевич", "Булгаков"),
            new Author("Антон", "Павлович", "Чехов"),
            new Author("Татьяна", "Витальевна", "Устинова"),
            new Author("Франклин", "Патрик", "Герберт"),
            new Author("Александр", "Иванович", "Кузнецов")
    );
    private final List<Genre> genresToSave = List.of(new Genre("Фантастика"),
            new Genre("Приключения"),
            new Genre("Путешествия"),
            new Genre("Сказки"),
            new Genre("Роман"),
            new Genre("Современные детективы"),
            new Genre("Зарубежная фантастика"),
            new Genre("Космическая фантастика"),
            new Genre("Без жанра")
    );

    @Execution
    public void execution(MongoTemplate mongoTemplate) { //MongoDatabase mongoDatabase
        mongoTemplate.getMongoDatabaseFactory()
                .getMongoDatabase()
                .drop();
        saveAuthors(mongoTemplate);
        saveGenres(mongoTemplate);
        saveBooks(mongoTemplate);
        saveComments(mongoTemplate);
    }

    @RollbackExecution
    public void rollbackExecution() {
    }

    private void saveAuthors(MongoTemplate mongoTemplate) {
        authorsToSave.forEach(a -> savedAuthors.add(mongoTemplate.save(a)));
    }

    private void saveGenres(MongoTemplate mongoTemplate) {
        genresToSave.forEach(g -> savedGenres.add(mongoTemplate.save(g)));
    }

    private void saveBooks(MongoTemplate mongoTemplate) {
        val booksToSave = List.of(
                new Book(savedAuthors.get(1), "Мастер и Маргарита",
                        "Краткое содержание книги Мастер и Маргарита...",
                        List.of(savedGenres.get(0), savedGenres.get(1))),
                new Book(savedAuthors.get(3), "Судьба по книге перемен",
                        "Детективная история...",
                        List.of(savedGenres.get(5))),
                new Book(savedAuthors.get(0), "Евгений Онегин",
                        "Краткое содержание романа...",
                        List.of(savedGenres.get(4))),
                new Book(savedAuthors.get(4), "Дюна",
                        "Действие «Дюны» происходит в далеком будущем – через 20 тысяч лет от условно нашего времени....",
                        List.of(savedGenres.get(6), savedGenres.get(7))),
                new Book(savedAuthors.get(5), "Арктическая одиссея",
                        "В основе остросюжетной книги «Арктическая одиссея» – дневник полярного Робинзона 20-летнего Александра Кузнецова",
                        List.of(savedGenres.get(1)))
        );
        booksToSave.forEach(b -> savedBooks.add(mongoTemplate.save(b)));
    }

    private void saveComments(MongoTemplate mongoTemplate) {
        val currDate = new Date();
        val commentsToSave = List.of(
                new Comment("User1", savedBooks.get(0), "Комментарий 1 к книге «Мастер и Маргарита»", currDate),
                new Comment("User2", savedBooks.get(0), "Если бы меня, как водится, спросили о единственной книге, которую я взяла бы на необитаемый остров,\n" +
                        "это, безусловно, была бы эта книга – книга-шедевр, книга-дыхание, книга-сказка. Это история и любви,\n" +
                        "и веры, и прощения. Это драма, которая местами комедия. Юмор Булгакова – это мой юмор, его слова –\n" +
                        "это слова из моей песни. Хитросплетение сюжетов, прекрасные параллели между ними, уморительные диа-\n" +
                        "логи. Герои, которые поражают.\n" +
                        "Мне нравятся практически все герои этого романа. Они дополняют друг друга, они невозможны друг без\n" +
                        "друга. Единственный, кто всегда вызывал непонимание, раздражение, злость – это сам Мастер, человек,\n" +
                        "которому дан талант и который ничего не делает, чтобы этот талант защитить. А безусловный любимчик\n" +
                        "– это кот Бегемот.\n" +
                        "О чудесной идее романа можно говорить бесконечно. Можно, но не нужно, потому что эту книгу нужно\n" +
                        "читать. Сначала быстро, за два дня, а потом еще раз, уже смакуя, снова и снова возвращаясь к одним\n" +
                        "и тем же строкам, представляя на себе белый плащ с кровавым подбоем, чувствуя, как в твою кожу вти-\n" +
                        "рается крем, пахнущий болотной тиной, или раскачиваясь на люстре с примусом в лапах…", currDate),
                new Comment("User3", savedBooks.get(0), "Комментарий 3 к книге «Мастер и Маргарита»", currDate),
                new Comment("User4", savedBooks.get(0), "Комментарий 4 к книге «Мастер и Маргарита»", currDate),
                new Comment("User5", savedBooks.get(0), "Комментарий 5 к книге «Мастер и Маргарита»", currDate),
                new Comment("User6", savedBooks.get(0), "Комментарий 6 к книге «Мастер и Маргарита»", currDate),
                new Comment("User8", savedBooks.get(0), "Комментарий 8 к книге «Мастер и Маргарита»", currDate),
                new Comment("User9", savedBooks.get(0), "Комментарий 9 к книге «Мастер и Маргарита»", currDate),
                new Comment("User7", savedBooks.get(0), "Комментарий 7 к книге «Мастер и Маргарита»", currDate),
                new Comment("User10", savedBooks.get(0), "Комментарий 10 к книге «Мастер и Маргарита»", currDate),
                new Comment("User11", savedBooks.get(0), "Комментарий 11 к книге «Мастер и Маргарита»", currDate),
                new Comment("User12", savedBooks.get(0), "Комментарий 12 к книге «Мастер и Маргарита»", currDate),
                new Comment("User13", savedBooks.get(1), "Комментарий 1 к книге «Судьба по книге перемен»", currDate),
                new Comment("User14", savedBooks.get(1), "Комментарий 2 к книге «Судьба по книге перемен»", currDate),
                new Comment("User15", savedBooks.get(1), "Комментарий 3 к книге «Судьба по книге перемен»", currDate),
                new Comment("User16", savedBooks.get(1), "Комментарий 4 к книге «Судьба по книге перемен»", currDate),
                new Comment("User17", savedBooks.get(1), "Комментарий 5 к книге «Судьба по книге перемен»", currDate),
                new Comment("User18", savedBooks.get(2), "Комментарий 1 к книге «Евгений Онегин»", currDate),
                new Comment("User19", savedBooks.get(2), "Комментарий 2 к книге «Евгений Онегин»", currDate),
                new Comment("User20", savedBooks.get(2), "Комментарий 3 к книге «Евгений Онегин»", currDate),
                new Comment("User21", savedBooks.get(3), "Комментарий 1 к книге «Дюна»", currDate),
                new Comment("User22", savedBooks.get(3), "Комментарий 2 к книге «Дюна»", currDate),
                new Comment("User23", savedBooks.get(4), "Комментарий 1 к книге «Арктическая одиссея»", currDate)
        );
        commentsToSave.forEach(mongoTemplate::save);
    }
}
