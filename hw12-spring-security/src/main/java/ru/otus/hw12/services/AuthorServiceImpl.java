package ru.otus.hw12.services;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw12.domain.Author;
import ru.otus.hw12.dto.AuthorDto;
import ru.otus.hw12.repositories.AuthorRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository authorRepository;

    @Override
    @Transactional
    public AuthorDto save(AuthorDto author) {
        val authorEntity = author.toEntity();
        return AuthorDto.fromEntity(authorRepository.save(authorEntity));
    }

    @Override
    public Optional<AuthorDto> findById(long id) {
        return authorRepository.findById(id).map(AuthorDto::fromEntity);

    }

    @Override
    @Transactional(readOnly = true)
    public List<AuthorDto> findAll() {
        List<Author> authorEntities = authorRepository.findAll();
        return authorEntities.stream()
                .map(AuthorDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(long id) {
        authorRepository.deleteById(id);
    }
}


