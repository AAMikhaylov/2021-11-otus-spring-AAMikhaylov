package ru.otus.hw16.controllers.validators;


import lombok.val;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.otus.hw16.domain.Author;


@Component("beforeSaveAuthorValidator")
public class AuthorValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(Author.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        val author = (Author) target;
        if (author.getFirstName().trim().equals(""))
            errors.rejectValue("firstName", "firstName.empty", "Имя не должно быть быть пустым");
        if (author.getFirstName().length() < 2 || author.getFirstName().length() > 25)
            errors.rejectValue("firstName", "firstName.length", "Длина имени должна быть от 2 до 25 символов");

        if (author.getMiddleName().trim().equals(""))
            errors.rejectValue("middleName", "middleName.empty", "Отчество не должно быть быть пустым");
        if (author.getMiddleName().length() < 2 || author.getMiddleName().length() > 25)
            errors.rejectValue("middleName", "middleName.length", "Длина отчества должна быть от 2 до 25 символов");

        if (author.getLastName().trim().equals(""))
            errors.rejectValue("lastName", "lastName.empty", "Фамилия не должна быть быть пустой");
        if (author.getLastName().length() < 2 || author.getLastName().length() > 25)
            errors.rejectValue("lastName", "lastName.length", "Длина фамилии должна быть от 2 до 25 символов");
    }
}
