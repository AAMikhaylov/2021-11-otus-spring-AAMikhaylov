package ru.otus.hw05.services;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Component;
import ru.otus.hw05.dao.GenreDao;
import ru.otus.hw05.domain.Genre;
import ru.otus.hw05.exceptions.DaoException;
import ru.otus.hw05.exceptions.ExceptionHandler;
import ru.otus.hw05.io.IOMessageService;

@Component
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {
    private final GenreDao genreDao;
    private final IOMessageService ioMessageService;
    private final ExceptionHandler exceptionHandler;

    @Override
    public void create() {
        try {
            ioMessageService.writeLocal("messages.genre.create.title");
            ioMessageService.writeLocal("messages.genre.create.name");
            val name = ioMessageService.read();
            if  (name.equalsIgnoreCase("q")) {
                ioMessageService.writeLocal("messages.cancelOperation");
                return;
            }
            val genre = new Genre(name);
            long id = genreDao.create(genre);
            ioMessageService.writeLocal("messages.genre.create.complete", Long.toString(id));
        } catch (Exception e) {
            exceptionHandler.handle(e);
        }
    }

    @Override
    public void readAll() {
        try {
            ioMessageService.writeLocal("messages.genre.read.title");
            ioMessageService.writeLocal("messages.genre.read.tableHeaders");
            val genres = genreDao.getAll();
            genres.forEach(this::printGenreInfo);
        } catch (Exception e) {
            exceptionHandler.handle(e);
        }
    }

    @Override
    public void readById() {
        try {
            ioMessageService.writeLocal("messages.genre.readById.title");
            val id = getInputId();
            if (id == 0L) {
                return;
            }
            val genre = genreDao.getById(id).orElseThrow(() -> new DaoException("messages.genre.read.error.notFound"));
            ioMessageService.writeLocal("messages.genre.read.tableHeaders");
            printGenreInfo(genre);
        } catch (Exception e) {
            exceptionHandler.handle(e);
        }
    }

    @Override
    public void update() {
        try {
            ioMessageService.writeLocal("messages.genre.update.title");
            val id = getInputId();
            if (id == 0L)
                return;
            val genre = genreDao.getById(id).orElseThrow(() -> new DaoException("messages.genre.read.error.notFound"));
            ioMessageService.writeLocal("messages.genre.update.name", genre.getName());
            var name = ioMessageService.read();
            if  (name.equalsIgnoreCase("q")) {
                ioMessageService.writeLocal("messages.cancelOperation");
                return;
            }
            name = name.trim().isEmpty() ? genre.getName() : name;
            val updatedGenre = new Genre(genre.getId(), name);
            var updCnt = 0;
            if (!updatedGenre.equals(genre))
                updCnt = genreDao.update(updatedGenre);
            ioMessageService.writeLocal("messages.genre.update.complete", Integer.toString(updCnt));
        } catch (Exception e) {
            exceptionHandler.handle(e);
        }
    }

    @Override
    public void delete() {
        try {
            ioMessageService.writeLocal("messages.genre.delete.title");
            val id = getInputId();
            if (id == 0L) {
                return;
            }
            val delCnt = genreDao.deleteById(id);
            ioMessageService.writeLocal("messages.genre.delete.complete", Integer.toString(delCnt));
        } catch (Exception e) {
            exceptionHandler.handle(e);
        }
    }

    private long getInputId() {
        while (true) {
            try {
                ioMessageService.writeLocal("messages.genre.id");
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
                    throw new NumberFormatException("messages.genre.error.idFormat");
                }
            } catch (Exception e) {
                exceptionHandler.handle(e);
            }
        }
    }

    private void printGenreInfo(Genre genre) {
        ioMessageService.writeLocal("messages.genre.info",
                String.format("%-15d", genre.getId()),
                String.format("%s", genre.getName())
        );

    }
}