package ru.otus.hw04;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import ru.otus.hw04.domain.Person;
import ru.otus.hw04.services.PersonService;
import ru.otus.hw04.services.QuizService;


@ShellComponent
@RequiredArgsConstructor
public class ShellCommands {
    private final PersonService personService;
    private final QuizService quizService;
    private Person person;

    @ShellMethod(value = "New user registration.", key = {"r", "reg"})
    String registerPerson() {
        person = personService.registerPerson();
        return "Welcome: " + person + "!";
    }

    @ShellMethod(value = "Start quiz.", key = {"sq", "start-quiz"})
    @ShellMethodAvailability(value = "checkRegistration")
    void quizStart() {
        quizService.start(person);
    }

    Availability checkRegistration() {
        return person == null ? Availability.unavailable("you must register first (command \"r\" or \"reg\")") : Availability.available();
    }
}
