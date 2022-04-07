package ru.otus.hw10.controllers.deserializer;

import com.fasterxml.jackson.databind.util.StdConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw10.dto.GenreDto;
import ru.otus.hw10.exceptions.NotFoundException;
import ru.otus.hw10.services.GenreService;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GenresDeserializer extends StdConverter<List<String>, List<GenreDto>> {
    private final GenreService genreService;

    @Override
    public List<GenreDto> convert(List<String> genreIds) {
        List<GenreDto> result = new ArrayList<>();
        if (genreIds.size() == 0) {
            return result;
        }
        genreIds.forEach(id -> result.add(genreService.findById(Long.parseLong(id))
                .orElseThrow(() -> new NotFoundException("Жанр c ID=" + id + " не найден!")))
        );
        return result;
    }
}
