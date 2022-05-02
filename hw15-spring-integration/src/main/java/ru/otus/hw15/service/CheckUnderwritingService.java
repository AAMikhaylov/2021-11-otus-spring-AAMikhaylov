package ru.otus.hw15.service;

import org.springframework.stereotype.Service;
import ru.otus.hw15.domain.LoanRequest;
import ru.otus.hw15.domain.LoanSolution;
import ru.otus.hw15.service.CheckService;

@Service
public class CheckUnderwritingService implements CheckService {
    private final static int SALARY_FACTOR_PRC = 80;
    @Override
    public LoanSolution checkRequest(LoanRequest request) {
        if (request.getRequestSum() * 100 / request.getSalary() > SALARY_FACTOR_PRC)
            return new LoanSolution("Underwriting department", false, "Недостаточный доход для выдачи кредита.");
        else
            return new LoanSolution("Underwriting department", true, "Одобрено.");
    }
}
