package ru.otus.hw13.controllers;

import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;

import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw13.domain.Book;
import ru.otus.hw13.dto.BookDto;
import ru.otus.hw13.security.AclConfig;
import ru.otus.hw13.security.AclMethodSecurityConfiguration;
import ru.otus.hw13.security.services.UserService;
import ru.otus.hw13.services.AuthorServiceImpl;
import ru.otus.hw13.services.BookService;
import ru.otus.hw13.services.BookServiceImpl;
import ru.otus.hw13.services.GenreServiceImpl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SuppressWarnings({"unchecked"})
@AutoConfigureDataJpa
@AutoConfigureTestDatabase
@AutoConfigureTestEntityManager
@Transactional
@Import({AclConfig.class, AclMethodSecurityConfiguration.class})
@WebMvcTest(value = {BookController.class, UserService.class, BookServiceImpl.class, AuthorServiceImpl.class, GenreServiceImpl.class})
@DisplayName("Класс BookController должен:")
class BookControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    BookService bookService;
    @Autowired
    private TestEntityManager em;

    @Test
    @WithUserDetails()
    @DisplayName("Не добавлять в модель книги с ограничением возраста для USER_ROLE")
    void booksForUser() throws Exception {
        val query = em.getEntityManager().createQuery("select b from Book b where b.ageRestrict=false", Book.class);
        val expectedBooks = query.getResultList()
                .stream()
                .map(BookDto::fromEntityLazy)
                .collect(Collectors.toList());
        val result = mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andReturn();
        List<BookDto> actualBooks = (List<BookDto>) Objects.requireNonNull(result.getModelAndView()).getModel().get("books");
        assertThat(actualBooks)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedBooks);
    }

    @Test
    @WithUserDetails("admin")
    @DisplayName("Добавлять в модель все книги для ADMIN_ROLE")
    void booksForAdmin() throws Exception {
        val query = em.getEntityManager().createQuery("select b from Book b", Book.class);
        val expectedBooks = query.getResultList()
                .stream()
                .map(BookDto::fromEntityLazy)
                .collect(Collectors.toList());
        val result = mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andReturn();
        List<BookDto> actualBooks = (List<BookDto>) Objects.requireNonNull(result.getModelAndView()).getModel().get("books");
        assertThat(actualBooks)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedBooks);
    }
}