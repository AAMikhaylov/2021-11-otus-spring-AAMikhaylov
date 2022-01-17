package ru.otus.hw04.dao;

import ru.otus.hw04.domain.Question;
import ru.otus.hw04.exceptions.QuestionDaoException;

import java.util.List;

public interface QuestionDao {
    List<Question> getAll() throws QuestionDaoException;

    Question findById(int id) throws QuestionDaoException;

}
