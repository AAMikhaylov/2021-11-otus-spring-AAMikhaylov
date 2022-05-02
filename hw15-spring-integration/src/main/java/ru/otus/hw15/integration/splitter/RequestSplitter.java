package ru.otus.hw15.integration.splitter;
import lombok.val;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;
import ru.otus.hw15.domain.LoanRequest;

import java.util.*;

@Component
public class RequestSplitter {
    public Collection<Message<LoanRequest>> splitRequest(Message<LoanRequest> message) {
        val srcRequest=message.getPayload();
        val correlationId=Objects.requireNonNull(message.getHeaders().get("correlationId"));

        val messages=new ArrayList<Message<LoanRequest>>();
        val msgSecurity= new GenericMessage<>(
                new LoanRequest(srcRequest.getClient(), srcRequest.getRequestSum(), srcRequest.getSalary()),
                Map.of("correlationId", correlationId, "depId", "securityDep")
        );
        val msgUnderwriting= new GenericMessage<>(
                new LoanRequest(srcRequest.getClient(), srcRequest.getRequestSum(), srcRequest.getSalary()),
                Map.of("correlationId", correlationId, "depId", "underwritingDep")
        );
        messages.add(message);
        messages.add(msgSecurity);
        messages.add(msgUnderwriting);
        return messages;
    }

}
