package ru.otus.hw10.controllers.rest;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.otus.hw10.dto.BookDto;
import ru.otus.hw10.dto.CommentDto;

import ru.otus.hw10.exceptions.IntegrityViolationException;
import ru.otus.hw10.exceptions.NotFoundException;
import ru.otus.hw10.services.BookService;
import ru.otus.hw10.services.CommentService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class BookRestController {
    private final BookService bookService;
    private final CommentService commentService;

    @GetMapping("/api/books")
    public List<BookDto> getList() {
        return bookService.findAll();

    }

    @GetMapping("/api/books/{id}")
    public BookDto getDetail(@PathVariable long id) {
        return bookService.findByIdEager(id).orElseThrow(() -> new NotFoundException("Книга c ID=" + id + " не найдена!"));
    }

    @DeleteMapping("/api/books/{id}")
    public void delete(@PathVariable long id) {
        try {
            bookService.delete(id);
        } catch (DataIntegrityViolationException e) {
            throw new IntegrityViolationException("Нельзя удалить книгу c ID=" + id + ". Нарушение целостности БД");
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Книга с ID=" + id + " не найдена.");
        }
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/api/books/{id}/comments")
    public long addComment(@Valid @RequestBody CommentDto comment, @PathVariable long id) {
        val book = bookService.findByIdLazy(id).orElseThrow(() -> new NotFoundException("Книга c ID=" + id + " не найдена!"));
        val toSaveComment = new CommentDto(0L, comment.getUserName(), null, book, comment.getContent());
        return commentService.save(toSaveComment).getId();
    }


    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/api/books")
    public long add(@Valid @RequestBody BookDto book) {
        return  bookService.save(book).getId();

    }

    @PutMapping("/api/books/{id}")
    public long update(@PathVariable long id, @Valid @RequestBody BookDto book) {
        val toSaveBook = new BookDto(id, book.getAuthor(),book.getTitle(), book.getShortContent(), book.getGenres(),null);
        return bookService.save(toSaveBook).getId();
    }


}
