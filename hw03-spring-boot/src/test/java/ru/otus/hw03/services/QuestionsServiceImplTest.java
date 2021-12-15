package ru.otus.hw03.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.otus.hw03.dao.QuestionDao;
import ru.otus.hw03.domain.Question;
import ru.otus.hw03.exceptions.QuestionDaoException;
import ru.otus.hw03.exceptions.QuestionServiceException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("Методы QuestionServiceImpl должны:")
class QuestionsServiceImplTest {
    @Configuration
    public static class TestContextConfig {
        @MockBean
        QuestionDao questionDao;

        @Bean
        QuestionService questionService(QuestionDao questionDao) {
            return new QuestionsServiceImpl(questionDao);
        }
    }

    private static final List<Question> EXPECTED_QUESTIONS = List.of(
            new Question(1, "Test question 1", "Test answer 1"),
            new Question(2, "Test question 2", "Test answer 2"),
            new Question(3, "Test question 3", "Test answer 3"),
            new Question(4, "Test question 4", "Test answer 4")
    );
    @Autowired
    private QuestionDao questionDao;
    @Autowired
    private QuestionService questionService;

    @BeforeEach
    void setUp() throws QuestionDaoException {
        when(questionDao.getAll()).thenReturn(new ArrayList<>(EXPECTED_QUESTIONS));
        when(questionDao.findById(anyInt()))
                .thenAnswer(invocationOnMock -> {
                            int id = invocationOnMock.getArgument(0);
                            for (Question question : EXPECTED_QUESTIONS)
                                if (question.getId() == id)
                                    return question;
                            return null;
                        }
                );
    }

    @Test
    @DisplayName("Возвращать все вопросы списком")
    void testGetAllQuestions() throws QuestionServiceException {
        List<Question> allQuestions = questionService.getAllQuestions();
        assertThat(allQuestions)
                .hasSize(4)
                .usingRecursiveComparison()
                .isEqualTo(EXPECTED_QUESTIONS);
    }

    @Test
    @DisplayName("Возвращать вопрос по ID")
    void testGetQuestionById() throws QuestionServiceException {
        for (int i = 1; i < 5; i++) {
            assertThat(questionService.getQuestionById(i))
                    .usingRecursiveComparison()
                    .isEqualTo(EXPECTED_QUESTIONS.get(i - 1));
        }
        assertThat(questionService.getQuestionById(5)).isNull();
    }

    @Test
    @DisplayName("Бросать исключение, если ошибка в DAO")
    void testException() throws QuestionDaoException {
        when(questionDao.getAll()).thenThrow(new QuestionDaoException("Dao getall error."));
        when(questionDao.findById(anyInt())).thenThrow(new QuestionDaoException("Dao getById error."));
        assertThatThrownBy(questionService::getAllQuestions)
                .isInstanceOf(QuestionServiceException.class)
                .hasCauseInstanceOf(QuestionDaoException.class)
                .hasRootCauseMessage("Dao getall error.")
                .hasMessage("Can't get all questions");
        assertThatThrownBy(() -> questionService.getQuestionById(2))
                .isInstanceOf(QuestionServiceException.class)
                .hasCauseInstanceOf(QuestionDaoException.class)
                .hasRootCauseMessage("Dao getById error.")
                .hasMessage("Can't get question by id");


    }


}