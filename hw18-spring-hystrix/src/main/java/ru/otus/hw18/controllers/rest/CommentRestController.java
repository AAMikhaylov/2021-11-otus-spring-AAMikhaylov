package ru.otus.hw18.controllers.rest;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.otus.hw18.dto.AuthorDto;
import ru.otus.hw18.dto.BookDto;
import ru.otus.hw18.dto.CommentDto;
import ru.otus.hw18.dto.GenreDto;
import ru.otus.hw18.exceptions.BadRequestException;
import ru.otus.hw18.exceptions.IntegrityViolationException;
import ru.otus.hw18.exceptions.NotFoundException;
import ru.otus.hw18.services.CommentService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentRestController {
    private final CommentService commentService;

    @HystrixCommand(commandKey = "commentKey", fallbackMethod = "listCommentFallback")
    @GetMapping("/api/comments")
    public List<CommentDto> getList() {
        return commentService.findAll();

    }

    @HystrixCommand(commandKey = "commentKey", fallbackMethod = "commentFallback")
    @GetMapping("/api/comments/{id}")
    public CommentDto getDetail(@PathVariable long id) {
        return commentService.findById(id).orElseThrow(() -> new NotFoundException("Комментарий c ID=" + id + " не найден!"));
    }

    @HystrixCommand(commandKey = "commentKey", fallbackMethod = "voidFallback")
    @DeleteMapping("/api/comments/{id}")
    public void delete(@PathVariable long id) {
        try {
            commentService.delete(id);
        } catch (DataIntegrityViolationException e) {
            throw new IntegrityViolationException("Нельзя удалить комментарий c ID=" + id + ". Нарушение целостности БД");
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Комментарий с ID=" + id + " не найден.");
        }
    }

    @HystrixCommand(commandKey = "commentKey", fallbackMethod = "idFallback")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/api/comments")
    public long add(@Valid @RequestBody CommentDto comment) {
        if (comment.getBook() == null) {
            throw new BadRequestException("У комментария должна быть книга.");
        }
        val savedComment = commentService.save(comment);
        return savedComment.getId();
    }

    @HystrixCommand(commandKey = "commentKey", fallbackMethod = "idFallback")
    @PutMapping("/api/comments/{id}")
    public long update(@PathVariable long id, @Valid @RequestBody CommentDto comment) {
        val toSaveComment = new CommentDto(id, comment.getUserName(), comment.getCommentDate(), comment.getBook(), comment.getContent());
        return commentService.save(toSaveComment).getId();
    }

    public List<CommentDto> listCommentFallback() {
        return List.of(commentFallback(0L));
    }

    public void voidFallback(long id) {
    }

    public long idFallback() {
        return 0L;
    }

    public long idFallback(CommentDto comment) {
        return 0L;
    }

    public long idFallback(long id, CommentDto comment) {
        return 0L;
    }

    public CommentDto commentFallback(long id) {
        val book = new BookDto(0L,
                new AuthorDto(0L, "N/A", "N/A", "N/A"), "N/A", "N/A",
                List.of(new GenreDto(0L, "N/A")),
                new ArrayList<>());
        return new CommentDto(id, "N/A", null, book, "N/A");
    }
}
