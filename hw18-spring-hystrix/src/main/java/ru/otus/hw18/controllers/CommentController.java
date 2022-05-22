package ru.otus.hw18.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CommentController {

    @GetMapping("/comments")
    public String commentsPage() {
        return "comments";
    }

    @GetMapping("/comment")
    public String commentDetailPage(@RequestParam Long id, Model model) {
        model.addAttribute("id", id);
        return "comment";
    }

    @GetMapping("/editComment")
    public String commentEditPage(@RequestParam(required = false) Long id, @RequestParam(required = false) Long bookId, Model model) {
        Long idAttribute = id == null ? 0L : id;
        Long bookIdAttribute = bookId == null ? 0L : bookId;
        model.addAttribute("id", idAttribute);
        model.addAttribute("bookId", bookIdAttribute);
        return "editComment";
    }


}
