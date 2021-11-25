package ru.otus.hw01.service;

import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import ru.otus.hw01.dao.QuestionDao;
import ru.otus.hw01.domain.Question;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class QuestionsServiceImplTest {

    private static List<Question> questionList;
    private static QuestionService questionService;

    @BeforeAll
    static void beforeAll() {
        questionList = new ArrayList<>();
        questionList.add(new Question(1, "Test question 1", "Test answer 1"));
        questionList.add(new Question(2, "Test question 2", "Test answer 2"));
        questionList.add(new Question(3, "Test question 3", "Test answer 3"));
        questionList.add(new Question(4, "Test question 4", "Test answer 4"));
        QuestionDao questionDao;
        questionDao = Mockito.mock(QuestionDao.class);
        when(questionDao.getAll()).thenReturn(new ArrayList<>(questionList));
        when(questionDao.findById(anyInt()))
                .thenAnswer(invocationOnMock -> {
                            int id = invocationOnMock.getArgument(0);
                            for (Question question : questionList)
                                if (question.getId() == id)
                                    return question;
                            return null;
                        }
                );
        questionService = new QuestionsServiceImpl(questionDao);
    }

    @Test
    void getAllQuestions() {
        List<Question> allQuestions = questionService.getAllQuestions();
        assertEquals(allQuestions, questionList);
    }

    @Test
    void getQuestionById() {
        for (int i = 1; i < 5; i++) {
            Question question = questionService.getQuestionById(i);
            assertEquals(i, question.getId());
        }
        Question question = questionService.getQuestionById(5);
        assertNull(question);
    }
}