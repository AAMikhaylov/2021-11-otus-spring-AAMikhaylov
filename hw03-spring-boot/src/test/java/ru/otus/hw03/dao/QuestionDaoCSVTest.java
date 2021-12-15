package ru.otus.hw03.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.otus.hw03.domain.Question;
import ru.otus.hw03.exceptions.QuestionDaoException;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;


@SpringBootTest
@DisplayName("Методы QuestionDao должны:")
class QuestionDaoCSVTest {
    @Configuration
    public static class TestContextConfig {
        @MockBean
        private FileReader fileReader;

        @Bean
        QuestionDaoCSV questionDao() {
            return new QuestionDaoCSV(fileReader);
        }
    }

    private final static String CSV_DATA = "id,question,answer\r\n" +
            "3,\"test question 1\",33\r\n" +
            "2,\"test question 5\",1";
    private final static List<Question> EXPECTED_QUESTIONS = List.of(
            new Question(3, "test question 1", "33"),
            new Question(2, "test question 5", "1"));
    @Autowired
    private FileReader fileReader;
    @Autowired
    private QuestionDaoCSV questionDaoCSV;


    @BeforeEach
    void setUp() throws IOException {
        when(fileReader.getFileData()).thenReturn(CSV_DATA);
    }

    @Test
    @DisplayName("Возвращать все вопросы списком")
    void testGetAll() throws QuestionDaoException {
        List<Question> questionList = questionDaoCSV.getAll();
        assertThat(questionList)
                .usingRecursiveComparison()
                .isEqualTo(EXPECTED_QUESTIONS);
    }

    @Test
    @DisplayName("Выполнять поиск вопроса по ID")
    void testFindById() throws QuestionDaoException {
        Question actualQuestion = questionDaoCSV.findById(2);
        assertThat(actualQuestion)
                .usingRecursiveComparison()
                .isEqualTo(EXPECTED_QUESTIONS.get(1));
    }

    @Test
    @DisplayName("Бросать исключение при ошибке чтения файла ")
    void testExceptions() throws IOException {
        when(fileReader.getFileData()).thenThrow(new IOException());
        assertThatThrownBy(() -> questionDaoCSV.findById(2))
                .isInstanceOf(QuestionDaoException.class)
                .hasCauseInstanceOf(IOException.class)
                .hasMessage("Can't get question by id.");
        assertThatThrownBy(questionDaoCSV::getAll)
                .isInstanceOf(QuestionDaoException.class)
                .hasCauseInstanceOf(IOException.class)
                .hasMessage("Can't get questions.");
    }

    @AfterEach
    void tearDown() {
        reset(fileReader);
    }
}