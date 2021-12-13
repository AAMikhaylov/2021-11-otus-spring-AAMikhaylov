package ru.otus.hw03.services;

import org.springframework.stereotype.Component;
import ru.otus.hw03.dao.QuestionDao;
import ru.otus.hw03.domain.Question;
import ru.otus.hw03.exceptions.QuestionServiceException;

import java.util.List;

@Component
public class QuestionsServiceImpl implements QuestionService {
    private final QuestionDao questionDao;

    public QuestionsServiceImpl(QuestionDao questionDao) {
        this.questionDao = questionDao;
    }

    @Override
    public List<Question> getAllQuestions() throws QuestionServiceException {
        try {
            return questionDao.getAll();
        } catch (Exception e) {
            throw new QuestionServiceException("Can't get all questions", e);
        }

    }

    @Override
    public Question getQuestionById(int id) throws QuestionServiceException {
        try {
            return questionDao.findById(id);
        } catch (Exception e) {
            throw new QuestionServiceException("Can't get question by id", e);
        }
    }
}
