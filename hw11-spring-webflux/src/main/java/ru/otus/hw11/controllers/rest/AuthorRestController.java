package ru.otus.hw11.controllers.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw11.domain.Author;
import ru.otus.hw11.dto.AuthorDto;
import ru.otus.hw11.exceptions.IntegrityViolationException;
import ru.otus.hw11.exceptions.NotFoundException;
import ru.otus.hw11.repositories.react.AuthorReactRepository;
import ru.otus.hw11.repositories.react.BookReactRepository;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class AuthorRestController {
    private final AuthorReactRepository authorRepository;
    private final BookReactRepository bookRepository;

    @GetMapping("/api/authors")
    public Flux<AuthorDto> getList() {
        return authorRepository.findAll().map(AuthorDto::fromEntity);
    }

    @GetMapping("/api/authors/{id}")
    public Mono<AuthorDto> getDetail(@PathVariable String id) {
        return authorRepository.findById(id)
                .switchIfEmpty(Mono.error(() -> new NotFoundException("Автор c ID=" + id + " не найден!")))
                .map(AuthorDto::fromEntity);
    }

    @DeleteMapping("/api/authors/{id}")
    public Mono<Void> delete(@PathVariable String id) {
        return bookRepository.countBookByAuthorId(id)
                .flatMap(cntBook -> {
                    if (cntBook != 0)
                        return Mono.error(() -> new IntegrityViolationException("Нельзя удалить автора c ID=" + id + ". У автора есть книги."));
                    else
                        return Mono.empty();
                })
                .then(authorRepository.findById(id))
                .switchIfEmpty(Mono.error(new NotFoundException("Автор c ID=" + id + " не найден!")))
                .flatMap(authorRepository::delete);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/api/authors")
    public Mono<String> add(@Valid @RequestBody Mono<AuthorDto> author) {
        return author.map(AuthorDto::toEntity)
                .flatMap(authorRepository::save)
                .map(Author::getId);
    }

    @PutMapping("/api/authors/{id}")
    public Mono<String> update(@PathVariable String id, @Valid @RequestBody AuthorDto author) {
        return Mono.just(new AuthorDto(id, author.getFirstName(), author.getMiddleName(), author.getLastName()))
                .map(AuthorDto::toEntity)
                .flatMap(authorRepository::save)
                .map(Author::getId);
    }

}
