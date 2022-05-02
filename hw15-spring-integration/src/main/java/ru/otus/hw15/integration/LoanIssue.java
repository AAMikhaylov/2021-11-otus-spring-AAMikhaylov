package ru.otus.hw15.integration;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.messaging.Message;
import ru.otus.hw15.domain.LoanRequest;
import java.util.List;

@MessagingGateway
public interface LoanIssue {
    @Gateway(requestChannel = "loanRequestChannel", replyChannel = "loanSolutionChannel")
    List<?> process(Message<LoanRequest> message);


}
