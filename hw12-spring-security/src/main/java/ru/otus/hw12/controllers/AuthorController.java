package ru.otus.hw12.controllers;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.otus.hw12.dto.AuthorDto;
import ru.otus.hw12.exceptions.IntegrityViolationException;
import ru.otus.hw12.exceptions.NotFoundException;
import ru.otus.hw12.services.AuthorService;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class AuthorController {
    private final AuthorService authorService;

    @GetMapping("/authors")
    public String listPage(Model model) {
        List<AuthorDto> authors = authorService.findAll();
        model.addAttribute("authors", authors);
        return "authors";
    }

    @GetMapping("/author")
    public String detailPage(@RequestParam long id, Model model) {
        val author = authorService.findById(id).orElseThrow(() -> new NotFoundException("Автор c ID=" + id + " не найден!"));
        model.addAttribute("author", author);
        return "author";
    }

    @GetMapping("/editAuthor")
    public String editPage(@RequestParam(required = false) Long id, Model model) {
        AuthorDto author;
        if (id == null) {
            author = new AuthorDto(0L, "", "", "");
        } else {
            author = authorService.findById(id).orElseThrow(() -> new NotFoundException("Автор c ID=" + id + " не найден!"));
        }
        model.addAttribute("author", author);
        return "editAuthor";
    }

    @GetMapping("/deleteAuthor")
    String delete(@RequestParam Long id) {
        try {
            authorService.delete(id);
        } catch (DataIntegrityViolationException e) {
            throw new IntegrityViolationException("Нельзя удалить автора c ID=" + id + ". У автора есть книги.");
        }
        return "redirect:/authors";
    }

    @Validated
    @PostMapping("/saveAuthor")
    public String save(@Valid @ModelAttribute("author") AuthorDto author, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "editAuthor";
        val savedAuthor = authorService.save(author);
        return "redirect:/author?id=" + savedAuthor.getId();
    }
}
