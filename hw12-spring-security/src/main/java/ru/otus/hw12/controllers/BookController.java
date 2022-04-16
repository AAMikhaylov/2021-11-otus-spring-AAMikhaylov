package ru.otus.hw12.controllers;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.otus.hw12.dto.*;
import ru.otus.hw12.exceptions.IntegrityViolationException;
import ru.otus.hw12.exceptions.NotFoundException;
import ru.otus.hw12.services.AuthorService;
import ru.otus.hw12.services.BookService;
import ru.otus.hw12.services.GenreService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;
    private final AuthorService authorService;
    private final GenreService genreService;

    @GetMapping("/")
    public String listPage(Model model) {
        List<BookDto> books = bookService.findAll();
        model.addAttribute("books", books);
        return "index";
    }

    @GetMapping("/book")
    public String detailPage(@RequestParam long id, Model model) {
        val bookDto = bookService.findByIdEager(id).orElseThrow(() -> new NotFoundException("Книга c ID=" + id + " не найдена!"));
        model.addAttribute("book", bookDto);
        return "book";
    }


    @GetMapping("/editBook")
    public String editPage(@RequestParam(required = false) Long id, Model model) {
        BookDto bookDto;
        if (id == null) {
            bookDto = new BookDto(0L, null, "", "", new ArrayList<>(), new ArrayList<>());
        } else {
            bookDto = bookService.findByIdLazy(id).orElseThrow(() -> new NotFoundException("Книга c ID=" + id + " не найдена!"));
        }
        model.addAttribute("book", bookDto);
        List<AuthorDto> authors = authorService.findAll();
        model.addAttribute("authors", authors);
        List<GenreDto> genres = genreService.findAll();
        model.addAttribute("genres", genres);
        return "editBook";
    }

    @Validated
    @PostMapping("/saveBook")
    public String save(@Valid @ModelAttribute("book") BookDto book, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            List<AuthorDto> authors = authorService.findAll();
            model.addAttribute("authors", authors);
            List<GenreDto> genres = genreService.findAll();
            model.addAttribute("genres", genres);
            return "editBook";
        }
        val savedBook = bookService.save(book);
        return "redirect:/book?id=" + savedBook.getId();
    }

    @GetMapping("/deleteBook")
    String delete(@RequestParam Long id) {
        try {
            bookService.delete(id);
        } catch (DataIntegrityViolationException e) {
            throw new IntegrityViolationException("Нельзя удалить книгу c ID=" + id + ". Нарушение целостности БД");
        }
        return "redirect:/";
    }

}


