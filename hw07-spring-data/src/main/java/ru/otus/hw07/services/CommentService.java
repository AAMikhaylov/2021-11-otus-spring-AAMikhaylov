package ru.otus.hw07.services;

import ru.otus.hw07.models.Comment;

import java.util.List;
import java.util.Optional;


public interface CommentService {
    void delete(long id);

    Comment save(Comment comment);

    Optional<Comment> findById(long id);

    List<Comment> findAll();


}
