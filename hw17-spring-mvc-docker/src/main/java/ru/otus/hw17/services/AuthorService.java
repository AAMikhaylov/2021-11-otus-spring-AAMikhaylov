package ru.otus.hw17.services;
import ru.otus.hw17.dto.AuthorDto;

import java.util.List;
import java.util.Optional;

public interface AuthorService {
    void delete(long id);

    AuthorDto save(AuthorDto author);

    Optional<AuthorDto> findById(long id);

    List<AuthorDto> findAll();

}
