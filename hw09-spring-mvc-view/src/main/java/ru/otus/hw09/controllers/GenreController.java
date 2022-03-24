package ru.otus.hw09.controllers;

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
import ru.otus.hw09.dto.GenreDto;
import ru.otus.hw09.exceptions.IntegrityViolationException;
import ru.otus.hw09.exceptions.NotFoundException;
import ru.otus.hw09.services.GenreService;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class GenreController {
    private final GenreService genreService;

    @GetMapping("/genres")
    public String listPage(Model model) {
        val genres = genreService.findAll();
        model.addAttribute("genres", genres);
        return "genres";
    }


    @GetMapping("/genre")
    public String detailPage(@RequestParam long id, Model model) {
        val genre = genreService.findById(id).orElseThrow(() -> new NotFoundException("Жанр c ID=" + id + " не найден!"));
        model.addAttribute("genre", genre);
        return "genre";
    }

    @GetMapping("/editGenre")
    public String editPage(@RequestParam(required = false) Long id, Model model) {
        GenreDto genre;
        if (id == null) {
            genre = new GenreDto(0L, "");
        } else {
            genre = genreService.findById(id).orElseThrow(() -> new NotFoundException("Жанр c ID=" + id + " не найден!"));
        }
        model.addAttribute("genre", genre);
        return "editGenre";
    }

    @GetMapping("/deleteGenre")
    String delete(@RequestParam Long id) {
        try {
            genreService.delete(id);
        } catch (DataIntegrityViolationException e) {
            throw new IntegrityViolationException("Нельзя удалить жанр c ID=" + id + ". У жанра есть книги.");
        }
        return "redirect:/genres";
    }

    @Validated
    @PostMapping("/saveGenre")
    public String save(@Valid @ModelAttribute("genre") GenreDto genre, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "editGenre";
        genreService.save(genre);
        return "redirect:/genres";
    }
}

