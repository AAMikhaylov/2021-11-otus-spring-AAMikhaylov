package ru.otus.hw16.controllers.validators;

import lombok.val;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.otus.hw16.domain.Comment;

@Component("beforeSaveCommentValidator")
public class CommentValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(Comment.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        val comment = (Comment) target;
        if (comment.getUserName().trim().equals(""))
            errors.rejectValue("userName", "userName.empty", "Имя пользователя не должно быть пустым");
        if (comment.getUserName().length() < 2 || comment.getUserName().length() > 25)
            errors.rejectValue("userName", "userName.length", "Длина имени пользователя должна быть от 2 до 50 символов");
        if (comment.getContent().trim().equals(""))
            errors.rejectValue("content", "content.empty", "Комментарий не должен быть пустым");
        if (comment.getContent().length() > 1000)
            errors.rejectValue("content", "content.length", "Длина комментария не более 1000 символов");
    }
}
