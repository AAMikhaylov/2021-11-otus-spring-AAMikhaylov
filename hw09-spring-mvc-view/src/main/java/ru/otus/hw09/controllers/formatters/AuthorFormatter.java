package ru.otus.hw09.controllers.formatters;

import lombok.RequiredArgsConstructor;
import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;
import ru.otus.hw09.dto.AuthorDto;
import ru.otus.hw09.exceptions.NotFoundException;
import ru.otus.hw09.services.AuthorService;
import java.text.ParseException;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class AuthorFormatter implements Formatter<AuthorDto> {
    private final AuthorService authorService;

    @Override
    public AuthorDto parse(String authorId, Locale locale) throws ParseException {
        try {
            return authorService.findById(Long.parseLong(authorId))
                    .orElseThrow(() -> new NotFoundException("Автор c ID=" + authorId + " не найден!"));
        } catch (NumberFormatException e) {
            throw new ParseException(e.getMessage(), 0);
        }
    }

    @Override
    public String print(AuthorDto authorDto, Locale locale) {
        return authorDto.getShortName();
    }
}
