package ru.otus.hw09.controllers;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.otus.hw09.dto.CommentDto;
import ru.otus.hw09.exceptions.BadRequestException;
import ru.otus.hw09.exceptions.IntegrityViolationException;
import ru.otus.hw09.exceptions.NotFoundException;
import ru.otus.hw09.services.BookService;
import ru.otus.hw09.services.CommentService;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;
    private final BookService bookService;

    @GetMapping("/comments")
    public String listPage(Model model) {
        val comments = commentService.findAll();
        model.addAttribute("comments", comments);
        return "comments";
    }

    @GetMapping("/comment")
    public String detailPage(@RequestParam long id, Model model) {
        val comment = commentService.findById(id).orElseThrow(() -> new NotFoundException("Комментарий c ID=" + id + " не найден!"));
        model.addAttribute("comment", comment);
        return "comment";
    }

    @GetMapping("/editComment")
    public String editPage(@RequestParam(required = false) Long id, @RequestParam(required = false) Long bookId, Model model) {
        CommentDto commentDto;
        if (id == null) {
            if (bookId == null) {
                throw new BadRequestException("Book ID must be present");
            }
            val book = bookService.findByIdLazy(bookId).orElseThrow(() -> new NotFoundException("Книга c ID=" + bookId + " не найдена!"));
            commentDto = new CommentDto(0L, null, null, book, null);
        } else {
            commentDto = commentService.findById(id).orElseThrow(() -> new NotFoundException("Комментарий c ID=" + id + " не найден!"));
        }
        model.addAttribute("comment", commentDto);
        return "editComment";
    }

    @Validated
    @PostMapping("/saveComment")
    public String save(@Valid @ModelAttribute("comment") CommentDto comment, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "editComment";
        }
        if (comment.getBook() == null) {
            throw new BadRequestException("Book for comment must present");
        }
        val savedComment = commentService.save(comment);
        if (comment.getBook().getId() != 0L)
            return "redirect:/book?id=" + savedComment.getBook().getId();
        else
            return "redirect:/comment?id=" + savedComment.getId();
    }

    @GetMapping("/deleteComment")
    String delete(@RequestParam Long id) {
        try {
            val comment = commentService.findById(id).orElseThrow(() -> new NotFoundException("Комментарий c ID=" + id + " не найден!"));
            val bookId = comment.getBook().getId();
            commentService.delete(id);
            return "redirect:/book?id=" + bookId;
        } catch (DataIntegrityViolationException e) {
            throw new IntegrityViolationException("Нельзя удалить комментарий c ID=" + id + ". Нарушение целостности БД");
        }

    }

}
