package ru.otus.hw18.controllers.rest;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.otus.hw18.dto.AuthorDto;
import ru.otus.hw18.exceptions.IntegrityViolationException;
import ru.otus.hw18.exceptions.NotFoundException;
import ru.otus.hw18.services.AuthorService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class AuthorRestController {
    private final AuthorService authorService;

    @HystrixCommand(commandKey = "authorKey", fallbackMethod = "listAuthorFallback")
    @GetMapping("/api/authors")
    public List<AuthorDto> getList() {
        return authorService.findAll();
    }

    @HystrixCommand(commandKey = "authorKey", fallbackMethod = "authorFallback")
    @GetMapping("/api/authors/{id}")
    public AuthorDto getDetail(@PathVariable long id) {
        return authorService.findById(id).orElseThrow(() -> new NotFoundException("Автор c ID=" + id + " не найден!"));
    }

    @HystrixCommand(commandKey = "authorKey", fallbackMethod = "voidFallback")
    @DeleteMapping("/api/authors/{id}")
    public void delete(@PathVariable long id) {
        try {
            authorService.delete(id);
        } catch (DataIntegrityViolationException e) {
            throw new IntegrityViolationException("Нельзя удалить автора c ID=" + id + ". У автора есть книги.");
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Автор с ID=" + id + " не найден.");
        }
    }

    @HystrixCommand(commandKey = "authorKey", fallbackMethod = "idFallback")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/api/authors")
    public long add(@Valid @RequestBody AuthorDto author) {
        val savedAuthor = authorService.save(author);
        return savedAuthor.getId();
    }

    @HystrixCommand(commandKey = "authorKey", fallbackMethod = "idFallback")
    @PutMapping("/api/authors/{id}")
    public long update(@PathVariable long id, @Valid @RequestBody AuthorDto author) {
        val toSaveAuthor = new AuthorDto(id, author.getFirstName(), author.getMiddleName(), author.getLastName());
        val savedAuthor = authorService.save(toSaveAuthor);
        return savedAuthor.getId();
    }

    public List<AuthorDto> listAuthorFallback() {
        return List.of(authorFallback(0L));
    }

    public void voidFallback(long id) {
    }

    public long idFallback() {
        return 0L;
    }

    public long idFallback(AuthorDto author) {
        return 0L;
    }

    public long idFallback(long id, AuthorDto author) {
        return 0L;
    }

    public AuthorDto authorFallback(long id) {
        return new AuthorDto(id, "N/A", "N/A", "N/A");
    }

}
