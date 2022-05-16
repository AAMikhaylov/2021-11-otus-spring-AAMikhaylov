package ru.otus.hw16.controllers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.otus.hw16.health.RequestCounter;

@Controller
@RequiredArgsConstructor
public class BookController {
    private final RequestCounter requestCounter;

    @GetMapping("/")
    public String booksPage() {
        requestCounter.mark();
        return "index";
    }

    @GetMapping("/book")
    public String bookDetailPage(@RequestParam String url, Model model) {
        requestCounter.mark();
        model.addAttribute("url",url);
        return "book";
    }

    @GetMapping("/editBook")
    public String bookEditPage(@RequestParam(required = false) String url, Model model) {
        requestCounter.mark();
        model.addAttribute("url", url);
        return "editBook";
    }
}


