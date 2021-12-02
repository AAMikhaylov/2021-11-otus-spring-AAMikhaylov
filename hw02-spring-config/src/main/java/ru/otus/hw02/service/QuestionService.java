package ru.otus.hw02.service;

import ru.otus.hw02.domain.Question;

import java.util.List;
public interface QuestionService {
    List<Question> getAllQuestions();

    Question getQuestionById(int id);

}
