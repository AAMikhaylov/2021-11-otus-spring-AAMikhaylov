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

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@DataMongoTest
@Import({CommentServiceImpl.class})
@DisplayName("Класс CommentServiceImpl должен:")
class CommentServiceImplTest {
    @Autowired
    private CommentServiceImpl commentService;
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
    private List<Comment> comments;

    @BeforeEach
    void setUp() {
        mongoTemplate.getDb().drop();
        var author = authorRepository.save(new Author("firstName", "middleName", "lastName"));
        var genres = genreRepository.saveAll(List.of(new Genre("genreName1"), new Genre("genreName2")));
        var book = bookRepository.save(new Book(author, "testTitle", "testShortContent", genres));
        var date = new Date();
        comments = commentRepository.saveAll(List.of(
                new Comment("User1", book, "Comment1", date),
                new Comment("User2", book, "Comment2", date),
                new Comment("User3", book, "Comment3", date)
        ));
        book = bookRepository.save(new Book(author, "testTitle2", "testShortContent2", genres));
        comments.addAll(commentRepository.saveAll(List.of(
                new Comment("User4", book, "Comment4", date),
                new Comment("User5", book, "Comment5", date)
                )));


    }

    @Test
    @DisplayName("Возвращать пустое значение,если комментария с заданным ID не существует")
    void getByIdTestFail() {
        val actualComment = commentService.findById("COMMENT_ID_NOT_EXIST");
        assertThat(actualComment.isEmpty()).isEqualTo(true);
    }

    @Test
    @DisplayName("Удалять комментарий по заданному ID")
    void deleteByIdSuccess() {
        val commentOpt = commentService.findById(comments.get(0).getId());
        assertThat(commentOpt.isPresent()).isTrue();
        commentService.delete(commentOpt.get().getId());
        val deletedComment = commentRepository.findById(commentOpt.get().getId());
        assertThat(deletedComment.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("Возвращать ожидаемый комментарий по ID")
    void getByIdTestSuccess() {
        val actualComment = commentService.findById(comments.get(0).getId()).orElseThrow(NoSuchElementException::new);
        val expectedComment = commentRepository.findById(comments.get(0).getId()).orElseThrow(NoSuchElementException::new);
        assertThat(actualComment).usingRecursiveComparison().isEqualTo(expectedComment);
    }

    @Test
    @DisplayName("Возвращать ожидаемые комментарии по ID книги")
    void getByBookIdTestSuccess() {
        var book = bookRepository.findAll().get(0);
        val actualComment = commentService.findAllByBookId(book.getId());
        assertThat(actualComment)
                .hasSize(3)
                .allMatch(c->List.of("User1","User2","User3").contains(c.getUserName()))
                .allMatch(c->List.of("Comment1","Comment2","Comment3").contains(c.getContent()));
    }


    @Test
    @DisplayName("Возвращать список комментариев c полной информацией")
    void getAllTest() {
        List<Comment> actualComments = commentService.findAll();
        assertThat(actualComments)
                .hasSize(5)
                .allMatch(c -> !"".equals(c.getId()), "ID is correct")
                .allMatch(c -> !c.getContent().isEmpty(), "Content is not empty")
                .allMatch(c -> c.getCommentDate().before(new Date()), "comment date not too big")
                .allMatch(c -> c.getCommentDate().after(new Timestamp(10000)), "comment date not too small")
                .allMatch(c -> !c.getUserName().isEmpty(), "user name is not empty")
                .allMatch(c -> c.getBook() != null, "book is present");
    }

    @Test
    @DisplayName("Обновлять комментарий")
    void updateTest() {
        val oldComment = commentService.findById(comments.get(1).getId()).orElseThrow(NoSuchElementException::new);
        val updatedComment = new Comment(oldComment.getId(), "testUserName", oldComment.getBook(), "testContent", new Date());
        commentService.save(updatedComment);
        val actualComment = commentRepository.findById(updatedComment.getId()).orElseThrow(NoSuchElementException::new);
        assertThat(actualComment).usingRecursiveComparison().ignoringFields("book.genres", "book.comments", "commentDate").isEqualTo(updatedComment);
    }

    @Test
    @DisplayName("Создавать новый комментарий к указанной книге")
    void createCommentTest() {
        val book = bookRepository.findAll().get(0);
        val newComment = new Comment("newUserName", book, "newComment", new Date());
        val savedComment = commentService.save(newComment);
        val actualComment = commentRepository.findById(savedComment.getId()).orElseThrow(NoSuchElementException::new);
        val expectedComment = new Comment(savedComment.getId(), newComment.getUserName(), newComment.getBook(), newComment.getContent(), new Date());
        assertThat(actualComment).usingRecursiveComparison().ignoringFields("book.comments", "commentDate").isEqualTo(expectedComment);
    }


    @Test
    @DisplayName("Бросать исключение при добавлении комментария к несуществующей книге")
    void createCommentWithNotExistBookTest() {
        val book = bookRepository.findAll().get(0);
        val wrongBook = new Book("NOT_EXIST_ID", book.getAuthor(), book.getTitle(), book.getShortContent(), book.getGenres());
        val newComment = new Comment("newUserName", wrongBook, "newComment", new Date());
        assertThatThrownBy(() -> commentService.save(newComment))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessage("Book isn't present");
    }
}