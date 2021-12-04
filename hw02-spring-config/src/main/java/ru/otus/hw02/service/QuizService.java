package ru.otus.hw02.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.otus.hw02.domain.Question;
import ru.otus.hw02.present.UIReader;
import ru.otus.hw02.present.UIWriter;

import java.io.IOException;
import java.util.List;

@Service
public class QuizService {
    private final QuestionService questionService;
    private final int scorePass;
    private final UIWriter uiWriter;
    private final UIReader uiReader;

    public QuizService(QuestionService questionService, @Value("${quiz.scorePass}") int scorePass, UIReader uiReader, UIWriter uiWriter) {
        this.questionService = questionService;
        this.scorePass = scorePass;
        this.uiWriter = uiWriter;
        this.uiReader = uiReader;
    }

    public void start() {
        try {
            uiWriter.write("Enter your name: ");
            String name = uiReader.read();
            uiWriter.write("Enter your surname: ");
            String surname = uiReader.read();
            List<Question> questions = questionService.getAllQuestions();
            int score = 0;
            for (Question question : questions) {
                uiWriter.write(question.getContent() + "\n");
                uiWriter.write("Enter your answer: ");
                String answer = uiReader.read();
                uiWriter.write("Your answer: " + answer + ". ");
                if (answer.equalsIgnoreCase(question.getAnswer())) {
                    score++;
                    uiWriter.write("That's right!\n");
                } else {
                    uiWriter.write("It's wrong!\n");
                }
                uiWriter.write("-----------------------------------------------\n");
            }
            if (score >= scorePass)
                uiWriter.write(name + " " + surname + ", congratulations! You passed this test! Your score: " + score + "\n");
            else
                uiWriter.write(name + " " + surname + ", sorry, you did not pass this test. Your score: " + score + "\n");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
