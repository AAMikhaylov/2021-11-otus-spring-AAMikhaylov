package ru.otus.hw11.controllers;

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
    public String bookDetailPage(@RequestParam String id, Model model) {
        model.addAttribute("id", id);
        return "book";
    }

    @GetMapping("/editBook")
    public String bookEditPage(@RequestParam(required = false) String id, Model model) {
        model.addAttribute("id", id == null ? 0 : id);
        return "editBook";
    }
}


