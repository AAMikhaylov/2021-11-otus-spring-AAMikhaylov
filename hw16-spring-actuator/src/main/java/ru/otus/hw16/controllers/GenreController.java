package ru.otus.hw16.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.otus.hw16.health.RequestCounter;

@Controller
@RequiredArgsConstructor
public class GenreController {
    private final RequestCounter requestCounter;

    @GetMapping("/genres")
    public String genresPage() {
        requestCounter.mark();
        return "genres";
    }

    @GetMapping("/genre")
    public String genreDetailPage(@RequestParam String url, Model model) {
        model.addAttribute("url", url);
        requestCounter.mark();
        return "genre";
    }

    @GetMapping("/editGenre")
    public String genreEditPage(@RequestParam(required = false) String url, Model model) {
        model.addAttribute("url", url);
        requestCounter.mark();
        return "editGenre";
    }
}
