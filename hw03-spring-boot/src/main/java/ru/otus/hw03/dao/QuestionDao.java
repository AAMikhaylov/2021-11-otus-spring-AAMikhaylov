package ru.otus.hw03.dao;

import ru.otus.hw03.domain.Question;
import ru.otus.hw03.exceptions.QuestionDaoException;

import java.util.List;

public interface QuestionDao {
    List<Question> getAll() throws QuestionDaoException;

    Question findById(int id) throws QuestionDaoException;

}
