package ru.otus.hw13.controllers.formatters;

import lombok.RequiredArgsConstructor;
import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;
import ru.otus.hw13.dto.BookDto;
import ru.otus.hw13.exceptions.NotFoundException;
import ru.otus.hw13.services.BookService;

import java.text.ParseException;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class BookFormatter implements Formatter<BookDto> {
    private final BookService bookService;

    @Override
    public BookDto parse(String bookId, Locale locale) throws ParseException {
        try {
            return bookService.findByIdLazy(Long.parseLong(bookId))
                    .orElseThrow(() -> new NotFoundException("Книга c ID=" + bookId + " не найдена!"));
        } catch (NumberFormatException e) {
            throw new ParseException(e.getMessage(), 0);
        }
    }

    @Override
    public String print(BookDto bookDto, Locale locale) {
        return bookDto.getTitle();
    }
}
