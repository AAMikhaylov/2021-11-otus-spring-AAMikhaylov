package ru.otus.hw11.controllers;

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
    public String commentDetailPage(@RequestParam String id, Model model) {
        model.addAttribute("id", id);
        return "comment";
    }

    @GetMapping("/editComment")
    public String commentEditPage(@RequestParam(required = false) String id, @RequestParam(required = false) String bookId, Model model) {
        model.addAttribute("id", id == null ? 0 : id);
        model.addAttribute("bookId", bookId == null ? 0 : bookId);
        return "editComment";
    }


}
