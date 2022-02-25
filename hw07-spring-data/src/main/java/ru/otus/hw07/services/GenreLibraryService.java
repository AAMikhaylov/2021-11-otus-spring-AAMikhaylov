package ru.otus.hw07.services;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import ru.otus.hw07.io.IOMessageService;
import ru.otus.hw07.models.Genre;

import java.util.*;

@Component
@RequiredArgsConstructor
public class GenreLibraryService {
    private final GenreService genreService;
    private final IOMessageService ioMessageService;


    public void createGenre() {
        try {
            ioMessageService.writeLocal("messages.genre.create.title");
            ioMessageService.writeLocal("messages.genre.create.name");
            val name = ioMessageService.read();
            if (name.equalsIgnoreCase("q")) {
                ioMessageService.writeLocal("messages.cancelOperation");
                return;
            }
            val genre = new Genre(name);
            val savedGenre = genreService.save(genre);
            ioMessageService.writeLocal("messages.genre.create.complete", Long.toString(savedGenre.getId()));
        } catch (DataIntegrityViolationException e) {
            ioMessageService.writeLocal("messages.genre.delete.error.integrityViolation");
        } catch (Exception e) {
            ioMessageService.write(e.getMessage());
        }
    }

    public void outputAllGenres() {
        try {
            ioMessageService.writeLocal("messages.genre.read.title");
            ioMessageService.writeLocal("messages.genre.read.tableHeaders");
            val genres = genreService.findAll();
            genres.forEach(this::writeGenre);
        } catch (Exception e) {
            ioMessageService.write(e.getMessage());
        }
    }

    public void outputGenreById() {
        try {
            ioMessageService.writeLocal("messages.genre.readById.title");
            val genreOpt = selectGenre();
            if (genreOpt.isEmpty())
                return;
            val genre = genreOpt.get();
            ioMessageService.writeLocal("messages.genre.read.tableHeaders");
            writeGenre(genre);
        } catch (Exception e) {
            ioMessageService.write(e.getMessage());
        }
    }

    public void updateGenre() {
        try {
            ioMessageService.writeLocal("messages.genre.update.title");
            val genreOpt = selectGenre();
            if (genreOpt.isEmpty())
                return;
            val oldGenre = genreOpt.get();
            ioMessageService.writeLocal("messages.genre.update.name", oldGenre.getName());
            var name = ioMessageService.read();
            if (name.equalsIgnoreCase("q")) {
                ioMessageService.writeLocal("messages.cancelOperation");
                return;
            }
            name = name.trim().isEmpty() ? oldGenre.getName() : name;
            val newGenre = new Genre(oldGenre.getId(), name);
            genreService.save(newGenre);
            ioMessageService.writeLocal("messages.genre.update.complete");
        } catch (Exception e) {
            ioMessageService.write(e.getMessage());
        }
    }

    public void deleteGenre() {
        try {
            ioMessageService.writeLocal("messages.genre.delete.title");
            val genreOpt = selectGenre();
            if (genreOpt.isEmpty())
                return;
            val genre = genreOpt.get();
            genreService.delete(genre.getId());
            ioMessageService.writeLocal("messages.genre.delete.complete");
        } catch (DataIntegrityViolationException e) {
            ioMessageService.writeLocal("messages.genre.delete.error.integrityViolation");
        } catch (Exception e) {
            ioMessageService.write(e.getMessage());
        }
    }

    public List<Genre> selectGenres() {
        ioMessageService.writeLocal("messages.book.addGenres");
        Map<Long, Genre> genresMap = new HashMap<>();
        var strGenreId = "";
        while (!strGenreId.equalsIgnoreCase("f")) {
            try {
                ioMessageService.writeLocal("messages.book.genreId");
                strGenreId = ioMessageService.read();
                if (strGenreId.equalsIgnoreCase("f")) {
                    break;
                }
                if (strGenreId.equalsIgnoreCase("l")) {
                    outputAllGenres();
                    continue;
                }
                try {
                    long genreId = Long.parseLong(strGenreId);
                    val genreOpt = genreService.findById(genreId);
                    if (genreOpt.isEmpty()) {
                        ioMessageService.writeLocal("messages.genre.read.error.notFound");
                        continue;
                    }
                    val genre = genreOpt.get();
                    genresMap.put(genre.getId(), genre);
                } catch (NumberFormatException e) {
                    ioMessageService.writeLocal("messages.genre.error.idFormat");
                }
            } catch (Exception e) {
                ioMessageService.write(e.getMessage());
            }
        }
        ioMessageService.writeLocal("messages.book.genresAdded", Integer.toString(genresMap.size()));
        return new ArrayList<>(genresMap.values());
    }

    public Optional<Genre> selectGenre() {
        while (true) {
            try {
                ioMessageService.writeLocal("messages.genre.id");
                val strGenreId = ioMessageService.read();
                if (strGenreId.trim().equalsIgnoreCase("q")) {
                    ioMessageService.writeLocal("messages.cancelOperation");
                    return Optional.empty();
                }
                if (strGenreId.trim().equalsIgnoreCase("l")) {
                    outputAllGenres();
                    continue;
                }
                try {
                    long genreId = Long.parseLong(strGenreId);
                    Optional<Genre> genreOpt = genreService.findById(genreId);
                    if (genreOpt.isEmpty()) {
                        ioMessageService.writeLocal("messages.genre.read.error.notFound");
                        continue;
                    }
                    return genreOpt;
                } catch (NumberFormatException e) {
                    ioMessageService.writeLocal("messages.genre.error.idFormat");
                }
            } catch (Exception e) {
                ioMessageService.write(e.getMessage());
            }
        }
    }

    private void writeGenre(Genre genre) {
        ioMessageService.writeLocal("messages.genre.info",
                String.format("%-15d", genre.getId()),
                String.format("%s", genre.getName())
        );
    }
}
