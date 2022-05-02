package ru.otus.hw15.service;

import lombok.val;
import org.springframework.stereotype.Service;
import ru.otus.hw15.domain.LoanRequest;
import ru.otus.hw15.domain.LoanSolution;

@Service
public class CheckSecurityService implements CheckService {
    private static final int MAX_AGE=60;
    @Override
    public LoanSolution checkRequest(LoanRequest request) {
        val client = request.getClient();
        if (client.getAge() > MAX_AGE)
            return new LoanSolution("Security department", false, "Отказ по возрасту.");
        else
            return new LoanSolution("Security department", true, "Одобрено.");
    }
}
