package ru.otus.hw04.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw04.domain.Person;
import ru.otus.hw04.io.IOMessageService;

@Component
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {
    private final IOMessageService msgService;

    @Override
    public Person registerPerson() {
        msgService.writeLocal("messages.enterName");
        var name = msgService.read();
        if (name.isEmpty())
            name = "unknownName";
        msgService.writeLocal("messages.enterSurname");
        var surname = msgService.read();
        if (surname.isEmpty())
            surname = "unknownSurname";
        return new Person(name, surname);
    }
}
