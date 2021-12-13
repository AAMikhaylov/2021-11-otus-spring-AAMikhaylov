package ru.otus.hw03;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import ru.otus.hw03.services.QuizService;


@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(Main.class, args);
        QuizService quizService = context.getBean(QuizService.class);
        quizService.start();


    }
}