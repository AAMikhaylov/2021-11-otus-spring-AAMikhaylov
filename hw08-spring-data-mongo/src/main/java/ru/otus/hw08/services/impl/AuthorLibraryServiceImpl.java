package ru.otus.hw08.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import ru.otus.hw08.io.IOMessageService;
import ru.otus.hw08.models.Author;
import ru.otus.hw08.services.AuthorLibraryService;
import ru.otus.hw08.services.AuthorService;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuthorLibraryServiceImpl implements AuthorLibraryService {
    private final AuthorService authorService;
    private final IOMessageService ioMessageService;

    @Override
    public void createAuthor() {
        try {
            ioMessageService.writeLocal("messages.author.create.title");
            ioMessageService.writeLocal("messages.author.create.firstName");
            val firstName = ioMessageService.read();
            if (firstName.equalsIgnoreCase("q")) {
                ioMessageService.writeLocal("messages.cancelOperation");
                return;
            }
            ioMessageService.writeLocal("messages.author.create.middleName");
            val middleName = ioMessageService.read();
            if (middleName.equalsIgnoreCase("q")) {
                ioMessageService.writeLocal("messages.cancelOperation");
                return;
            }
            ioMessageService.writeLocal("messages.author.create.lastName");
            val lastName = ioMessageService.read();
            if (lastName.equalsIgnoreCase("q")) {
                ioMessageService.writeLocal("messages.cancelOperation");
                return;
            }
            val author = new Author(firstName, middleName, lastName);
            val savedAuthor = authorService.save(author);
            ioMessageService.writeLocal("messages.author.create.complete", savedAuthor.getId());
        } catch (Exception e) {
            ioMessageService.write(String.format("%s: %s\n", e.getClass().getSimpleName(), e.getMessage()));
        }
    }

    @Override
    public void outputAuthorById() {
        try {
            ioMessageService.writeLocal("messages.author.readById.title");
            val authorOpt = selectAuthor();
            if (authorOpt.isEmpty())
                return;
            val author = authorOpt.get();
            ioMessageService.writeLocal("messages.author.read.tableHeaders");
            writeAuthor(author);
        } catch (Exception e) {
            ioMessageService.write(String.format("%s: %s\n", e.getClass().getSimpleName(), e.getMessage()));
        }
    }

    @Override
    public void outputAllAuthors() {
        try {
            ioMessageService.writeLocal("messages.author.read.title");
            ioMessageService.writeLocal("messages.author.read.tableHeaders");
            val authors = authorService.findAll();
            authors.forEach(this::writeAuthor);
        } catch (Exception e) {
            ioMessageService.write(String.format("%s: %s\n", e.getClass().getSimpleName(), e.getMessage()));
        }
    }

    @Override
    public void updateAuthor() {
        try {
            ioMessageService.writeLocal("messages.author.update.title");
            val authorOpt = selectAuthor();
            if (authorOpt.isEmpty())
                return;
            val oldAuthor = authorOpt.get();
            ioMessageService.writeLocal("messages.author.update.firstName", oldAuthor.getFirstName());
            var firstName = ioMessageService.read();
            if (firstName.equalsIgnoreCase("q")) {
                ioMessageService.writeLocal("messages.cancelOperation");
                return;
            }
            firstName = firstName.trim().isEmpty() ? oldAuthor.getFirstName() : firstName;
            ioMessageService.writeLocal("messages.author.update.middleName", oldAuthor.getMiddleName());
            var middleName = ioMessageService.read();
            if (middleName.equalsIgnoreCase("q")) {
                ioMessageService.writeLocal("messages.cancelOperation");
                return;
            }
            middleName = middleName.trim().isEmpty() ? oldAuthor.getMiddleName() : middleName;
            ioMessageService.writeLocal("messages.author.update.lastName", oldAuthor.getLastName());
            var lastName = ioMessageService.read();
            if (lastName.equalsIgnoreCase("q")) {
                ioMessageService.writeLocal("messages.cancelOperation");
                return;
            }
            lastName = lastName.trim().isEmpty() ? oldAuthor.getLastName() : lastName;
            val newAuthor = new Author(oldAuthor.getId(), firstName, middleName, lastName);
            authorService.save(newAuthor);
            ioMessageService.writeLocal("messages.author.update.complete");
        } catch (DataIntegrityViolationException e) {
            ioMessageService.writeLocal("messages.author.update.error.integrityViolation");
        } catch (Exception e) {
            ioMessageService.write(String.format("%s: %s\n", e.getClass().getSimpleName(), e.getMessage()));
        }
    }

    @Override
    public void deleteAuthor() {
        try {
            ioMessageService.writeLocal("messages.author.delete.title");
            val authorOpt = selectAuthor();
            if (authorOpt.isEmpty())
                return;
            val author = authorOpt.get();
            authorService.delete(author.getId());
            ioMessageService.writeLocal("messages.author.delete.complete");
        } catch (DataIntegrityViolationException e) {
            ioMessageService.writeLocal("messages.author.delete.error.integrityViolation");
        } catch (Exception e) {
            ioMessageService.write(String.format("%s: %s\n", e.getClass().getSimpleName(), e.getMessage()));
        }
    }

    @Override
    public Optional<Author> selectAuthor() {
        while (true) {
            try {
                ioMessageService.writeLocal("messages.author.id");
                val strAuthorId = ioMessageService.read();
                if (strAuthorId.trim().equalsIgnoreCase("q")) {
                    ioMessageService.writeLocal("messages.cancelOperation");
                    return Optional.empty();
                }
                if (strAuthorId.trim().equalsIgnoreCase("l")) {
                    outputAllAuthors();
                    continue;
                }
                Optional<Author> authorOpt = authorService.findById(strAuthorId);
                if (authorOpt.isEmpty()) {
                    ioMessageService.writeLocal("messages.author.read.error.notFound");
                    continue;
                }
                return authorOpt;
            } catch (Exception e) {
                ioMessageService.write(String.format("%s: %s\n", e.getClass().getSimpleName(), e.getMessage()));
            }
        }
    }

    private void writeAuthor(Author author) {
        ioMessageService.writeLocal("messages.author.info",
                String.format("%-30s", author.getId()),
                String.format("%s", author.getFullName())
        );
    }

}
