package ru.otus.hw08.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import ru.otus.hw08.models.Genre;
import ru.otus.hw08.repositories.BookRepository;
import ru.otus.hw08.repositories.GenreRepository;
import ru.otus.hw08.services.GenreService;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {
    private final GenreRepository genreRepository;
    private final BookRepository bookRepository;

    @Override
    public Genre save(Genre genre) {
        return genreRepository.save(genre);
    }

    @Override
    public Optional<Genre> findById(String id) {
        return genreRepository.findById(id);
    }

    @Override
    public List<Genre> findAll() {
        return genreRepository.findAll();
    }

    @Override
    public void delete(String id) {
        if (bookRepository.countBookByGenresId(id) > 0) {
            throw new DataIntegrityViolationException("Can't delete genre. Genre has book(s)");
        }
        genreRepository.deleteById(id);
    }

    @Override
    public Genre cascadeUpdate(Genre genre) {
        return genreRepository.cascadeUpdate(genre);
    }
}
