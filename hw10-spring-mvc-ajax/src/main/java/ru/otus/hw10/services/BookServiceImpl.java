package ru.otus.hw10.services;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw10.domain.Book;
import ru.otus.hw10.dto.BookDto;
import ru.otus.hw10.repositories.BookRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;

    @Override
    @Transactional
    public BookDto save(BookDto book) {
        val bookEntity = book.toEntity();
        return BookDto.fromEntityLazy(bookRepository.save(bookEntity));
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<BookDto> findByIdLazy(long id) {
        Optional<Book> bookOpt = bookRepository.findById(id);
        bookOpt.ifPresent(book -> Hibernate.initialize(book.getGenres()));
        return bookOpt.map(BookDto::fromEntityLazy);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BookDto> findByIdEager(long id) {
        Optional<Book> bookOpt = bookRepository.findById(id);
        bookOpt.ifPresent(book -> Hibernate.initialize(book.getGenres()));
        bookOpt.ifPresent(book -> Hibernate.initialize(book.getComments()));
        return bookOpt.map(BookDto::fromEntityEager);
    }

    @Transactional(readOnly = true)
    public List<BookDto> findAll() {
        List<Book> bookEntities = bookRepository.findAll();
        return bookEntities.stream()
                .map(BookDto::fromEntityLazy)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(long id) {
        bookRepository.deleteById(id);
    }
}
