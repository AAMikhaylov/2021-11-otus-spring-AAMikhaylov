package ru.otus.hw11.controllers.rest;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw11.domain.Book;
import ru.otus.hw11.domain.Comment;
import ru.otus.hw11.dto.BookDto;
import ru.otus.hw11.dto.BookFormDto;
import ru.otus.hw11.dto.CommentDto;
import ru.otus.hw11.dto.CommentFormDto;
import ru.otus.hw11.exceptions.NotFoundException;
import ru.otus.hw11.repositories.react.AuthorReactRepository;
import ru.otus.hw11.repositories.react.BookReactRepository;
import ru.otus.hw11.repositories.react.CommentReactRepository;
import ru.otus.hw11.repositories.react.GenreReactRepository;

import javax.validation.Valid;


@RestController
@RequiredArgsConstructor
public class BookRestController {
    private final BookReactRepository bookRepository;
    private final CommentReactRepository commentRepository;
    private final AuthorReactRepository authorRepository;
    private final GenreReactRepository genreRepository;

    @GetMapping("/api/books")
    public Flux<BookDto> getList() {
        return bookRepository.findAll()
                .map(BookDto::fromEntity);
    }

    @GetMapping("/api/books/{id}/comments")
    public Flux<CommentDto> getListBookComments(@PathVariable String id) {
        return commentRepository.findAllByBookId(id)
                .map(CommentDto::fromEntity);
    }

    @GetMapping("/api/books/{id}")
    public Mono<BookDto> getDetail(@PathVariable String id) {
        return bookRepository.findById(id)
                .switchIfEmpty(Mono.error(() -> new NotFoundException("Книга c ID=" + id + " не найдена!")))
                .map(BookDto::fromEntity);
    }

    @DeleteMapping("/api/books/{id}")
    public Mono<Void> delete(@PathVariable String id) {
        return bookRepository.findById(id)
                .switchIfEmpty(Mono.error(() -> new NotFoundException("Книга c ID=" + id + " не найдена!")))
                .flatMap(bookRepository::delete)
                .then(Mono.just(id))
                .flatMap(commentRepository::deleteAllByBookId);
    }

    //
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/api/books/{id}/comments")
    public Mono<String> addComment(@Valid @RequestBody CommentFormDto commentForm, @PathVariable String id) {
        return Mono.just(commentForm)
                .zipWith(bookRepository.findById(id)
                                .switchIfEmpty(Mono.error(() -> new NotFoundException("Книга c ID=" + id + " не найдена!"))),
                        (commentFrm, book) -> new Comment(commentFrm.getUserName(), book, commentFrm.getContent())
                )
                .flatMap(commentRepository::save)
                .map(Comment::getId);
    }


    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/api/books")
    public Mono<String> add(@Valid @RequestBody BookFormDto bookForm) {
        val genresMono = Flux.fromIterable(bookForm.getGenres())
                .flatMap(genreRepository::findById)
                .collectList();
        return Mono.just(bookForm)
                .zipWith(authorRepository.findById(bookForm.getAuthorId())
                                .switchIfEmpty(Mono.error(() -> new NotFoundException("Автор c ID=" + bookForm.getAuthorId() + " не найден!"))),
                        (bookFrm, author) -> new Book(bookFrm.getId(), author, bookFrm.getTitle(), bookFrm.getShortContent(), null))
                .zipWith(genresMono,
                        (book, genres) -> new Book(book.getId(), book.getAuthor(), book.getTitle(), book.getShortContent(), genres)
                )
                .flatMap(bookRepository::save)
                .map(Book::getId);
    }

    @PutMapping("/api/books/{id}")
    public Mono<String> update(@PathVariable String id, @Valid @RequestBody BookFormDto bookForm) {
        val genresMono = Flux.fromIterable(bookForm.getGenres())
                .flatMap(genreRepository::findById)
                .collectList();
        return Mono.just(bookForm)
                .zipWith(authorRepository.findById(bookForm.getAuthorId())
                                .switchIfEmpty(Mono.error(() -> new NotFoundException("Автор c ID=" + bookForm.getAuthorId() + " не найден!"))),
                        (bookFrm, author) -> new Book(id, author, bookFrm.getTitle(), bookFrm.getShortContent(), null))
                .zipWith(genresMono,
                        (book, genres) -> new Book(book.getId(), book.getAuthor(), book.getTitle(), book.getShortContent(), genres)
                )
                .flatMap(bookRepository::save)
                .map(Book::getId);
    }
}
