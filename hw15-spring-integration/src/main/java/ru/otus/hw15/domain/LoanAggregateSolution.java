package ru.otus.hw15.domain;

import lombok.Data;
import lombok.val;

@Data
public class LoanAggregateSolution {
    private final Client client;
    private final boolean canIssue;
    private final String reason;


    @Override
    public String toString() {
        val clientName = client == null ? "" : client.getFullName();
        val clientAge = client == null ? 0 : client.getAge();
        String solution = canIssue ? "Одобрена" : "Отклонена";
        var reasonStr = "";
        if (!canIssue)
            reasonStr = String.format("Причина:\n%s", reason);
        return String.format("\n----------------------------------------------------\n" +
                        "Заявка на кредит: %s\nКлиент: %s (%d лет)\n%s" +
                        "----------------------------------------------------\n",
                solution, clientName, clientAge, reasonStr);
    }
}
