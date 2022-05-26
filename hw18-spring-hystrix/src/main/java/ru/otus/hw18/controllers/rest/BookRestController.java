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
import ru.otus.hw18.exceptions.IntegrityViolationException;
import ru.otus.hw18.exceptions.NotFoundException;
import ru.otus.hw18.services.BookService;
import ru.otus.hw18.services.CommentService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RestController
@RequiredArgsConstructor
public class BookRestController {
    private final BookService bookService;
    private final CommentService commentService;

    @HystrixCommand(commandKey = "bookKey", fallbackMethod = "listBookFallback")
    @GetMapping("/api/books")
    public List<BookDto> getList() {
        simulateFallback();
        return bookService.findAll();

    }

    @HystrixCommand(commandKey = "bookKey", fallbackMethod = "bookFallback")
    @GetMapping("/api/books/{id}")
    public BookDto getDetail(@PathVariable long id) {
        simulateFallback();
        return bookService.findByIdEager(id).orElseThrow(() -> new NotFoundException("Книга c ID=" + id + " не найдена!"));
    }

    @HystrixCommand(commandKey = "bookKey", fallbackMethod = "voidFallback")
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

    @HystrixCommand(commandKey = "bookKey", fallbackMethod = "idFallback")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/api/books/{id}/comments")
    public long addComment(@Valid @RequestBody CommentDto comment, @PathVariable long id) {
        val book = bookService.findByIdLazy(id).orElseThrow(() -> new NotFoundException("Книга c ID=" + id + " не найдена!"));
        val toSaveComment = new CommentDto(0L, comment.getUserName(), null, book, comment.getContent());
        return commentService.save(toSaveComment).getId();
    }

    @HystrixCommand(commandKey = "bookKey", fallbackMethod = "idFallback")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/api/books")
    public long add(@Valid @RequestBody BookDto book) {
        return bookService.save(book).getId();

    }

    @HystrixCommand(commandKey = "bookKey", fallbackMethod = "idFallback")
    @PutMapping("/api/books/{id}")
    public long update(@PathVariable long id, @Valid @RequestBody BookDto book) {
        val toSaveBook = new BookDto(id, book.getAuthor(), book.getTitle(), book.getShortContent(), book.getGenres(), null);
        return bookService.save(toSaveBook).getId();
    }

    public List<BookDto> listBookFallback() {
        return List.of(bookFallback(0L));
    }

    public void voidFallback(long id) {
    }

    public long idFallback() {
        return 0L;
    }

    public long idFallback(CommentDto comment, long id) {
        return 0L;
    }

    public long idFallback(BookDto book) {
        return 0L;
    }

    public long idFallback(long id, BookDto book) {
        return 0L;
    }

    public BookDto bookFallback(long id) {
        return new BookDto(id,
                new AuthorDto(0L, "N/A", "N/A", "N/A"), "N/A", "N/A",
                List.of(new GenreDto(0L, "N/A")),
                new ArrayList<>());
    }

    public void simulateFallback() {
        Random rand = new Random();
        int randomNum = rand.nextInt(4);
        if (randomNum == 3)
            try {
                Thread.sleep(3 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }
}
