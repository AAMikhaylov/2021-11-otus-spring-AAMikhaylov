package ru.otus.hw03.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.otus.hw03.domain.Question;
import ru.otus.hw03.exceptions.QuestionServiceException;
import ru.otus.hw03.io.IOMessageService;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


@SpringBootTest
@DisplayName("Класс QuizService должен:")
class QuizServiceTest {
    @Configuration
    public static class TestContextConfig {
        @MockBean
        QuestionService questionService;
        @MockBean
        IOMessageService ioMessageService;

        @Bean
        QuizService quizService(QuestionService questionService, IOMessageService msgService, @Value("${application.scorePass}") int scorePass) {
            return new QuizService(questionService, msgService, scorePass);
        }
    }

    private final static List<Question> QUESTIONS_LIST = List.of(
            new Question(1, "Question 1", "Answer 1"),
            new Question(2, "Question 2", "Answer 2"),
            new Question(3, "Question 3", "Answer 3"),
            new Question(4, "Question 4", "Answer 4"),
            new Question(5, "Question 5", "Answer 5")
    );
    private final static List<String> SUCCESS_PRINT_EXPECTED = List.of(
            "messages.enterName", "messages.enterSurname",
            "Question 1",
            "messages.enterAnswer", "messages.successAnswer",
            "Question 2",
            "messages.enterAnswer", "messages.successAnswer",
            "Question 3",
            "messages.enterAnswer", "messages.successAnswer",
            "Question 4",
            "messages.enterAnswer", "messages.successAnswer",
            "Question 5",
            "messages.enterAnswer", "messages.successAnswer",
            "messages.successResult",
            "5"
    );
    private final static List<String> FAIL_PRINT_EXPECTED = List.of(
            "messages.enterName", "messages.enterSurname",
            "Question 1",
            "messages.enterAnswer", "messages.successAnswer",
            "Question 2",
            "messages.enterAnswer", "messages.successAnswer",
            "Question 3",
            "messages.enterAnswer", "messages.successAnswer",
            "Question 4",
            "messages.enterAnswer", "messages.successAnswer",
            "Question 5",
            "messages.enterAnswer", "messages.successAnswer",
            "messages.failResult",
            "2"
    );

    private final static String[] SUCCESS_ANSWERS = {"Surname", "Answer 1", "Answer 2", "Answer 3", "Answer 4", "Answer 5"};
    private final static String[] FAIL_ANSWERS = {"Surname", "Answer 2", "Answer 3", "Answer 3", "Answer 2", "Answer 5"};

    private final List<String> printedStrings = new ArrayList<>();

    @Autowired
    private QuestionService questionService;
    @Autowired
    private IOMessageService ioMessageService;
    @Autowired
    private QuizService quizService;


    @BeforeEach
    void setUp() throws QuestionServiceException {
        when(questionService.getAllQuestions()).thenReturn(QUESTIONS_LIST);
        doAnswer(invocationOnMock -> {
            printedStrings.add(invocationOnMock.getArgument(0).toString().trim());
            return null;
        }).when(ioMessageService).write(anyString());
        doAnswer(invocationOnMock -> {
            String arg0 = invocationOnMock.getArgument(0).toString();
            printedStrings.add(arg0.trim());
            if (("messages.successResult".equals(arg0) || "messages.failResult".equals(arg0))) {
                String score = invocationOnMock.getArgument(3).toString();
                printedStrings.add(score);
            }
            return null;
        }).when(ioMessageService).writeLocal(anyString(), any());
    }

    @AfterEach
    void tearDown() {
        reset(ioMessageService);
    }

    @Test
    @DisplayName("Выводить ожидаемые сообщения в случае успешного теста")
    void successTestStart() {
        when(ioMessageService.read()).thenReturn("Name", SUCCESS_ANSWERS);
        quizService.start();
        assertThat(printedStrings).containsAll(SUCCESS_PRINT_EXPECTED);
    }

    @Test
    @DisplayName("Выводить ожидаемые сообщения в случае провального теста")
    void failTestStart() {
        when(ioMessageService.read()).thenReturn("Name", FAIL_ANSWERS);
        quizService.start();
        assertThat(printedStrings).containsAll(FAIL_PRINT_EXPECTED);
    }

}