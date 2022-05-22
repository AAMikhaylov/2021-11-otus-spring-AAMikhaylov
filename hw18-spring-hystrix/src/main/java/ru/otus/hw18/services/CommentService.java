package ru.otus.hw18.services;

import ru.otus.hw18.dto.CommentDto;

import java.util.List;
import java.util.Optional;


public interface CommentService {
    void delete(long id);

    CommentDto save(CommentDto comment);

    Optional<CommentDto> findById(long id);

    List<CommentDto> findAll();

}
