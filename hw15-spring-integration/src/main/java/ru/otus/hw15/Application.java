package ru.otus.hw15;

import lombok.val;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.otus.hw15.service.LoanService;

@SpringBootApplication
public class Application {

    public static void main(String[] args) throws InterruptedException {
        val ctx = SpringApplication.run(Application.class, args);
        ctx.getBean(LoanService.class).start();
    }
}
