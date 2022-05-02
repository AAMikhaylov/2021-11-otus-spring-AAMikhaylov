package ru.otus.hw15.domain;

import lombok.Data;

@Data
public class LoanSolution {
    private final String departmentName;
    private final boolean canIssue;
    private final String reason;
}
