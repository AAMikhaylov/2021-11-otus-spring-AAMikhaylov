package ru.otus.hw05.services;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Component;
import ru.otus.hw05.dao.AuthorDao;
import ru.otus.hw05.domain.Author;
import ru.otus.hw05.exceptions.DaoException;
import ru.otus.hw05.exceptions.ExceptionHandler;
import ru.otus.hw05.io.IOMessageService;

@Component
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {
    private final AuthorDao authorDao;
    private final IOMessageService ioMessageService;
    private final ExceptionHandler exceptionHandler;

    @Override
    public void create() {
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
            long id = authorDao.create(author);
            ioMessageService.writeLocal("messages.author.create.complete", Long.toString(id));
        } catch (Exception e) {
            exceptionHandler.handle(e);
        }
    }

    @Override
    public void readById() {
        try {
            ioMessageService.writeLocal("messages.author.readById.title");
            val id = getInputId();
            if (id == 0L) {
                return;
            }
            val author = authorDao.getById(id).orElseThrow(() -> new DaoException("messages.author.read.error.notFound"));
            ioMessageService.writeLocal("messages.author.read.tableHeaders");
            printAuthorInfo(author);
        } catch (Exception e) {
            exceptionHandler.handle(e);
        }
    }


    @Override
    public void readAll() {
        try {
            ioMessageService.writeLocal("messages.author.read.title");
            ioMessageService.writeLocal("messages.author.read.tableHeaders");
            val authors = authorDao.getAll();
            authors.forEach(this::printAuthorInfo);
        } catch (Exception e) {
            exceptionHandler.handle(e);
        }
    }

    @Override
    public void update() {
        try {
            ioMessageService.writeLocal("messages.author.update.title");
            val id = getInputId();
            if (id == 0L)
                return;
            val author = authorDao.getById(id).orElseThrow(() -> new DaoException("messages.author.read.error.notFound"));
            ioMessageService.writeLocal("messages.author.update.firstName", author.getFirstName());
            var firstName = ioMessageService.read();
            if (firstName.equalsIgnoreCase("q")) {
                ioMessageService.writeLocal("messages.cancelOperation");
                return;
            }
            firstName = firstName.trim().isEmpty() ? author.getFirstName() : firstName;
            ioMessageService.writeLocal("messages.author.update.middleName", author.getMiddleName());
            var middleName = ioMessageService.read();
            if (middleName.equalsIgnoreCase("q")) {
                ioMessageService.writeLocal("messages.cancelOperation");
                return;
            }
            middleName = middleName.trim().isEmpty() ? author.getMiddleName() : middleName;
            ioMessageService.writeLocal("messages.author.update.lastName", author.getLastName());
            var lastName = ioMessageService.read();
            if (lastName.equalsIgnoreCase("q")) {
                ioMessageService.writeLocal("messages.cancelOperation");
                return;
            }
            lastName = lastName.trim().isEmpty() ? author.getLastName() : lastName;
            val updatedAuthor = new Author(author.getId(), firstName, middleName, lastName);
            var updCnt = 0;
            if (!updatedAuthor.equals(author))
                updCnt = authorDao.update(updatedAuthor);
            ioMessageService.writeLocal("messages.author.update.complete", Integer.toString(updCnt));
        } catch (Exception e) {
            exceptionHandler.handle(e);
        }
    }

    @Override
    public void delete() {
        try {
            ioMessageService.writeLocal("messages.author.delete.title");
            val id = getInputId();
            if (id == 0L) {
                return;
            }
            val delCnt = authorDao.deleteById(id);
            ioMessageService.writeLocal("messages.author.delete.complete", Integer.toString(delCnt));
        } catch (Exception e) {
            exceptionHandler.handle(e);
        }
    }

    private long getInputId() {
        while (true) {
            try {
                ioMessageService.writeLocal("messages.author.id");
                val strId = ioMessageService.read();
                if (strId.trim().equalsIgnoreCase("q")) {
                    ioMessageService.writeLocal("messages.cancelOperation");
                    return 0L;
                }
                if (strId.trim().equalsIgnoreCase("l")) {
                    readAll();
                    continue;
                }
                try {
                    return Long.parseLong(strId);
                } catch (NumberFormatException e) {
                    throw new NumberFormatException("messages.author.error.idFormat");
                }
            } catch (Exception e) {
                exceptionHandler.handle(e);
            }
        }
    }

    private void printAuthorInfo(Author author) {
        ioMessageService.writeLocal("messages.author.info",
                String.format("%-15d", author.getId()),
                String.format("%s", author.getFullName())
        );
    }
}


