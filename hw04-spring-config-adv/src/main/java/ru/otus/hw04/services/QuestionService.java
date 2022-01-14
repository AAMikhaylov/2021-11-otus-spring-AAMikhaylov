package ru.otus.hw04.services;
import ru.otus.hw04.domain.Question;
import ru.otus.hw04.exceptions.QuestionServiceException;

import java.util.List;

public interface QuestionService {
    List<Question> getAllQuestions() throws QuestionServiceException;

    Question getQuestionById(int id) throws QuestionServiceException;

}
