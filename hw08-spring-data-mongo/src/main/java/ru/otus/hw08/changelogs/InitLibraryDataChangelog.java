package ru.otus.hw08.changelogs;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.mongodb.client.MongoDatabase;
import lombok.val;
import ru.otus.hw08.models.Author;
import ru.otus.hw08.models.Book;
import ru.otus.hw08.models.Comment;
import ru.otus.hw08.models.Genre;
import ru.otus.hw08.repositories.AuthorRepository;
import ru.otus.hw08.repositories.BookRepository;
import ru.otus.hw08.repositories.CommentRepository;
import ru.otus.hw08.repositories.GenreRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ChangeLog(order = "001")
public class InitLibraryDataChangelog {
    private final List<Author> authors = new ArrayList<>();
    private final List<Genre> genres = new ArrayList<>();
    private final List<Book> books = new ArrayList<>();
    private final List<Comment> comments = new ArrayList<>();

    @ChangeSet(order = "000", id = "dropDb", author = "AA.Mikhaylov", runAlways = true)
    public void dropDb(MongoDatabase db) {
        db.drop();
    }

    @ChangeSet(order = "001", id = "initAuthors", author = "AA.Mikhaylov", runAlways = true)
    public void initAuthors(AuthorRepository authorRepository) {
        var author = new Author("Александр", "Сергеевич", "Пушкин");
        authorRepository.save(author);
        authors.add(author);
        author = new Author("Михаил", "Афанасьевич", "Булгаков");
        authorRepository.save(author);
        authors.add(author);
        author = new Author("Антон", "Павлович", "Чехов");
        authorRepository.save(author);
        authors.add(author);
        author = new Author("Татьяна", "Витальевна", "Устинова");
        authorRepository.save(author);
        authors.add(author);
        author = new Author("Франклин", "Патрик", "Герберт");
        authorRepository.save(author);
        authors.add(author);
        author = new Author("Александр", "Иванович", "Кузнецов");
        authorRepository.save(author);
        authors.add(author);
    }

    @ChangeSet(order = "002", id = "initGenres", author = "AA.Mikhaylov", runAlways = true)
    public void initGenres(GenreRepository genreRepository) {
        var genre = new Genre("Фантастика");
        genreRepository.save(genre);
        genres.add(genre);
        genre = new Genre("Приключения");
        genreRepository.save(genre);
        genres.add(genre);
        genre = new Genre("Путешествия");
        genreRepository.save(genre);
        genres.add(genre);
        genre = new Genre("Сказки");
        genreRepository.save(genre);
        genres.add(genre);
        genre = new Genre("Роман");
        genreRepository.save(genre);
        genres.add(genre);
        genre = new Genre("Современные детективы");
        genreRepository.save(genre);
        genres.add(genre);
        genre = new Genre("Зарубежная фантастика");
        genreRepository.save(genre);
        genres.add(genre);
        genre = new Genre("Космическая фантастика");
        genreRepository.save(genre);
        genres.add(genre);
        genre = new Genre("Без жанра");
        genreRepository.save(genre);
        genres.add(genre);
    }

    @ChangeSet(order = "003", id = "initBooks", author = "AA.Mikhaylov", runAlways = true)
    public void initBooks(BookRepository bookRepository) {
        var book = new Book(authors.get(1), "Мастер и Маргарита", "Краткое содержание книги Мастер и Маргарита...", List.of(genres.get(0), genres.get(1)));
        bookRepository.save(book);
        books.add(book);
        book = new Book(authors.get(3), "Судьба по книге перемен", "Детективная история...", List.of(genres.get(5)));
        bookRepository.save(book);
        books.add(book);
        book = new Book(authors.get(0), "Евгений Онегин", "Краткое содержание романа...", List.of(genres.get(4)));
        bookRepository.save(book);
        books.add(book);
        book = new Book(authors.get(4), "Дюна", "Действие «Дюны» происходит в далеком будущем – через 20 тысяч лет от условно нашего времени....", List.of(genres.get(6), genres.get(7)));
        bookRepository.save(book);
        books.add(book);
        book = new Book(authors.get(5), "Арктическая одиссея", "В основе остросюжетной книги «Арктическая одиссея» – дневник полярного Робинзона 20-летнего Александра Кузнецова", List.of(genres.get(1)));
        bookRepository.save(book);
        books.add(book);
    }

    @ChangeSet(order = "004", id = "initComments", author = "AA.Mikhaylov", runAlways = true)
    public void initComments(CommentRepository commentRepository) {
        val currDate = new Date();
        var comment = new Comment("User1", books.get(0), "Комментарий 1 к книге «Мастер и Маргарита»", currDate);
        commentRepository.save(comment);
        comment = new Comment("User2", books.get(0), "Если бы меня, как водится, спросили о единственной книге, которую я взяла бы на необитаемый остров,\n" +
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
                "рается крем, пахнущий болотной тиной, или раскачиваясь на люстре с примусом в лапах…", currDate);
        commentRepository.save(comment);
        comment = new Comment("User3", books.get(0), "Комментарий 3 к книге «Мастер и Маргарита»", currDate);
        commentRepository.save(comment);
        comment = new Comment("User4", books.get(0), "Комментарий 4 к книге «Мастер и Маргарита»", currDate);
        commentRepository.save(comment);
        comment = new Comment("User5", books.get(0), "Комментарий 5 к книге «Мастер и Маргарита»", currDate);
        commentRepository.save(comment);
        comment = new Comment("User6", books.get(0), "Комментарий 6 к книге «Мастер и Маргарита»", currDate);
        commentRepository.save(comment);
        comment = new Comment("User7", books.get(0), "Комментарий 7 к книге «Мастер и Маргарита»", currDate);
        commentRepository.save(comment);
        comment = new Comment("User8", books.get(0), "Комментарий 8 к книге «Мастер и Маргарита»", currDate);
        commentRepository.save(comment);
        comment = new Comment("User9", books.get(0), "Комментарий 9 к книге «Мастер и Маргарита»", currDate);
        commentRepository.save(comment);
        comment = new Comment("User10", books.get(0), "Комментарий 10 к книге «Мастер и Маргарита»", currDate);
        commentRepository.save(comment);
        comment = new Comment("User11", books.get(0), "Комментарий 11 к книге «Мастер и Маргарита»", currDate);
        commentRepository.save(comment);
        comment = new Comment("User12", books.get(0), "Комментарий 12 к книге «Мастер и Маргарита»", currDate);
        commentRepository.save(comment);
        comment = new Comment("User13", books.get(1), "Комментарий 1 к книге «Судьба по книге перемен»", currDate);
        commentRepository.save(comment);
        comment = new Comment("User14", books.get(1), "Комментарий 2 к книге «Судьба по книге перемен»", currDate);
        commentRepository.save(comment);
        comment = new Comment("User15", books.get(1), "Комментарий 3 к книге «Судьба по книге перемен»", currDate);
        commentRepository.save(comment);
        comment = new Comment("User16", books.get(1), "Комментарий 4 к книге «Судьба по книге перемен»", currDate);
        commentRepository.save(comment);
        comment = new Comment("User17", books.get(1), "Комментарий 5 к книге «Судьба по книге перемен»", currDate);
        commentRepository.save(comment);
        comment = new Comment("User18", books.get(2), "Комментарий 1 к книге «Евгений Онегин»", currDate);
        commentRepository.save(comment);
        comment = new Comment("User19", books.get(2), "Комментарий 2 к книге «Евгений Онегин»", currDate);
        commentRepository.save(comment);
        comment = new Comment("User20", books.get(2), "Комментарий 3 к книге «Евгений Онегин»", currDate);
        commentRepository.save(comment);
        comment = new Comment("User21", books.get(3), "Комментарий 1 к книге «Дюна»", currDate);
        commentRepository.save(comment);
        comment = new Comment("User22", books.get(3), "Комментарий 2 к книге «Дюна»", currDate);
        commentRepository.save(comment);
        comment = new Comment("User23", books.get(4), "Комментарий 1 к книге «Арктическая одиссея»", currDate);
        commentRepository.save(comment);
    }

}
