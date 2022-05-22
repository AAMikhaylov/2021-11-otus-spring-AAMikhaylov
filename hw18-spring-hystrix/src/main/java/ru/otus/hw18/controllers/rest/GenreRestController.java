package ru.otus.hw18.controllers.rest;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.otus.hw18.dto.GenreDto;
import ru.otus.hw18.exceptions.IntegrityViolationException;
import ru.otus.hw18.exceptions.NotFoundException;
import ru.otus.hw18.services.GenreService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class GenreRestController {
    private final GenreService genreService;

    @HystrixCommand(commandKey = "genreKey", fallbackMethod = "listGenreFallback")
    @GetMapping("/api/genres")
    public List<GenreDto> getList() {
        return genreService.findAll();
    }

    @HystrixCommand(commandKey = "genreKey", fallbackMethod = "genreFallback")
    @GetMapping("/api/genres/{id}")
    public GenreDto getDetail(@PathVariable long id) {
        return genreService.findById(id).orElseThrow(() -> new NotFoundException("Жанр c ID=" + id + " не найден!"));

    }

    @HystrixCommand(commandKey = "genreKey", fallbackMethod = "voidFallback")
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

    @HystrixCommand(commandKey = "genreKey", fallbackMethod = "idFallback")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/api/genres")
    public long add(@Valid @RequestBody GenreDto genre) {
        val savedGenre = genreService.save(genre);
        return savedGenre.getId();
    }

    @HystrixCommand(commandKey = "genreKey", fallbackMethod = "idFallback")
    @PutMapping("/api/genres/{id}")
    public long update(@PathVariable long id, @Valid @RequestBody GenreDto genre) {
        val toSaveGenre = new GenreDto(id, genre.getName());
        val savedGenre = genreService.save(toSaveGenre);
        return savedGenre.getId();
    }

    public List<GenreDto> listGenreFallback() {
        return List.of(genreFallback(0L));
    }

    public void voidFallback(long id) {
    }

    public long idFallback() {
        return 0L;
    }

    public long idFallback(GenreDto genre) {
        return 0L;
    }

    public long idFallback(long id, GenreDto genre) {
        return 0L;
    }

    public GenreDto genreFallback(long id) {
        return new GenreDto(id, "N/A");
    }
}
