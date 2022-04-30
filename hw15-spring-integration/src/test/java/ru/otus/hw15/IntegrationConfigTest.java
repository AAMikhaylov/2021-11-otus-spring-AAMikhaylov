package ru.otus.hw15;

import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.support.GenericMessage;
import ru.otus.hw15.domain.Client;
import ru.otus.hw15.domain.LoanRequest;
import ru.otus.hw15.domain.LoanSolution;
import ru.otus.hw15.integration.LoanIssue;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("Конфигурация Spring Integration должна:")
class IntegrationConfigTest {
    @Autowired
    LoanIssue loanIssue;

    private final static Client SUCCESS_CLIENT = new Client("testClientSuccess", 20);
    private final static LoanRequest SUCCESS_LOAN_REQUEST = new LoanRequest(SUCCESS_CLIENT, 1000, 20000);
    private final static List<?> EXPECTED_SUCCESS_SOLUTIONS = List.of(
            SUCCESS_LOAN_REQUEST,
            new LoanSolution("Security department", true, "Одобрено."),
            new LoanSolution("Underwriting department", true, "Одобрено.")
    );

    private final static Client FAIL_CLIENT = new Client("testClientFail", 82);
    private final static LoanRequest FAIL_LOAN_REQUEST = new LoanRequest(FAIL_CLIENT, 19000, 20000);
    private final static List<?> EXPECTED_FAIL_SOLUTIONS = List.of(
            FAIL_LOAN_REQUEST,
            new LoanSolution("Security department", false, "Отказ по возрасту."),
            new LoanSolution("Underwriting department", false, "Недостаточный доход для выдачи кредита.")
    );
    @Test
    @DisplayName("формировать набор ожидаемых сообщений в случае положительного решения о выдачи кредита")
    void testSuccessIssue() {
        val msg = new GenericMessage<>(SUCCESS_LOAN_REQUEST,
                Map.of("correlationId", UUID.randomUUID(), "depId", "loanDep")
        );
        val actualSolutions = loanIssue.process(msg);
        assertThat(actualSolutions)
                .usingRecursiveComparison()
                .isEqualTo(EXPECTED_SUCCESS_SOLUTIONS);
    }

    @Test
    @DisplayName("формировать набор ожидаемых сообщений в случае отрицательного решения о выдачи кредита")
    void testFailIssue() {
        val msg = new GenericMessage<>(FAIL_LOAN_REQUEST,
                Map.of("correlationId", UUID.randomUUID(), "depId", "loanDep")
        );
        val actualSolutions = loanIssue.process(msg);
        assertThat(actualSolutions)
                .usingRecursiveComparison()
                .isEqualTo(EXPECTED_FAIL_SOLUTIONS);
    }
}