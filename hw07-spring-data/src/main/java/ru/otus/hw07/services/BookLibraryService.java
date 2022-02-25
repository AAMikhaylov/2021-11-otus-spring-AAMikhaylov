package ru.otus.hw07.services;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Component;
import ru.otus.hw07.io.IOMessageService;
import ru.otus.hw07.models.Author;
import ru.otus.hw07.models.Book;
import ru.otus.hw07.models.Comment;
import ru.otus.hw07.models.Genre;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BookLibraryService {
    private final BookService bookService;
    private final AuthorLibraryService authorLibraryService;
    private final GenreLibraryService genreLibraryService;
    private final IOMessageService ioMessageService;


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
            val book = new Book(author.get(), titleBook, shortContent, genres, new ArrayList<>());
            val savedBook = bookService.save(book);
            ioMessageService.writeLocal("messages.book.create.complete", Long.toString(savedBook.getId()));
        } catch (Exception e) {
            ioMessageService.write(e.getMessage());
        }
    }

    public void outputAllBooks() {
        try {
            ioMessageService.writeLocal("messages.book.read.title");
            ioMessageService.writeLocal("messages.book.read.tableHeaders");
            val books = bookService.findAll();
            books.forEach(this::writeBookInfo);
        } catch (Exception e) {
            ioMessageService.write(e.getMessage());
        }
    }

    public void outputBookById() {

        try {
            ioMessageService.writeLocal("messages.book.readById.title");
            val bookOpt = selectBook(false);
            if (bookOpt.isEmpty())
                return;
            val book = bookOpt.get();
            ioMessageService.writeLocal("messages.book.read.tableHeaders");
            writeBookInfoWithComments(book);
        } catch (Exception e) {
            ioMessageService.write(e.getMessage());
        }
    }

    public void updateBook() {
        try {
            ioMessageService.writeLocal("messages.book.update.title");
            val bookOpt = selectBook(true);
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
            ioMessageService.writeLocal("messages.book.update.changeAuthor", author.getShortName());
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
            val newBook = new Book(oldBook.getId(), author, titleBook, shortContent, genres, oldBook.getComments());
            bookService.save(newBook);
            ioMessageService.writeLocal("messages.book.update.complete");
        } catch (Exception e) {
            ioMessageService.write(e.getMessage());
        }
    }


    public void deleteBook() {
        try {
            ioMessageService.writeLocal("messages.book.delete.title");
            val bookOpt = selectBook(true);
            if (bookOpt.isEmpty())
                return;
            val book = bookOpt.get();
            bookService.delete(book.getId());
            ioMessageService.writeLocal("messages.book.delete.complete");
        } catch (Exception e) {
            ioMessageService.write(e.getMessage());
        }
    }

    public Optional<Book> selectBook(boolean lazy) {
        while (true) {
            try {
                ioMessageService.writeLocal("messages.book.id");
                val strBookId = ioMessageService.read();
                if (strBookId.trim().equalsIgnoreCase("q")) {
                    ioMessageService.writeLocal("messages.cancelOperation");
                    return Optional.empty();
                }
                if (strBookId.trim().equalsIgnoreCase("l")) {
                    outputAllBooks();
                    continue;
                }
                try {
                    long bookId = Long.parseLong(strBookId);
                    val bookOpt = (lazy ? bookService.findByIdLazy(bookId) : bookService.findByIdEager(bookId));
                    if (bookOpt.isEmpty()) {
                        ioMessageService.writeLocal("messages.book.read.error.notFound");
                        continue;
                    }
                    return bookOpt;
                } catch (NumberFormatException e) {
                    ioMessageService.writeLocal("messages.book.error.idFormat");
                }
            } catch (Exception e) {
                ioMessageService.write(e.getMessage());
            }
        }
    }


    private void writeBookInfo(Book book) {
        ioMessageService.writeLocal(
                "messages.book.info",
                Long.toString(book.getId()),
                book.getAuthor().getShortName(),
                book.getTitle(),
                book.getGenres().stream().map(Genre::getName).collect(Collectors.joining(", ")),
                book.getShortContent());
    }

    private void writeBookInfoWithComments(Book book) {
        writeBookInfo(book);
        ioMessageService.writeLocal("messages.book.comments.header");
        List<Comment> comments = book.getComments();
        for (Comment comment : comments) {
            ioMessageService.writeLocal("messages.book.comments.info", Long.toString(comment.getId()),
                    comment.getCommentDate(),
                    comment.getCommentDate(),
                    comment.getUserName(),
                    comment.getContent());
        }


    }

}
