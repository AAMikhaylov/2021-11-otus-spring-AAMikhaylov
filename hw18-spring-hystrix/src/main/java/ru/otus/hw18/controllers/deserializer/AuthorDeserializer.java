package ru.otus.hw18.controllers.deserializer;

import com.fasterxml.jackson.databind.util.StdConverter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Component;
import ru.otus.hw18.dto.AuthorDto;
import ru.otus.hw18.exceptions.NotFoundException;
import ru.otus.hw18.services.AuthorService;

@Component
@RequiredArgsConstructor
public class AuthorDeserializer extends StdConverter<String, AuthorDto> {
    private final AuthorService authorService;

    @Override
    public AuthorDto convert(String authorId) {
        if (authorId == null || authorId.isEmpty())
            return null;
        val id = Long.parseLong(authorId);
        if (id == 0L)
            return null;
        return authorService.findById(id).orElseThrow(() -> new NotFoundException("Автор c ID=" + authorId + " не найден!"));
    }
}
