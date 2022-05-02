package ru.otus.hw15.domain;

import lombok.Data;
import lombok.ToString;

@Data
public class LoanRequest {
    private final Client client;
    private final int requestSum;
    private final int salary;

    @Override
    public String toString() {
        return String.format("Клиент: %s, запрошено: %d, доход: %d",
                client.getFullName(),
                requestSum,
                salary);
    }
}
