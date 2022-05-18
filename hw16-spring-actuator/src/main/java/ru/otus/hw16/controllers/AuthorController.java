package ru.otus.hw16.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.otus.hw16.health.RequestCounter;

@Controller
@RequiredArgsConstructor
public class AuthorController {
    private final RequestCounter requestCounter;

    @GetMapping("/authors")
    public String authorsPage() {
        requestCounter.mark();
        return "authors";
    }

    @GetMapping("/author")
    public String authorDetailPage(@RequestParam String url, Model model) {
        model.addAttribute("url", url);
        requestCounter.mark();
        return "author";
    }

    @GetMapping("/editAuthor")
    public String authorEditPage(@RequestParam(required = false) String url, Model model) {
        model.addAttribute("url", url);
        requestCounter.mark();
        return "editAuthor";
    }
}
