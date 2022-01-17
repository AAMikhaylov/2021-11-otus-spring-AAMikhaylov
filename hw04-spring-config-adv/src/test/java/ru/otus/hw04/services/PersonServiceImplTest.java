package ru.otus.hw04.services;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.otus.hw04.domain.Person;
import ru.otus.hw04.io.IOMessageService;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {PersonServiceImpl.class})
@DisplayName("Класс PersonServiceImpl должен:")
class PersonServiceImplTest {
    @MockBean
    IOMessageService ioMessageService;
    @Autowired
    PersonService personService;
    private final static String EXPECTED_PERSON_NAME = "expectedName";
    private final static String EXPECTED_PERSON_SURNAME = "expectedSurname";
    private final static Person EXPECTED_PERSON = new Person(EXPECTED_PERSON_NAME, EXPECTED_PERSON_SURNAME);
    private final static List<String> EXPECTED_MESSAGES = List.of("messages.enterName", "messages.enterSurname");
    private final List<String> printedStrings = new ArrayList<>();

    @BeforeEach
    void setUp() {
        doAnswer(invocationOnMock -> {
            printedStrings.add(invocationOnMock.getArgument(0).toString().trim());
            return null;
        }).when(ioMessageService).writeLocal(anyString());
        doReturn(EXPECTED_PERSON_NAME, EXPECTED_PERSON_SURNAME).when(ioMessageService).read();
    }

    @AfterEach
    void tearDown() {
        reset(ioMessageService);
        printedStrings.clear();
    }

    @Test
    @DisplayName("Регистрировать нового person с ожидаемыми именем и фамилией")
    void registerPersonTest() {
        Person person = personService.registerPerson();
        assertThat(person)
                .usingRecursiveComparison()
                .isEqualTo(EXPECTED_PERSON);
    }

    @Test
    @DisplayName("Выводить ожидаемые сообщения при регистрации нового person")
    void registerPersonMessagesTest() {
        personService.registerPerson();
        assertThat(printedStrings).isEqualTo(EXPECTED_MESSAGES);
    }
}