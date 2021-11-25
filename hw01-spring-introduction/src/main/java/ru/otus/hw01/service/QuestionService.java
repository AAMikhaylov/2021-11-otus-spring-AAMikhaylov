package ru.otus.hw01.service;

import ru.otus.hw01.domain.Question;

import java.util.List;

public interface QuestionService {
    List<Question> getAllQuestions();

    Question getQuestionById(int id);

}
