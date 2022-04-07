package ru.otus.hw10.services;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw10.domain.Comment;
import ru.otus.hw10.dto.CommentDto;
import ru.otus.hw10.repositories.CommentRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public CommentDto save(CommentDto comment) {
        val commentEntity = comment.toEntity();
        return CommentDto.fromEntity(commentRepository.save(commentEntity));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CommentDto> findById(long id) {
        return commentRepository.findById(id).map(CommentDto::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> findAll() {
        List<Comment> commentEntities = commentRepository.findAll();
        return commentEntities.stream()
                .map(CommentDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(long id) {
        commentRepository.deleteById(id);
    }
}
