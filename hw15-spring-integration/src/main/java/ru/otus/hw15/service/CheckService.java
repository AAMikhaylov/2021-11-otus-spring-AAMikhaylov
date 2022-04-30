package ru.otus.hw15.service;

import ru.otus.hw15.domain.LoanRequest;
import ru.otus.hw15.domain.LoanSolution;

public interface CheckService {
    LoanSolution checkRequest(LoanRequest request);
}
