package ru.otus.hw13.services;
import ru.otus.hw13.dto.BookDto;
import java.util.List;
import java.util.Optional;

public interface BookService {
    void delete(long id);

    BookDto save(BookDto book);

    Optional<BookDto> findByIdLazy(long id);

    Optional<BookDto> findByIdEager(long id);

    List<BookDto> findAll();


}
