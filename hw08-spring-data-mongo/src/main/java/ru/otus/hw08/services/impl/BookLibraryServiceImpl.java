package ru.otus.hw08.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import ru.otus.hw08.io.IOMessageService;
import ru.otus.hw08.models.Author;
import ru.otus.hw08.models.Book;
import ru.otus.hw08.models.Comment;
import ru.otus.hw08.models.Genre;
import ru.otus.hw08.services.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BookLibraryServiceImpl implements BookLibraryService {
    private final BookService bookService;
    private final AuthorLibraryService authorLibraryService;
    private final GenreLibraryService genreLibraryService;
    private final CommentService commentService;
    private final IOMessageService ioMessageService;

    @Override
    public void createBook() {
        try {
            ioMessageService.writeLocal("messages.book.create.title");
            ioMessageService.writeLocal("messages.book.create.titleBook");
            val titleBook = ioMessageService.read();
            if (titleBook.equalsIgnoreCase("q")) {
                ioMessageService.writeLocal("messages.cancelOperation");
                return;
            }
            val author = authorLibraryService.selectAuthor();
            if (author.isEmpty())
                return;
            val genres = genreLibraryService.selectGenres();
            ioMessageService.writeLocal("messages.book.create.shortContent");
            val shortContent = ioMessageService.read();
            if (shortContent.equalsIgnoreCase("q")) {
                ioMessageService.writeLocal("messages.cancelOperation");
                return;
            }
            val book = new Book(author.get(), titleBook, shortContent, genres);
            val savedBook = bookService.save(book);
            ioMessageService.writeLocal("messages.book.create.complete", savedBook.getId());
        } catch (DataIntegrityViolationException e) {
            ioMessageService.writeLocal("messages.book.create.error.integrityViolation");
        } catch (Exception e) {
            ioMessageService.write(String.format("%s: %s\n", e.getClass().getSimpleName(), e.getMessage()));
        }
    }

    @Override
    public void outputAllBooks() {
        try {
            ioMessageService.writeLocal("messages.book.read.title");
            ioMessageService.writeLocal("messages.book.read.tableHeaders");
            val books = bookService.findAll();
            books.forEach(this::writeBookInfo);
        } catch (Exception e) {
            ioMessageService.write(String.format("%s: %s\n", e.getClass().getSimpleName(), e.getMessage()));
        }
    }

    @Override
    public void outputBookById() {
        try {
            ioMessageService.writeLocal("messages.book.readById.title");
            val bookOpt = selectBook();
            if (bookOpt.isEmpty())
                return;
            val book = bookOpt.get();
            ioMessageService.writeLocal("messages.book.read.tableHeaders");
            writeBookInfo(book);
            writeBookComments(book);
        } catch (Exception e) {
            ioMessageService.write(String.format("%s: %s\n", e.getClass().getSimpleName(), e.getMessage()));
        }
    }

    @Override
    public void updateBook() {
        try {
            ioMessageService.writeLocal("messages.book.update.title");
            val bookOpt = selectBook();
            if (bookOpt.isEmpty())
                return;
            val oldBook = bookOpt.get();
            ioMessageService.writeLocal("messages.book.update.titleBook", oldBook.getTitle());
            var titleBook = ioMessageService.read();
            if (titleBook.equalsIgnoreCase("q")) {
                ioMessageService.writeLocal("messages.cancelOperation");
                return;
            }
            titleBook = titleBook.trim().isEmpty() ? oldBook.getTitle() : titleBook;
            var author = oldBook.getAuthor();
            ioMessageService.writeLocal("messages.book.update.changeAuthor", author != null ? author.getShortName() : "");
            if (ioMessageService.read().equalsIgnoreCase("y")) {
                Optional<Author> authorOpt = authorLibraryService.selectAuthor();
                if (authorOpt.isEmpty()) {
                    return;
                }
                author = authorOpt.get();
            }
            List<Genre> genres = oldBook.getGenres();
            ioMessageService.writeLocal("messages.book.update.changeGenres", genres.stream().map(Genre::getName).collect(Collectors.joining(", ")));
            if (ioMessageService.read().equalsIgnoreCase("y")) {
                genres = genreLibraryService.selectGenres();
            }
            ioMessageService.writeLocal("messages.book.update.shortContent", oldBook.getShortContent());
            var shortContent = ioMessageService.read();
            if (shortContent.equalsIgnoreCase("q")) {
                ioMessageService.writeLocal("messages.cancelOperation");
                return;
            }
            shortContent = shortContent.trim().isEmpty() ? oldBook.getShortContent() : shortContent;
            val newBook = new Book(oldBook.getId(), author, titleBook, shortContent, genres);
            bookService.save(newBook);
            ioMessageService.writeLocal("messages.book.update.complete");
        } catch (DataIntegrityViolationException e) {
            ioMessageService.writeLocal("messages.book.update.error.integrityViolation");
        } catch (Exception e) {
            ioMessageService.write(String.format("%s: %s\n", e.getClass().getSimpleName(), e.getMessage()));
        }
    }

    @Override
    public void deleteBook() {
        try {
            ioMessageService.writeLocal("messages.book.delete.title");
            val bookOpt = selectBook();
            if (bookOpt.isEmpty())
                return;
            val book = bookOpt.get();
            bookService.delete(book.getId());
            ioMessageService.writeLocal("messages.book.delete.complete");
        } catch (Exception e) {
            ioMessageService.write(String.format("%s: %s\n", e.getClass().getSimpleName(), e.getMessage()));
        }
    }

    @Override
    public Optional<Book> selectBook() {
        while (true) {
            try {
                ioMessageService.writeLocal("messages.book.id");
                val bookId = ioMessageService.read();
                if (bookId.trim().equalsIgnoreCase("q")) {
                    ioMessageService.writeLocal("messages.cancelOperation");
                    return Optional.empty();
                }
                if (bookId.trim().equalsIgnoreCase("l")) {
                    outputAllBooks();
                    continue;
                }
                val bookOpt = bookService.findById(bookId);
                if (bookOpt.isEmpty()) {
                    ioMessageService.writeLocal("messages.book.read.error.notFound");
                    continue;
                }
                return bookOpt;
            } catch (Exception e) {
                ioMessageService.write(String.format("%s: %s\n", e.getClass().getSimpleName(), e.getMessage()));
            }
        }
    }


    private void writeBookInfo(Book book) {
        ioMessageService.writeLocal(
                "messages.book.info",
                book.getId(),
                book.getAuthor() != null ? book.getAuthor().getShortName() : "",
                book.getTitle(),
                book.getGenres() != null ? book.getGenres().stream().map(Genre::getName).collect(Collectors.joining(", ")) : "",
                book.getShortContent());
    }

    private void writeBookComments(Book book) {
        ioMessageService.writeLocal("messages.book.comments.header");
        List<Comment> comments = commentService.findAllByBookId(book.getId());
        for (Comment comment : comments) {
            ioMessageService.writeLocal("messages.book.comments.info", comment.getId(),
                    comment.getCommentDate(),
                    comment.getCommentDate(),
                    comment.getUserName(),
                    comment.getContent());
        }


    }


}
