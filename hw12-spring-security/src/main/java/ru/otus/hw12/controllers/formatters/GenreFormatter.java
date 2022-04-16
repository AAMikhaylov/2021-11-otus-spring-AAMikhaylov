package ru.otus.hw12.controllers.formatters;

import lombok.RequiredArgsConstructor;
import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;
import ru.otus.hw12.dto.GenreDto;
import ru.otus.hw12.exceptions.NotFoundException;
import ru.otus.hw12.services.GenreService;

import java.text.ParseException;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class GenreFormatter implements Formatter<GenreDto> {
    private final GenreService genreService;

    @Override
    public GenreDto parse(String genreId, Locale locale) throws ParseException {
        try {
            return genreService.findById(Long.parseLong(genreId))
                    .orElseThrow(() -> new NotFoundException("Жанр c ID=" + genreId + " не найден!"));
        } catch (NumberFormatException e) {
            throw new ParseException(e.getMessage(), 0);
        }
    }

    @Override
    public String print(GenreDto genreDto, Locale locale) {
        return genreDto.getName();
    }
}
