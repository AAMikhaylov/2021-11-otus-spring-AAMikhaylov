package ru.otus.hw08.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import ru.otus.hw08.models.Comment;
import ru.otus.hw08.repositories.BookRepository;
import ru.otus.hw08.repositories.CommentRepository;
import ru.otus.hw08.services.CommentService;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final BookRepository bookRepository;

    @Override
    public Comment save(Comment comment) {
        bookRepository.findById(comment.getBook().getId()).orElseThrow(() -> new DataIntegrityViolationException("Book isn't present"));
        var commentWithDate = new Comment(comment.getId(), comment.getUserName(), comment.getBook(), comment.getContent(), new Date());
        return commentRepository.save(commentWithDate);
    }

    @Override
    public Optional<Comment> findById(String id) {
        return commentRepository.findById(id);
    }


    @Override
    public List<Comment> findAll() {
        return commentRepository.findAll();
    }

    @Override
    public List<Comment> findAllByBookId(String bookId) {
        return commentRepository.findAllByBookId(bookId);
    }

    @Override
    public void delete(String id) {
        commentRepository.deleteById(id);
    }
}
