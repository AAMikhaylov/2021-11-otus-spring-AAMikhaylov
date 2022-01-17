package ru.otus.hw04.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.otus.hw04.domain.Person;
import ru.otus.hw04.domain.Question;
import ru.otus.hw04.io.IOMessageService;

import java.util.List;

@Service
public class QuizServiceImpl implements QuizService {
    private final QuestionService questionService;
    private final IOMessageService msgService;
    private final int scorePass;


    public QuizServiceImpl(QuestionService questionService, IOMessageService msgService, @Value("${application.scorePass}") int scorePass) {
        this.questionService = questionService;
        this.msgService = msgService;
        this.scorePass = scorePass;
    }

    @Override
    public void start(Person person) {
        try {
            List<Question> questions = questionService.getAllQuestions();
            if (questions.size() > 0) {
                int score = 0;
                for (Question question : questions) {
                    msgService.write(question.getContent() + System.lineSeparator());
                    msgService.writeLocal("messages.enterAnswer");
                    String answer = msgService.read();
                    if (answer.equalsIgnoreCase(question.getAnswer())) {
                        score++;
                        msgService.writeLocal("messages.successAnswer");
                    } else {
                        msgService.writeLocal("messages.failAnswer");
                    }
                    msgService.write("-----------------------------------------------" + System.lineSeparator());
                }
                if (score >= scorePass)
                    msgService.writeLocal("messages.successResult", person.getName(), person.getSurname(), Integer.toString(score));
                else
                    msgService.writeLocal("messages.failResult", person.getName(), person.getSurname(), Integer.toString(score));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
