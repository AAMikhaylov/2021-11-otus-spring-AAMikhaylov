package ru.otus.hw13.services;

import lombok.val;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw13.domain.Book;
import ru.otus.hw13.dto.BookDto;
import ru.otus.hw13.repositories.BookRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final MutableAclService mutableAclService;

    public BookServiceImpl(BookRepository bookRepository, @Autowired(required = false) MutableAclService mutableAclService) {
        this.bookRepository = bookRepository;
        this.mutableAclService = mutableAclService;
    }

    @Override
    @Transactional
    public BookDto save(BookDto book) {
        val bookEntity = book.toEntity();
        val savedBookEntity = bookRepository.save(bookEntity);
        addGrants(savedBookEntity);
        return BookDto.fromEntityLazy(savedBookEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BookDto> findByIdLazy(long id) {
        Optional<Book> bookOpt = Optional.ofNullable(bookRepository.findById(id));
        bookOpt.ifPresent(book -> Hibernate.initialize(book.getGenres()));
        return bookOpt.map(BookDto::fromEntityLazy);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BookDto> findByIdEager(long id) {
        Optional<Book> bookOpt = Optional.ofNullable(bookRepository.findById(id));
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
    public void delete(long id) {
        bookRepository.deleteById(id);
        deleteGrants(id);
    }

    private void deleteGrants(long bookId) {
        if (mutableAclService == null)
            return;
        val oid = new ObjectIdentityImpl(Book.class, bookId);
        mutableAclService.deleteAcl(oid, true);
    }

    private void addGrants(Book book) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null)
            return;
        val owner = new PrincipalSid(authentication);
        val oid = new ObjectIdentityImpl(book.getClass(), book.getId());
        mutableAclService.deleteAcl(oid, true);
        val acl = mutableAclService.createAcl(oid);
        acl.setOwner(owner);
        acl.insertAce(acl.getEntries().size(), BasePermission.READ, new GrantedAuthoritySid("ROLE_ADMIN"), true);
        if (!book.isAgeRestrict())
            acl.insertAce(acl.getEntries().size(), BasePermission.READ, new GrantedAuthoritySid("ROLE_USER"), true);
        mutableAclService.updateAcl(acl);
    }

}
