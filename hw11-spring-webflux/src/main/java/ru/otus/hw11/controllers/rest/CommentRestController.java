package ru.otus.hw11.controllers.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw11.domain.Comment;
import ru.otus.hw11.dto.CommentDto;

import ru.otus.hw11.dto.CommentFormDto;
import ru.otus.hw11.exceptions.NotFoundException;
import ru.otus.hw11.repositories.react.BookReactRepository;
import ru.otus.hw11.repositories.react.CommentReactRepository;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class CommentRestController {
    private final CommentReactRepository commentRepository;
    private final BookReactRepository bookRepository;

    @GetMapping("/api/comments")
    public Flux<CommentDto> getList() {
        return commentRepository.findAll()
                .map(CommentDto::fromEntity);
    }

    @GetMapping("/api/comments/{id}")
    public Mono<CommentDto> getDetail(@PathVariable String id) {
        return commentRepository.findById(id)
                .switchIfEmpty(Mono.error(() -> new NotFoundException("Комментарий c ID=" + id + " не найден!")))
                .map(CommentDto::fromEntity);
    }

    @DeleteMapping("/api/comments/{id}")
    public Mono<Void> delete(@PathVariable String id) {
        return commentRepository.findById(id)
                .switchIfEmpty(Mono.error(() -> new NotFoundException("Комментарий c ID=" + id + " не найден!")))
                .flatMap(commentRepository::delete);

    }

    @PutMapping("/api/comments/{id}")
    public Mono<String> update(@PathVariable String id, @Valid @RequestBody CommentFormDto commentForm) {

        return commentRepository.findById(id)
                .switchIfEmpty(Mono.error(() -> new NotFoundException("Комментарий c ID=" + id + " не найден!")))
                .map(Comment::getId)
                .zipWith(bookRepository.findById(commentForm.getBookId())
                                .switchIfEmpty(Mono.error(() -> new NotFoundException("Книга c ID=" + id + " не найдена!"))),
                        (commentId, book) -> new Comment(commentId, commentForm.getUserName(), book, commentForm.getContent())
                )
                .flatMap(commentRepository::save)
                .map(Comment::getId);
    }
}
