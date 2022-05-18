package ru.otus.hw16.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.otus.hw16.health.RequestCounter;

@Controller
@RequiredArgsConstructor
public class CommentController {
    private final RequestCounter requestCounter;

    @GetMapping("/comments")
    public String commentsPage() {
        requestCounter.mark();
        return "comments";
    }

    @GetMapping("/comment")
    public String commentDetailPage(@RequestParam String url, Model model) {
        model.addAttribute("url", url);
        requestCounter.mark();
        return "comment";
    }

    @GetMapping("/editComment")
    public String commentEditPage(@RequestParam(required = false) String url, @RequestParam(required = false) String bookUrl, Model model) {
        model.addAttribute("uri", url);
        model.addAttribute("bookUri", bookUrl);
        requestCounter.mark();
        return "editComment";
    }


}
