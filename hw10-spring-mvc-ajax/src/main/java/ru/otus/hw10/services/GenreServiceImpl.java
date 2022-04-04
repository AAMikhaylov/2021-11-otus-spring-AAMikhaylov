package ru.otus.hw10.services;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw10.domain.Genre;
import ru.otus.hw10.dto.GenreDto;
import ru.otus.hw10.repositories.GenreRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {
    private final GenreRepository genreRepository;

    @Override
    @Transactional
    public GenreDto save(GenreDto genre) {
        val genreEntity = genre.toEntity();
        return GenreDto.fromEntity(genreRepository.save(genreEntity));
    }

    @Override
    public Optional<GenreDto> findById(long id) {
        return genreRepository.findById(id).map(GenreDto::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GenreDto> findAll() {
        List<Genre> genreEntities = genreRepository.findAll();
        return genreEntities.stream()
                .map(GenreDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(long id) {
        genreRepository.deleteById(id);
    }

}
