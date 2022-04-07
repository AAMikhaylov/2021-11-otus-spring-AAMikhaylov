package ru.otus.hw10.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class GenreController {

    @GetMapping("/genres")
    public String genresPage() {
        return "genres";
    }

    @GetMapping("/genre")
    public String genreDetailPage(@RequestParam Long id, Model model) {
        model.addAttribute("id", id);
        return "genre";
    }

    @GetMapping("/editGenre")
    public String genreEditPage(@RequestParam(required = false) Long id, Model model) {
        Long idAttribute = id == null ? 0L : id;
        model.addAttribute("id", idAttribute);
        return "editGenre";
    }
}

