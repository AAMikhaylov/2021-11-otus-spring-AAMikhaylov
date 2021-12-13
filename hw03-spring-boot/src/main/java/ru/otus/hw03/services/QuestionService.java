package ru.otus.hw03.services;
import ru.otus.hw03.domain.Question;
import ru.otus.hw03.exceptions.QuestionServiceException;

import java.util.List;

public interface QuestionService {
    List<Question> getAllQuestions() throws QuestionServiceException;

    Question getQuestionById(int id) throws QuestionServiceException;

}
