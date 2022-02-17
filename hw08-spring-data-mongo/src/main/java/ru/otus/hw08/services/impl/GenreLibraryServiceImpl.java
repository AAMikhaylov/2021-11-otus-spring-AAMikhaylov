package ru.otus.hw08.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import ru.otus.hw08.io.IOMessageService;
import ru.otus.hw08.models.Genre;
import ru.otus.hw08.services.GenreLibraryService;
import ru.otus.hw08.services.GenreService;

import java.util.*;

@Component
@RequiredArgsConstructor
public class GenreLibraryServiceImpl implements GenreLibraryService {
    private final GenreService genreService;
    private final IOMessageService ioMessageService;

    @Override
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
            ioMessageService.writeLocal("messages.genre.create.complete", savedGenre.getId());
        } catch (DataIntegrityViolationException e) {
            ioMessageService.writeLocal("messages.genre.delete.error.integrityViolation");
        } catch (Exception e) {
            ioMessageService.write(String.format("%s: %s\n", e.getClass().getSimpleName(), e.getMessage()));
        }
    }

    @Override
    public void outputAllGenres() {
        try {
            ioMessageService.writeLocal("messages.genre.read.title");
            ioMessageService.writeLocal("messages.genre.read.tableHeaders");
            val genres = genreService.findAll();
            genres.forEach(this::writeGenre);
        } catch (Exception e) {
            ioMessageService.write(String.format("%s: %s\n", e.getClass().getSimpleName(), e.getMessage()));
        }
    }

    @Override
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
            ioMessageService.write(String.format("%s: %s\n", e.getClass().getSimpleName(), e.getMessage()));
        }
    }

    @Override
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
            genreService.cascadeUpdate(newGenre);
            ioMessageService.writeLocal("messages.genre.update.complete");
        } catch (Exception e) {
            ioMessageService.write(String.format("%s: %s\n", e.getClass().getSimpleName(), e.getMessage()));
        }
    }

    @Override
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
            ioMessageService.write(String.format("%s: %s\n", e.getClass().getSimpleName(), e.getMessage()));
        }
    }

    @Override
    public List<Genre> selectGenres() {
        ioMessageService.writeLocal("messages.book.addGenres");
        Map<String, Genre> genresMap = new HashMap<>();
        var genreId = "";
        while (!genreId.equalsIgnoreCase("f")) {
            try {
                ioMessageService.writeLocal("messages.book.genreId");
                genreId = ioMessageService.read();
                if (genreId.equalsIgnoreCase("f")) {
                    break;
                }
                if (genreId.equalsIgnoreCase("l")) {
                    outputAllGenres();
                    continue;
                }
                val genreOpt = genreService.findById(genreId);
                if (genreOpt.isEmpty()) {
                    ioMessageService.writeLocal("messages.genre.read.error.notFound");
                    continue;
                }
                val genre = genreOpt.get();
                genresMap.put(genre.getId(), genre);
            } catch (Exception e) {
                ioMessageService.write(String.format("%s: %s\n", e.getClass().getSimpleName(), e.getMessage()));
            }
        }
        ioMessageService.writeLocal("messages.book.genresAdded", Integer.toString(genresMap.size()));
        return new ArrayList<>(genresMap.values());
    }

    @Override
    public Optional<Genre> selectGenre() {
        while (true) {
            try {
                ioMessageService.writeLocal("messages.genre.id");
                val genreId = ioMessageService.read();
                if (genreId.trim().equalsIgnoreCase("q")) {
                    ioMessageService.writeLocal("messages.cancelOperation");
                    return Optional.empty();
                }
                if (genreId.trim().equalsIgnoreCase("l")) {
                    outputAllGenres();
                    continue;
                }
                Optional<Genre> genreOpt = genreService.findById(genreId);
                if (genreOpt.isEmpty()) {
                    ioMessageService.writeLocal("messages.genre.read.error.notFound");
                    continue;
                }
                return genreOpt;
            } catch (Exception e) {
                ioMessageService.write(String.format("%s: %s\n", e.getClass().getSimpleName(), e.getMessage()));
            }
        }
    }

    private void writeGenre(Genre genre) {
        ioMessageService.writeLocal("messages.genre.info",
                String.format("%-30s", genre.getId()),
                String.format("%s", genre.getName())
        );
    }

}
