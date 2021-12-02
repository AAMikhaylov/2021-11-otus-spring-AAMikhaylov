package ru.otus.hw02.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.otus.hw02.dao.QuestionDao;
import ru.otus.hw02.domain.Question;

import java.util.List;
@Component
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
