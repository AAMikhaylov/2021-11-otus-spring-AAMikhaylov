package ru.otus.hw04.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.otus.hw04.domain.Person;
import ru.otus.hw04.domain.Question;
import ru.otus.hw04.exceptions.QuestionServiceException;
import ru.otus.hw04.io.IOMessageService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.*;


@SpringBootTest(classes = {QuizServiceImpl.class})
@DisplayName("Класс QuizServiceImpl должен:")
class QuizServiceImplTest {
    @MockBean
    QuestionService questionService;
    @MockBean
    IOMessageService ioMessageService;
    @Autowired
    private QuizService quizService;

    private final static Person EXPECTED_PERSON = new Person("expectedName", "expectedSurname");
    private final static List<Question> QUESTIONS_LIST = List.of(
            new Question(1, "Question 1", "Answer 1"),
            new Question(2, "Question 2", "Answer 2"),
            new Question(3, "Question 3", "Answer 3"),
            new Question(4, "Question 4", "Answer 4"),
            new Question(5, "Question 5", "Answer 5")
    );
    private final static List<String> SUCCESS_PRINT_EXPECTED = List.of(
            "Question 1",
            "Question 2",
            "Question 3",
            "Question 4",
            "Question 5",
            "messages.enterAnswer", "messages.successAnswer",
            "messages.successResult",
            EXPECTED_PERSON.getName(),
            EXPECTED_PERSON.getSurname(),
            "5"
    );
    private final static List<String> FAIL_PRINT_EXPECTED = List.of(
            "Question 1",
            "Question 2",
            "Question 3",
            "Question 4",
            "Question 5",
            "messages.enterAnswer", "messages.successAnswer",
            "messages.enterAnswer", "messages.failAnswer",
            "messages.failResult",
            EXPECTED_PERSON.getName(),
            EXPECTED_PERSON.getSurname(),
            "2"
    );
    private final static String[] SUCCESS_ANSWERS = {"Answer 1", "Answer 2", "Answer 3", "Answer 4", "Answer 5"};
    private final static String[] FAIL_ANSWERS = {"Answer 2", "Answer 3", "Answer 3", "Answer 2", "Answer 5"};
    private final List<String> printedStrings = new ArrayList<>();

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
                printedStrings.add(invocationOnMock.getArgument(1).toString());
                printedStrings.add(invocationOnMock.getArgument(2).toString());
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
        when(ioMessageService.read()).thenReturn(SUCCESS_ANSWERS[0], Arrays.copyOfRange(SUCCESS_ANSWERS, 1, SUCCESS_ANSWERS.length));
        quizService.start(EXPECTED_PERSON);
        assertThat(printedStrings).containsAll(SUCCESS_PRINT_EXPECTED);
    }

    @Test
    @DisplayName("Выводить ожидаемые сообщения в случае провального теста")
    void failTestStart() {
        when(ioMessageService.read()).thenReturn(FAIL_ANSWERS[0], Arrays.copyOfRange(FAIL_ANSWERS, 1, FAIL_ANSWERS.length));
        quizService.start(EXPECTED_PERSON);
        assertThat(printedStrings).containsAll(FAIL_PRINT_EXPECTED);
    }
}