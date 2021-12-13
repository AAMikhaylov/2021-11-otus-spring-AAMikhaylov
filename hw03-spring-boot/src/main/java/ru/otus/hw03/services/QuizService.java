package ru.otus.hw03.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.otus.hw03.domain.Question;
import ru.otus.hw03.io.IOMessageService;

import java.util.List;

@Service
public class QuizService {
    private final QuestionService questionService;
    private final IOMessageService msgService;
    private final int scorePass;


    public QuizService(QuestionService questionService, IOMessageService msgService, @Value("${application.scorePass}") int scorePass) {
        this.questionService = questionService;
        this.msgService = msgService;
        this.scorePass = scorePass;
    }

    public void start() {
        msgService.writeLocal("messages.enterName");
        String name = msgService.read();
        msgService.writeLocal("messages.enterSurname");
        String surname = msgService.read();
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
                    msgService.writeLocal("messages.successResult", name, surname, Integer.toString(score));
                else
                    msgService.writeLocal("messages.failResult", name, surname, Integer.toString(score));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
