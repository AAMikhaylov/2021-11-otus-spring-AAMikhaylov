package ru.otus.hw17.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class BookController {

    @GetMapping("/")
    public String booksPage() {
        return "index";
    }

    @GetMapping("/book")
    public String bookDetailPage(@RequestParam Long id, Model model) {
        model.addAttribute("id", id);
        return "book";
    }

    @GetMapping("/editBook")
    public String bookEditPage(@RequestParam(required = false) Long id, Model model) {
        Long idAttribute = id == null ? 0L : id;
        model.addAttribute("id", idAttribute);
        return "editBook";
    }
}


