package ru.otus.hw17.services;

import lombok.val;
import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import ru.otus.hw17.domain.Book;
import ru.otus.hw17.domain.Comment;
import ru.otus.hw17.dto.CommentDto;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import({CommentServiceImpl.class})
@DisplayName("Класс CommentServiceImpl должен:")
class CommentServiceImplTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private CommentServiceImpl commentService;
    private static final int EXPECTED_COMMENT_COUNT = 23;
    private static final long COMMENT_ID_NOT_EXIST = 100L;

    private static final long COMMENT_ID_DELETING = 2L;
    private static final long COMMENT_ID_UPDATING = 1L;
    private static final long COMMENT_ID_READING = 3L;
    private static final int EXPECTED_QUERIES_COUNT_FINDALL = 2;
    private static final int EXPECTED_QUERIES_COUNT_FINDBYID = 2;
    private static final long BOOK_ID_FOR_COMMENT = 3L;


    @Test
    @DisplayName("Возвращать пустое значение,если комментария с заданным ID не существует")
    void getByIdTestFail() {
        val actualBook = commentService.findById(COMMENT_ID_NOT_EXIST);
        assertThat(actualBook.isEmpty()).isEqualTo(true);
    }

    @Test
    @DisplayName("Удалять комментарий по заданному ID")
    void deleteByIdSuccess() {
        val commentOpt = commentService.findById(COMMENT_ID_DELETING);
        assertThat(commentOpt.isPresent()).isEqualTo(true);
        commentService.delete(COMMENT_ID_DELETING);
        em.flush();
        em.clear();
        val deletedComment = em.find(Comment.class, COMMENT_ID_DELETING);
        assertThat(deletedComment).isNull();
    }

    @Test
    @DisplayName("Возвращать ожидаемый комментарий по ID")
    void getByIdTestSuccess() {
        val actualCommentOpt = commentService.findById(COMMENT_ID_READING);
        assertThat(actualCommentOpt.isPresent()).isEqualTo(true);
        val expectedComment = CommentDto.fromEntity(em.find(Comment.class, COMMENT_ID_READING));
        assertThat(actualCommentOpt.orElseThrow(NoSuchElementException::new)).usingRecursiveComparison().isEqualTo(expectedComment);
    }

    @Test
    @DisplayName("Возвращать список комментариев c полной информацией")
    void getAllTest() {
        List<CommentDto> actualComments = commentService.findAll();
        assertThat(actualComments)
                .hasSize(EXPECTED_COMMENT_COUNT)
                .allMatch(c -> c.getId() != 0L, "ID is not 0")
                .allMatch(c -> !c.getContent().isEmpty(), "Content is not empty")
                .allMatch(c -> c.getCommentDate().before(new Date()), "comment date not too big")
                .allMatch(c -> c.getCommentDate().after(new Timestamp(10000)), "comment date not too small")
                .allMatch(c -> !c.getUserName().isEmpty(), "user name is not empty")
                .allMatch(c -> c.getBook() != null, "book is present");
    }

    @Test
    @DisplayName("Формировать ожидаемое количество запросов к БД при загрузке всех комментариев")
    void getAllQueryCountTest() {
        SessionFactory sessionFactory = em.getEntityManager().getEntityManagerFactory()
                .unwrap(SessionFactory.class);
        sessionFactory.getStatistics().setStatisticsEnabled(true);
        sessionFactory.getStatistics().clear();
        commentService.findAll();
        assertThat(sessionFactory.getStatistics().getPrepareStatementCount()).isEqualTo(EXPECTED_QUERIES_COUNT_FINDALL);
    }

    @Test
    @DisplayName("Формировать ожидаемое количество запросов к БД при загрузке комментария по ID")
    void getByIdQueryCountTest() {
        SessionFactory sessionFactory = em.getEntityManager().getEntityManagerFactory()
                .unwrap(SessionFactory.class);
        sessionFactory.getStatistics().setStatisticsEnabled(true);
        sessionFactory.getStatistics().clear();
        commentService.findById(COMMENT_ID_READING);
        assertThat(sessionFactory.getStatistics().getPrepareStatementCount()).isEqualTo(EXPECTED_QUERIES_COUNT_FINDBYID);
    }

    @Test
    @DisplayName("Обновлять комментарий")
    void updateTest() {
        val oldComment = em.find(Comment.class, COMMENT_ID_UPDATING);
        val updatedComment = new Comment(COMMENT_ID_UPDATING, "testUserName", oldComment.getBook(), "testContent");
        commentService.save(CommentDto.fromEntity(updatedComment));
        em.flush();
        em.clear();
        val actualComment = em.find(Comment.class, COMMENT_ID_UPDATING);
        assertThat(actualComment).usingRecursiveComparison().ignoringFields("book.genres", "book.comments", "commentDate").isEqualTo(updatedComment);
    }

    @Test
    @DisplayName("Создавать новый комментарий к указанной книге")
    void createCommentTest() {
        val book = em.find(Book.class, BOOK_ID_FOR_COMMENT);
        Hibernate.initialize(book.getGenres());
        val newComment = new Comment("testUserName", book, "testComment");
        val savedComment = commentService.save(CommentDto.fromEntity(newComment));
        em.flush();
        em.clear();
        val actualComment = em.find(Comment.class, savedComment.getId());
        val expectedComment = new Comment(savedComment.getId(), newComment.getUserName(), newComment.getBook(), newComment.getContent());
        assertThat(actualComment).usingRecursiveComparison().ignoringFields("book.comments", "commentDate").isEqualTo(expectedComment);
    }

    @Test
    @DisplayName("Бросать исключение PersistenceException при попытке добавить комментарий к отсутствующей в БД книге")
    void createCommentWithNotExistBookTest() {
        val book = em.find(Book.class, BOOK_ID_FOR_COMMENT);
        val newComment = CommentDto.fromEntity(new Comment("testUserName", book, "testComment"));
        em.remove(book);
        em.flush();
        em.clear();
        assertThatThrownBy(() -> commentService.save(newComment)).isInstanceOf(DataIntegrityViolationException.class);
    }


}