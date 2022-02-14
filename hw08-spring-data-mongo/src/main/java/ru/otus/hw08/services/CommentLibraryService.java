package ru.otus.hw08.services;

import ru.otus.hw08.models.Comment;

import java.util.Optional;

public interface CommentLibraryService {

    void outputAllComments();

    void outputCommentById();

    void outputCommentByBook();

    void createComment();

    void updateComment();

    void deleteComment();

    Optional<Comment> selectComment();

}
