package ru.otus.hw16.controllers.validators;

import lombok.val;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.otus.hw16.domain.Genre;

@Component("beforeSaveGenreValidator")
public class GenreValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(Genre.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        val genre = (Genre) target;
        if (genre.getName().trim().equals(""))
            errors.rejectValue("name", "name.empty", "Наименование не должно быть быть пустым");
        if (genre.getName().length() < 2 || genre.getName().length() > 25)
            errors.rejectValue("name", "name.length", "Длина наименования должна быть от 2 до 25 символов");
    }
}
