package ru.otus.hw01.service;

import ru.otus.hw01.dao.QuestionDao;
import ru.otus.hw01.domain.Question;

import java.util.List;

public class QuestionsServiceImpl implements QuestionService {
    private final QuestionDao questionDao;

    public QuestionsServiceImpl(QuestionDao questionDao) {
        this.questionDao = questionDao;
    }

    @Override
    public List<Question> getAllQuestions() {
        return questionDao.getAll();
    }

    @Override
    public Question getQuestionById(int id) {
        return questionDao.findById(id);
    }
}
