package ru.otus.hw11.controllers.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw11.domain.Genre;
import ru.otus.hw11.dto.GenreDto;
import ru.otus.hw11.exceptions.IntegrityViolationException;
import ru.otus.hw11.exceptions.NotFoundException;
import ru.otus.hw11.repositories.react.BookReactRepository;
import ru.otus.hw11.repositories.react.GenreReactRepository;

import javax.validation.Valid;


@RestController
@RequiredArgsConstructor
public class GenreRestController {
    private final GenreReactRepository genreRepository;
    private final BookReactRepository bookRepository;

    @GetMapping("/api/genres")
    public Flux<GenreDto> getList() {
        return genreRepository.findAll()
                .map(GenreDto::fromEntity);
    }

    @GetMapping("/api/genres/{id}")
    public Mono<GenreDto> getDetail(@PathVariable String id) {
        return genreRepository.findById(id)
                .switchIfEmpty(Mono.error(() -> new NotFoundException("Жанр c ID=" + id + " не найден!")))
                .map(GenreDto::fromEntity);
    }

    @DeleteMapping("/api/genres/{id}")
    public Mono<Void> delete(@PathVariable String id) {
        return bookRepository.countBookByGenresId(id)
                .flatMap(cntBook -> {
                    if (cntBook != 0)
                        return Mono.error(() -> new IntegrityViolationException("Нельзя удалить жанр c ID=" + id + ". У жанра есть книги."));
                    else
                        return Mono.empty();
                })
                .then(genreRepository.findById(id))
                .switchIfEmpty(Mono.error(() -> new NotFoundException("Жанр c ID=" + id + " не найден!")))
                .flatMap(genreRepository::delete);

    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/api/genres")
    public Mono<String> add(@Valid @RequestBody GenreDto genre) {
        return Mono.just(genre)
                .map(GenreDto::toEntity)
                .flatMap(genreRepository::save)
                .map(Genre::getId);
    }

    @PutMapping("/api/genres/{id}")
    public Mono<String> update(@PathVariable String id, @Valid @RequestBody GenreDto genre) {
        return genreRepository.findById(id)
                .switchIfEmpty(Mono.error(() -> new NotFoundException("Жанр c ID=" + id + " не найден!")))
                .map(Genre::getId)
                .zipWith(Mono.just(genre),
                        (genreId, genreDto) -> new Genre(genreId, genreDto.getName())
                )
                .flatMap(genreRepository::cascadeUpdate)
                .map(Genre::getId);
    }
}
