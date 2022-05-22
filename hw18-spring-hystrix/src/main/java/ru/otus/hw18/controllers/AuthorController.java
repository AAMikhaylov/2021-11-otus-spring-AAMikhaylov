package ru.otus.hw18.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthorController {
    @GetMapping("/authors")
    public String authorsPage() {
        return "authors";
    }

    @GetMapping("/author")
    public String authorDetailPage(@RequestParam Long id, Model model) {
        model.addAttribute("id", id);
        return "author";
    }

    @GetMapping("/editAuthor")
    public String authorEditPage(@RequestParam(required = false) Long id, Model model) {
        Long idAttribute = id == null ? 0L : id;
        model.addAttribute("id", idAttribute);
        return "editAuthor";
    }


}
