package ru.otus.hw08.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import ru.otus.hw08.io.IOMessageService;
import ru.otus.hw08.models.Comment;
import ru.otus.hw08.services.BookLibraryService;
import ru.otus.hw08.services.CommentLibraryService;
import ru.otus.hw08.services.CommentService;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CommentLibraryServiceImpl implements CommentLibraryService {
    private final CommentService commentService;
    private final BookLibraryService bookLibraryService;
    private final IOMessageService ioMessageService;

    @Override
    public void outputAllComments() {
        try {
            ioMessageService.writeLocal("messages.comment.read.title");
            val comments = commentService.findAll();
            comments.forEach(this::writeComment);
        } catch (Exception e) {
            ioMessageService.write(String.format("%s: %s\n", e.getClass().getSimpleName(), e.getMessage()));
        }
    }

    @Override
    public void outputCommentByBook() {
        try {
            ioMessageService.writeLocal("messages.comment.readByBook.title");
            val bookOpt = bookLibraryService.selectBook();
            if (bookOpt.isEmpty()) {
                return;
            }
            val comments = commentService.findAllByBookId(bookOpt.get().getId());
            comments.forEach(this::writeComment);

        } catch (Exception e) {
            ioMessageService.write(String.format("%s: %s\n", e.getClass().getSimpleName(), e.getMessage()));
        }
    }


    @Override
    public void outputCommentById() {
        try {
            ioMessageService.writeLocal("messages.comment.readById.title");
            val commentOpt = selectComment();
            if (commentOpt.isEmpty())
                return;
            val comment = commentOpt.get();
            writeComment(comment);
        } catch (Exception e) {
            ioMessageService.write(String.format("%s: %s\n", e.getClass().getSimpleName(), e.getMessage()));
        }
    }

    @Override
    public void createComment() {
        try {
            ioMessageService.writeLocal("messages.comment.create.title");
            val bookOpt = bookLibraryService.selectBook();
            if (bookOpt.isEmpty()) {
                return;
            }
            ioMessageService.writeLocal("messages.comment.create.userName");
            val userName = ioMessageService.read();
            if (userName.equalsIgnoreCase("q")) {
                ioMessageService.writeLocal("messages.cancelOperation");
                return;
            }
            ioMessageService.writeLocal("messages.comment.create.comment");
            val commentText = ioMessageService.read();
            if (commentText.equalsIgnoreCase("q")) {
                ioMessageService.writeLocal("messages.cancelOperation");
                return;
            }
            val comment = new Comment(userName, bookOpt.get(), commentText);
            val savedComment = commentService.save(comment);
            ioMessageService.writeLocal("messages.comment.create.complete", savedComment.getId());
        } catch (DataIntegrityViolationException e) {
            ioMessageService.writeLocal("messages.comment.create.error.integrityViolation");
        } catch (Exception e) {
            ioMessageService.write(String.format("%s: %s\n", e.getClass().getSimpleName(), e.getMessage()));
        }
    }

    @Override
    public void updateComment() {
        try {
            ioMessageService.writeLocal("messages.comment.update.title");
            val commentOpt = selectComment();
            if (commentOpt.isEmpty())
                return;
            val oldComment = commentOpt.get();
            ioMessageService.writeLocal("messages.comment.update.userName", oldComment.getUserName());
            var userName = ioMessageService.read();
            if (userName.equalsIgnoreCase("q")) {
                ioMessageService.writeLocal("messages.cancelOperation");
                return;
            }
            userName = userName.trim().isEmpty() ? oldComment.getUserName() : userName;
            ioMessageService.writeLocal("messages.comment.update.comment", oldComment.getContent());
            var commentText = ioMessageService.read();
            if (commentText.equalsIgnoreCase("q")) {
                ioMessageService.writeLocal("messages.cancelOperation");
                return;
            }
            commentText = commentText.trim().isEmpty() ? oldComment.getContent() : commentText;
            val newComment = new Comment(oldComment.getId(), userName, oldComment.getBook(), commentText);
            commentService.save(newComment);
            ioMessageService.writeLocal("messages.comment.update.complete");
        } catch (DataIntegrityViolationException e) {
            ioMessageService.writeLocal("messages.comment.update.error.integrityViolation");
        } catch (Exception e) {
            ioMessageService.write(String.format("%s: %s\n", e.getClass().getSimpleName(), e.getMessage()));
        }
    }

    @Override
    public void deleteComment() {
        try {
            ioMessageService.writeLocal("messages.comment.delete.title");
            val commentOpt = selectComment();
            if (commentOpt.isEmpty())
                return;
            val comment = commentOpt.get();
            commentService.delete(comment.getId());
            ioMessageService.writeLocal("messages.comment.delete.complete");
        } catch (Exception e) {
            ioMessageService.write(String.format("%s: %s\n", e.getClass().getSimpleName(), e.getMessage()));
        }
    }

    @Override
    public Optional<Comment> selectComment() {
        while (true) {
            try {
                ioMessageService.writeLocal("messages.comment.id");
                val commentId = ioMessageService.read();
                if (commentId.trim().equalsIgnoreCase("q")) {
                    ioMessageService.writeLocal("messages.cancelOperation");
                    return Optional.empty();
                }
                if (commentId.trim().equalsIgnoreCase("l")) {
                    outputAllComments();
                    continue;
                }
                val commentOpt = commentService.findById(commentId);
                if (commentOpt.isEmpty()) {
                    ioMessageService.writeLocal("messages.comment.read.error.notFound");
                    continue;
                }
                return commentOpt;
            } catch (Exception e) {
                ioMessageService.write(String.format("%s: %s\n", e.getClass().getSimpleName(), e.getMessage()));
            }
        }
    }

    private void writeComment(Comment comment) {
        ioMessageService.writeLocal("messages.comment.info",
                comment.getId(),
                comment.getCommentDate(),
                comment.getCommentDate(),
                comment.getUserName(),
                comment.getBook() != null ? (comment.getBook().getAuthor() != null ? comment.getBook().getAuthor().getShortName() : "") : "",
                comment.getBook() != null ? comment.getBook().getTitle() : "",
                comment.getContent()
        );
    }
}
