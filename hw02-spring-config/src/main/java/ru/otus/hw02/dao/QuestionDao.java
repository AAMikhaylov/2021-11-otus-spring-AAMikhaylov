package ru.otus.hw02.dao;

import org.springframework.stereotype.Component;
import ru.otus.hw02.domain.Question;

import java.util.List;
public interface QuestionDao {
    List<Question> getAll();

    Question findById(int id);

}
