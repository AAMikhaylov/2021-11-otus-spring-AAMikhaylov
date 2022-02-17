package ru.otus.hw08.services;

import ru.otus.hw08.models.Comment;

import java.util.List;
import java.util.Optional;


public interface CommentService {
    void delete(String id);

    Comment save(Comment comment);

    Optional<Comment> findById(String id);

    List<Comment> findAllByBookId(String bookId);

    List<Comment> findAll();


}
