package ru.otus.hw12.controllers;

import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import ru.otus.hw12.dto.AuthorDto;
import ru.otus.hw12.exceptions.IntegrityViolationException;
import ru.otus.hw12.exceptions.NotFoundException;
import ru.otus.hw12.security.services.UserService;
import ru.otus.hw12.services.AuthorService;
import ru.otus.hw12.services.AuthorServiceImpl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SuppressWarnings({"unchecked", "ConstantConditions"})
@AutoConfigureDataJpa
@AutoConfigureTestDatabase
@WebMvcTest(value = {AuthorController.class, UserService.class, AuthorServiceImpl.class})

@DisplayName("Класс AuthorController должен:")
class AuthorControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    AuthorService authorService;

    private static final Long AUTHOR_ID_WITH_BOOKS = 1L;
    private static final Long AUTHOR_ID_WITHOUT_BOOKS = 3L;

    @Test
    @WithUserDetails("admin")
    @DisplayName("Обновлять автора")
    void updateAuthorSuccess() throws Exception {
        val expectedAuthor = new AuthorDto(AUTHOR_ID_WITH_BOOKS, "test1", "test2", "test3");
        mockMvc.perform(post("/saveAuthor")
                        .param("id", expectedAuthor.getId().toString())
                        .param("firstName", expectedAuthor.getFirstName())
                        .param("middleName", expectedAuthor.getMiddleName())
                        .param("lastName", expectedAuthor.getLastName()))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "/author?id=" + AUTHOR_ID_WITH_BOOKS))
                .andReturn();
        val actualAuthor = authorService.findById(AUTHOR_ID_WITH_BOOKS).orElseThrow(() -> new NotFoundException("Автор с ID=" + AUTHOR_ID_WITH_BOOKS));
        assertThat(actualAuthor)
                .usingRecursiveComparison()
                .isEqualTo(expectedAuthor);
    }

    @Test
    @WithUserDetails("admin")
    @DisplayName("Формировать ошибки заполнения полей при редактировании/создании автора")
    void addAuthorFieldsError() throws Exception {
        val author = new AuthorDto(0L, "", "1", "2");
        val expectedErrorCodes = List.of("Size", "NotBlank", "Size", "Size");
        val result = mockMvc.perform(post("/saveAuthor")
                        .param("id", author.getId().toString())
                        .param("firstName", author.getFirstName())
                        .param("middleName", author.getMiddleName())
                        .param("lastName", author.getLastName()))
                .andExpect(status().isOk())
                .andReturn();
        val bindingResult = (BindingResult) result.getModelAndView().getModel().get("org.springframework.validation.BindingResult.author");
        val actualErrorCodes = bindingResult.getFieldErrors().stream()
                .map(FieldError::getCode)
                .collect(Collectors.toList());
        assertThat(actualErrorCodes)
                .hasSize(4)
                .containsExactlyInAnyOrderElementsOf(expectedErrorCodes);
    }

    @Test
    @WithUserDetails("admin")
    @DisplayName("Добавлять нового автора")
    void addAuthorSuccess() throws Exception {
        val expectedAuthor = new AuthorDto(0L, "test1", "test2", "test3");
        val result = mockMvc.perform(post("/saveAuthor")
                        .param("id", expectedAuthor.getId().toString())
                        .param("firstName", expectedAuthor.getFirstName())
                        .param("middleName", expectedAuthor.getMiddleName())
                        .param("lastName", expectedAuthor.getLastName()))
                .andExpect(status().isFound())
                .andReturn();
        val resultPageNewAuthor = mockMvc.perform(get(result.getResponse().getRedirectedUrl())).andReturn();
        val actualAuthor = (AuthorDto) resultPageNewAuthor.getModelAndView().getModel().get("author");
        assertThat(actualAuthor)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expectedAuthor);
    }


    @Test
    @WithUserDetails("admin")
    @DisplayName("Не удалять автора с книгами - бросать IntegrityViolationException.")
    void deleteAuthorFail() throws Exception {
        val result = mockMvc.perform(get("/deleteAuthor").param("id", AUTHOR_ID_WITH_BOOKS.toString()))
                .andExpect(status().isConflict())
                .andReturn();
        val deletedAuthor = authorService.findById(AUTHOR_ID_WITH_BOOKS);
        assertThat(deletedAuthor.isPresent()).as("Author didn't delete").isTrue();
        assertThat(result.getResolvedException()).isInstanceOf(IntegrityViolationException.class).hasMessage("Нельзя удалить автора c ID=" + AUTHOR_ID_WITH_BOOKS + ". У автора есть книги.");
    }

    @Test
    @WithUserDetails("admin")
    @DisplayName("Удалять автора с указанным ID")
    void deleteAuthorSuccess() throws Exception {
        mockMvc.perform(get("/deleteAuthor").param("id", AUTHOR_ID_WITHOUT_BOOKS.toString()))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "/authors"))
                .andReturn();
        val deletedAuthor = authorService.findById(AUTHOR_ID_WITHOUT_BOOKS);
        assertThat(deletedAuthor.isEmpty()).as("Author deleted").isTrue();
    }

    @Test
    @WithUserDetails()
    @DisplayName("Добавлять в модель автора, при запросе страницы с автором")
    void author() throws Exception {
        val expectedAuthor = authorService.findById(AUTHOR_ID_WITH_BOOKS).orElseThrow(() -> new NotFoundException("Автор с ID=" + AUTHOR_ID_WITH_BOOKS));
        val result = mockMvc.perform(get("/author").param("id", AUTHOR_ID_WITH_BOOKS.toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("author"))
                .andReturn();
        val actualAuthor = (AuthorDto) Objects.requireNonNull(result.getModelAndView()).getModel().get("author");
        assertThat(actualAuthor)
                .usingRecursiveComparison()
                .isEqualTo(expectedAuthor);
    }


    @Test
    @WithUserDetails()
    @DisplayName("Добавлять в модель всех авторов, при запросе списка авторов")
    void authors() throws Exception {
        List<AuthorDto> expectedAuthors = authorService.findAll();
        val result = mockMvc.perform(get("/authors"))
                .andExpect(status().isOk())
                .andExpect(view().name("authors"))
                .andReturn();
        List<AuthorDto> actualAuthors = (List<AuthorDto>) Objects.requireNonNull(result.getModelAndView()).getModel().get("authors");
        assertThat(actualAuthors)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedAuthors);
    }

    @Test
    @WithUserDetails()
    @DisplayName("Возвращать Forbidden для пользователя user при удалении (недостаточно прав)")
    void deleteAuthorAuthFail() throws Exception {
        mockMvc.perform(get("/deleteAuthor").param("id", AUTHOR_ID_WITHOUT_BOOKS.toString()))
                .andExpect(status().isForbidden())
                .andReturn();
        val deletedAuthor = authorService.findById(AUTHOR_ID_WITHOUT_BOOKS);
        assertThat(deletedAuthor.isPresent()).as("Author didn't delete").isTrue();
    }

    @Test
    @DisplayName("Перенаправлять на форму SignIn ,если пользователь не аутентифицирован")
    void authorsAuthFail() throws Exception {
        mockMvc.perform(get("/authors"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "http://localhost/login"))
                .andReturn();
        mockMvc.perform(get("/author?id=" + AUTHOR_ID_WITH_BOOKS))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "http://localhost/login"))
                .andReturn();
        mockMvc.perform(get("/editAuthor"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "http://localhost/login"))
                .andReturn();
        mockMvc.perform(post("/saveAuthor"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "http://localhost/login"))
                .andReturn();
        mockMvc.perform(post("/deleteAuthor"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "http://localhost/login"))
                .andReturn();
    }
}