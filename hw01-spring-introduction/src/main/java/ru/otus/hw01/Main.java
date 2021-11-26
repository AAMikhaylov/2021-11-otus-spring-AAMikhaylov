package ru.otus.hw01;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.otus.hw01.domain.Question;
import ru.otus.hw01.service.QuestionService;

import java.io.*;
import java.util.List;


public class Main {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("/spring-context.xml");
        QuestionService questionService = context.getBean(QuestionService.class);
        List<Question> questions = questionService.getAllQuestions();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            for (Question question : questions) {
                System.out.println(question.getContent());
                System.out.print("Enter your answer:");
                String answer = br.readLine();
                System.out.print("Your answer: " + answer + ". ");
                if (answer.equalsIgnoreCase(question.getAnswer()))
                    System.out.println("That's right!");
                else
                    System.out.println("It's wrong!");
                System.out.println("-----------------------------------------------");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}