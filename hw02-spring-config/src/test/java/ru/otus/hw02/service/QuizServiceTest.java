package ru.otus.hw02.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ru.otus.hw02.domain.Question;
import ru.otus.hw02.present.UIReader;
import ru.otus.hw02.present.UIWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class QuizServiceTest {
    private final static int SCORE_PASS = 4;
    private final static List<Question> QUESTIONS_LIST = List.of(
            new Question(1, "Question 1", "1"),
            new Question(2, "Question 2", "2"),
            new Question(3, "Question 3", "3"),
            new Question(4, "Question 4", "4"),
            new Question(5, "Question 5", "5")
    );
    private final static String[] SUCCESS_ANSWERS_LIST = {"Surname", "1", "2", "3", "4", "5"};
    private final static String[] FAILURE_ANSWERS_LIST = {"Surname", "1", "3", "3", "2", "1"};
    private final static List<String> SUCCESS_TEST_PRINT_LIST = List.of("Enter your name: ", "Enter your surname: ",
            "Question 1\n", "Enter your answer: ", "Your answer: 1. ", "That's right!\n", "-----------------------------------------------\n",
            "Question 2\n", "Enter your answer: ", "Your answer: 2. ", "That's right!\n", "-----------------------------------------------\n",
            "Question 3\n", "Enter your answer: ", "Your answer: 3. ", "That's right!\n", "-----------------------------------------------\n",
            "Question 4\n", "Enter your answer: ", "Your answer: 4. ", "That's right!\n", "-----------------------------------------------\n",
            "Question 5\n", "Enter your answer: ", "Your answer: 5. ", "That's right!\n", "-----------------------------------------------\n",
            "Name Surname, congratulations! You passed this test! Your score: 5\n");
    private final static List<String> FAIL_TEST_PRINT_LIST = List.of("Enter your name: ", "Enter your surname: ",
            "Question 1\n", "Enter your answer: ", "Your answer: 1. ", "That's right!\n", "-----------------------------------------------\n",
            "Question 2\n", "Enter your answer: ", "Your answer: 3. ", "It's wrong!\n", "-----------------------------------------------\n",
            "Question 3\n", "Enter your answer: ", "Your answer: 3. ", "That's right!\n", "-----------------------------------------------\n",
            "Question 4\n", "Enter your answer: ", "Your answer: 2. ", "It's wrong!\n", "-----------------------------------------------\n",
            "Question 5\n", "Enter your answer: ", "Your answer: 1. ", "It's wrong!\n", "-----------------------------------------------\n",
            "Name Surname, sorry, you did not pass this test. Your score: 2\n");
    private static QuizService quizService;
    private static final List<String> printingStrings = new ArrayList<>();
    private static UIReader uiReader;


    @BeforeAll
    static void beforeAll() {
        QuestionService questionService = mock(QuestionService.class);
        when(questionService.getAllQuestions()).thenReturn(QUESTIONS_LIST);
        uiReader = mock(UIReader.class);
        UIWriter uiWriter = mock(UIWriter.class);
        doAnswer(invocationOnMock -> {
            printingStrings.add(invocationOnMock.getArgument(0).toString());
            return null;
        }).when(uiWriter).write(any());
        quizService = new QuizService(questionService, SCORE_PASS, uiReader, uiWriter);
    }

    @BeforeEach
    void beforeEach() {
        printingStrings.clear();
        reset(uiReader);
    }

    @Test
    void startSuccessTest() throws IOException {
        when(uiReader.read()).thenReturn("Name", SUCCESS_ANSWERS_LIST);
        quizService.start();
        Assertions.assertEquals(printingStrings, SUCCESS_TEST_PRINT_LIST);
    }

    @Test
    void startFailureTest() throws IOException {
        when(uiReader.read()).thenReturn("Name", FAILURE_ANSWERS_LIST);
        quizService.start();
        Assertions.assertEquals(printingStrings, FAIL_TEST_PRINT_LIST);
    }

}