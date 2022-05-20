package ru.otus.hw17.controllers.rest;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.otus.hw17.dto.GenreDto;
import ru.otus.hw17.exceptions.IntegrityViolationException;
import ru.otus.hw17.exceptions.NotFoundException;
import ru.otus.hw17.services.GenreService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class GenreRestController {
    private final GenreService genreService;

    @GetMapping("/api/genres")
    public List<GenreDto> getList() {
        return genreService.findAll();

    }

    @GetMapping("/api/genres/{id}")
    public GenreDto getDetail(@PathVariable long id) {
        return genreService.findById(id).orElseThrow(() -> new NotFoundException("Жанр c ID=" + id + " не найден!"));

    }

    @DeleteMapping("/api/genres/{id}")
    public void delete(@PathVariable long id) {
        try {
            genreService.delete(id);
        } catch (DataIntegrityViolationException e) {
            throw new IntegrityViolationException("Нельзя удалить жанр c ID=" + id + ". У жанра есть книги.");
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Жанр с ID=" + id + " не найден.");
        }
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/api/genres")
    public long add(@Valid @RequestBody GenreDto genre) {
        val savedGenre = genreService.save(genre);
        return savedGenre.getId();
    }

    @PutMapping("/api/genres/{id}")
    public long update(@PathVariable long id, @Valid @RequestBody GenreDto genre) {
        val toSaveGenre = new GenreDto(id, genre.getName());
        val savedGenre = genreService.save(toSaveGenre);
        return savedGenre.getId();
    }


}
