package ru.otus.hw06.services;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw06.models.Book;
import ru.otus.hw06.repositories.BookRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;

    @Override
    @Transactional
    public Book save(Book book) {
        return bookRepository.save(book);
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<Book> findByIdLazy(long id) {
        Optional<Book> bookOpt = bookRepository.findById(id);
        bookOpt.ifPresent(book -> Hibernate.initialize(book.getGenres()));
        return bookOpt;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Book> findByIdEager(long id) {
        Optional<Book> bookOpt = bookRepository.findById(id);
        bookOpt.ifPresent(book -> Hibernate.initialize(book.getGenres()));
        bookOpt.ifPresent(book -> Hibernate.initialize(book.getComments()));
        return bookOpt;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> findAll() {
        val books = bookRepository.findAll();
        if (books.size() > 0)
            Hibernate.initialize(books.get(books.size() - 1).getGenres());
        return books;
    }

    @Override
    @Transactional
    public void delete(long id) {
        bookRepository.deleteById(id);
    }
}
