package ru.otus.hw16.controllers.validators;

import lombok.val;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.otus.hw16.domain.Book;

@Component("beforeSaveBookValidator")
public class BookValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(Book.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        val book = (Book) target;
        if (book.getAuthor() == null)
            errors.rejectValue("author", "author.isNull", "Выберите автора.");
        if (book.getTitle().trim().equals(""))
            errors.rejectValue("title", "title.empty", "Название не должно быть быть пустым");
        if (book.getTitle().length() < 2 || book.getTitle().length() > 25)
            errors.rejectValue("title", "title.length", "Длина названия должна быть от 2 до 50 символов");
        if (book.getShortContent().length() > 1000)
            errors.rejectValue("shortContent", "shortContent.length", "Длина краткого содержания должно быть не более 1000 символов");
        if (book.getGenres() == null)
            errors.rejectValue("genres", "genres.isNull", "Выберите жанр(ы).");
    }
}
