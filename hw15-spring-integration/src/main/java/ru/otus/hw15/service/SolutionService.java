package ru.otus.hw15.service;

import ru.otus.hw15.domain.LoanAggregateSolution;

import java.util.List;

public interface SolutionService {
    LoanAggregateSolution aggregateSolutions(List<?> responseItems);
}
