package ru.otus.hw02.application;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.otus.hw02.domain.Question;
import ru.otus.hw02.service.QuestionService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

@Service
public class ApplicationService {
    private final QuestionService questionService;
    private final int passScore;

    public ApplicationService(QuestionService questionService, @Value("${testing.passScore}") int passScore) {
        this.questionService = questionService;
        this.passScore = passScore;
    }

    public void start() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.print("Enter your name: ");
            String name = br.readLine();
            System.out.print("Enter your surname: ");
            String surname = br.readLine();
            List<Question> questions = questionService.getAllQuestions();
            int score = 0;
            for (Question question : questions) {
                System.out.println(question.getContent());
                System.out.print("Enter your answer: ");
                String answer = br.readLine();
                System.out.print("Your answer: " + answer + ". ");
                if (answer.equalsIgnoreCase(question.getAnswer())) {
                    score++;
                    System.out.println("That's right!");
                } else {
                    System.out.println("It's wrong!");
                }
                System.out.println("-----------------------------------------------");
            }
        if (score>=passScore)
            System.out.println(name+" "+surname+ ", congratulations! You passed this test!");
        else
            System.out.println(name+" "+surname+ ", sorry, you did not pass this test.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
