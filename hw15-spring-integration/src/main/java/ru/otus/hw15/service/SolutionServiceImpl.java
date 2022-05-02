package ru.otus.hw15.service;

import lombok.val;
import org.springframework.stereotype.Service;
import ru.otus.hw15.domain.Client;
import ru.otus.hw15.domain.LoanAggregateSolution;
import ru.otus.hw15.domain.LoanRequest;
import ru.otus.hw15.domain.LoanSolution;
import ru.otus.hw15.service.SolutionService;

import java.util.List;

@Service
public class SolutionServiceImpl implements SolutionService {

    @Override
    public LoanAggregateSolution aggregateSolutions(List<?> responseItems) {
        Client client = null;
        boolean canIssuer = true;
        StringBuilder reason = new StringBuilder();
        for (var solutionItem : responseItems) {
            if (solutionItem instanceof LoanRequest)
                client = ((LoanRequest) solutionItem).getClient();
            if (solutionItem instanceof LoanSolution) {
                val solution = (LoanSolution) solutionItem;
                if (!solution.isCanIssue()) {
                    canIssuer = false;
                    reason.append(String.format("%s: %s\n", solution.getDepartmentName(), solution.getReason()));
                }
            }
        }
        return new LoanAggregateSolution(client, canIssuer, reason.toString());
    }
}
