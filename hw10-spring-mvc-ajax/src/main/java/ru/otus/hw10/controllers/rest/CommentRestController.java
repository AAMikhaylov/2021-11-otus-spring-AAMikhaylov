package ru.otus.hw10.controllers.rest;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.otus.hw10.dto.CommentDto;
import ru.otus.hw10.exceptions.BadRequestException;
import ru.otus.hw10.exceptions.IntegrityViolationException;
import ru.otus.hw10.exceptions.NotFoundException;
import ru.otus.hw10.services.CommentService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentRestController {
    private final CommentService commentService;

    @GetMapping("/api/comments")
    public List<CommentDto> getList() {
        return commentService.findAll();

    }

    @GetMapping("/api/comments/{id}")
    public CommentDto getDetail(@PathVariable long id) {
        return commentService.findById(id).orElseThrow(() -> new NotFoundException("Комментарий c ID=" + id + " не найден!"));

    }

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

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/api/comments")
    public long add(@Valid @RequestBody CommentDto comment) {
        if (comment.getBook() == null) {
            throw new BadRequestException("У комментария должна быть книга.");
        }
        val savedComment = commentService.save(comment);
        return savedComment.getId();
    }

    @PutMapping("/api/comments/{id}")
    public long update(@PathVariable long id, @Valid @RequestBody CommentDto comment) {
        val toSaveComment = new CommentDto(id, comment.getUserName(), comment.getCommentDate(), comment.getBook(), comment.getContent());
        return commentService.save(toSaveComment).getId();
    }
}
