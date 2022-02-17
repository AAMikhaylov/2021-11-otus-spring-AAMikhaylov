package ru.otus.hw07.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw07.models.Author;
import ru.otus.hw07.models.Book;
import ru.otus.hw07.models.Comment;
import ru.otus.hw07.repositories.CommentRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public Comment save(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Comment> findById(long id) {
        return commentRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> findAll() {
        return commentRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> findAllByBookId(long bookId) {
        var commentExample = new Comment(null,
                new Book(bookId, new Author(null, null, null), null, null, null, null),
                null);
        var matcher = ExampleMatcher.matching()
                .withIgnorePaths("id", "book.author");
        var example = Example.of(commentExample, matcher);
        return commentRepository.findAll(example);
    }

    @Override
    @Transactional
    public void delete(long id) {
        commentRepository.deleteById(id);
    }
}
