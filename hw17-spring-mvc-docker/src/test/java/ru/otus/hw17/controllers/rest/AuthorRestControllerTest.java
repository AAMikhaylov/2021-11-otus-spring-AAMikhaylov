package ru.otus.hw17.controllers.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.otus.hw17.dto.AuthorDto;
import ru.otus.hw17.exceptions.IntegrityViolationException;
import ru.otus.hw17.exceptions.NotFoundException;
import ru.otus.hw17.services.AuthorService;
import ru.otus.hw17.services.AuthorServiceImpl;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SuppressWarnings({"unchecked", "ConstantConditions"})
@AutoConfigureDataJpa
@AutoConfigureTestDatabase
@WebMvcTest({AuthorRestController.class, AuthorServiceImpl.class})
@DisplayName("Класс AuthorController должен:")
class AuthorRestControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    AuthorService authorService;

    private static final Long AUTHOR_ID_WITH_BOOKS = 1L;
    private static final Long AUTHOR_ID_WITHOUT_BOOKS = 3L;


    @Test
    @DisplayName("Обновляет автора")
    void updateAuthorSuccess() throws Exception {
        val expectedAuthor = new AuthorDto(AUTHOR_ID_WITH_BOOKS, "test1", "test2", "test3");
        val jsonBody = mapper.writeValueAsString(expectedAuthor);
        mockMvc.perform(put("/api/authors/" + AUTHOR_ID_WITH_BOOKS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(content().string(AUTHOR_ID_WITH_BOOKS.toString()))
                .andReturn();
        val actualAuthor = authorService.findById(AUTHOR_ID_WITH_BOOKS).orElseThrow(() -> new NotFoundException("Автор с ID=" + AUTHOR_ID_WITH_BOOKS));
        assertThat(actualAuthor)
                .usingRecursiveComparison()
                .isEqualTo(expectedAuthor);
    }


    @Test
    @DisplayName("Формирует ошибки заполнения полей при редактировании/создании автора")
    void addAuthorFieldsError() throws Exception {
        val jsonBody = mapper.writeValueAsString(new AuthorDto(0L, "", "1", "2"));
        val expectedErrorCodes = List.of("Size", "NotBlank", "Size", "Size");
        val result = mockMvc.perform(post("/api/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isBadRequest())
                .andReturn();
        MethodArgumentNotValidException bindException = (MethodArgumentNotValidException) result.getResolvedException();
        val bindingResult = bindException.getBindingResult();
        val actualErrorCodes = bindingResult.getFieldErrors().stream()
                .map(FieldError::getCode)
                .collect(Collectors.toList());
        assertThat(actualErrorCodes)
                .hasSize(4)
                .containsExactlyInAnyOrderElementsOf(expectedErrorCodes);
    }

    @Test
    @DisplayName("Добавлять нового автора")
    void addAuthorSuccess() throws Exception {
        val expectedAuthor = new AuthorDto(0L, "test1", "test2", "test3");
        val jsonBody = mapper.writeValueAsString(expectedAuthor);
        val resultId = mockMvc.perform(post("/api/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        val actualAuthor = authorService.findById(Long.parseLong(resultId))
                .orElseThrow(() -> new NotFoundException("Автор с ID=" + resultId));
        assertThat(actualAuthor)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expectedAuthor);
    }


    @Test
    @DisplayName("Не удалять автора с книгами - бросать IntegrityViolationException.")
    void deleteAuthorFail() throws Exception {
        val result = mockMvc.perform(delete("/api/authors/" + AUTHOR_ID_WITH_BOOKS))
                .andExpect(status().isConflict())
                .andReturn();
        val deletedAuthor = authorService.findById(AUTHOR_ID_WITH_BOOKS);
        assertThat(deletedAuthor.isPresent()).as("Author didn't delete").isTrue();
        assertThat(result.getResolvedException()).isInstanceOf(IntegrityViolationException.class).hasMessage("Нельзя удалить автора c ID=" + AUTHOR_ID_WITH_BOOKS + ". У автора есть книги.");
    }

    @Test
    @DisplayName("Удалять автора с указанным ID")
    void deleteAuthorSuccess() throws Exception {
        mockMvc.perform(delete("/api/authors/" + AUTHOR_ID_WITHOUT_BOOKS))
                .andExpect(status().isOk())
                .andReturn();
        val deletedAuthor = authorService.findById(AUTHOR_ID_WITHOUT_BOOKS);
        assertThat(deletedAuthor.isEmpty()).as("Author deleted").isTrue();
    }

    //
    @Test
    @DisplayName("Возвращать автора, при запросе автора")
    void author() throws Exception {
        val expectedAuthor = authorService.findById(AUTHOR_ID_WITH_BOOKS).orElseThrow(() -> new NotFoundException("Автор с ID=" + AUTHOR_ID_WITH_BOOKS));
        val result = mockMvc.perform(get("/api/authors/" + AUTHOR_ID_WITH_BOOKS))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expectedAuthor)));
    }


    @Test
    @DisplayName("Выводить всех авторов, при запросе списка авторов")
    void authors() throws Exception {
        List<AuthorDto> expectedAuthors = authorService.findAll();
        val result = mockMvc.perform(get("/api/authors"))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        List<AuthorDto> actualAuthors = mapper.readValue(result, new TypeReference<List<AuthorDto>>() {
        });
        assertThat(actualAuthors)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedAuthors);
    }
}