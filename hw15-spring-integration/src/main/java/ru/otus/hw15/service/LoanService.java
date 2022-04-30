package ru.otus.hw15.service;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;
import ru.otus.hw15.domain.Client;
import ru.otus.hw15.domain.LoanRequest;
import ru.otus.hw15.integration.LoanIssue;

import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ForkJoinPool;

@Service
@RequiredArgsConstructor
public class LoanService {
    private static final int REQUEST_DELAY_MILLS = 5000;
    private final LoanIssue loanIssue;
    private final SolutionService solutionService;

    public void start() throws InterruptedException {
        ForkJoinPool pool = ForkJoinPool.commonPool();
        while (true) {
            Thread.sleep(REQUEST_DELAY_MILLS);
            pool.execute(() -> {
                val newRequest = getRandomRequest();
                System.out.printf("Новая заявка на кредит:\n%s\n", newRequest);
                Message<LoanRequest> msg = new GenericMessage<>(newRequest,
                        Map.of("correlationId", UUID.randomUUID(), "depId", "loanDep")
                );
                val solution = solutionService.aggregateSolutions(loanIssue.process(msg));
                System.out.println(solution);
            });
        }
    }

    private LoanRequest getRandomRequest() {
        val clientName = "Client" + getRandomInt(10, 1000);
        val age = getRandomInt(40, 90);
        val requestSum = getRandomInt(30000, 90000);
        val salary = getRandomInt(50000, 100000);
        return new LoanRequest(new Client(clientName, age), requestSum, salary);
    }

    private int getRandomInt(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min) + min;
    }

}
