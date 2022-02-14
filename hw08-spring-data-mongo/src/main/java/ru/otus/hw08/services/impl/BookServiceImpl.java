package ru.otus.hw08.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import ru.otus.hw08.models.Book;
import ru.otus.hw08.models.Genre;
import ru.otus.hw08.repositories.AuthorRepository;
import ru.otus.hw08.repositories.BookRepository;
import ru.otus.hw08.repositories.CommentRepository;
import ru.otus.hw08.repositories.GenreRepository;
import ru.otus.hw08.services.BookService;

import java.util.*;

@RequiredArgsConstructor
@Component
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final CommentRepository commentRepository;
    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;

    @Override
    public Book save(Book book) {
        authorRepository.findById(book.getAuthor().getId())
                .orElseThrow(() -> new DataIntegrityViolationException("Author isn't present"));

        Map<String, Genre> changeGenres = new HashMap<>();
        for (var genre : book.getGenres()) {
            val storedGenre = genreRepository.findById(genre.getId()).orElseThrow(() -> new DataIntegrityViolationException("Genre isn't present"));
            if (!genre.getName().equals(storedGenre.getName())) {
                changeGenres.put(storedGenre.getId(), storedGenre);
            }
        }
        if (!changeGenres.isEmpty()) {
            book.getGenres().forEach(g -> changeGenres.putIfAbsent(g.getId(), g));
            book.getGenres().clear();
            book.getGenres().addAll(changeGenres.values());
        }
        return bookRepository.save(book);
    }

    @Override
    public Optional<Book> findById(String id) {
        return bookRepository.findById(id);
    }

    @Override
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    @Override
    public void delete(String id) {
        bookRepository.deleteById(id);
        commentRepository.deleteAllByBookId(id);
    }
}
