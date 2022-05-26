package ru.otus.hw18.controllers.deserializer;

import com.fasterxml.jackson.databind.util.StdConverter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Component;
import ru.otus.hw18.dto.BookDto;
import ru.otus.hw18.exceptions.NotFoundException;
import ru.otus.hw18.services.BookService;

@Component
@RequiredArgsConstructor
public class BookDeserializer extends StdConverter<String, BookDto> {
    private final BookService bookService;

    @Override
    public BookDto convert(String bookId) {
        if (bookId == null || bookId.isEmpty())
            return null;
        val id = Long.parseLong(bookId);
        if (id == 0L)
            return null;
        return bookService.findByIdLazy(id).orElseThrow(() -> new NotFoundException("Книга c ID=" + bookId + " не найдена!"));
    }
}
