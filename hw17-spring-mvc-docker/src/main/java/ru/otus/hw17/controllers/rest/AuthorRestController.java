package ru.otus.hw17.controllers.rest;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.otus.hw17.dto.AuthorDto;
import ru.otus.hw17.exceptions.IntegrityViolationException;
import ru.otus.hw17.exceptions.NotFoundException;
import ru.otus.hw17.services.AuthorService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class AuthorRestController {
    private final AuthorService authorService;

    @GetMapping("/api/authors")
    public List<AuthorDto> getList() {
        return authorService.findAll();

    }

    @GetMapping("/api/authors/{id}")
    public AuthorDto getDetail(@PathVariable long id) {
        return authorService.findById(id).orElseThrow(() -> new NotFoundException("Автор c ID=" + id + " не найден!"));
    }

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

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/api/authors")
    public long add(@Valid @RequestBody AuthorDto author) {
        val savedAuthor = authorService.save(author);
        return savedAuthor.getId();
    }

    @PutMapping("/api/authors/{id}")
    public long update(@PathVariable long id, @Valid @RequestBody AuthorDto author) {
        val toSaveAuthor = new AuthorDto(id, author.getFirstName(), author.getMiddleName(), author.getLastName());
        val savedAuthor = authorService.save(toSaveAuthor);
        return savedAuthor.getId();
     }

}
