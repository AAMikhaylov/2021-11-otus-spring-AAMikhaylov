package ru.otus.hw04;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.shell.CommandNotCurrentlyAvailable;
import org.springframework.shell.Shell;
import ru.otus.hw04.domain.Person;
import ru.otus.hw04.io.IOMessageService;
import ru.otus.hw04.services.PersonService;
import ru.otus.hw04.services.QuizService;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {ShellCommands.class})
@EnableAutoConfiguration
@DisplayName("Класс команд ShellCommands должен:")
class ShellCommandsTest {
    @Autowired
    private Shell shell;
    @MockBean
    IOMessageService ioMessageService;
    @MockBean
    PersonService personService;
    @MockBean
    QuizService quizService;

    private final static String REGISTER_COMMAND = "reg";
    private final static String REGISTER_SHORT_COMMAND = "r";
    private final static String START_QUIZ_SHORT_COMMAND = "sq";
    private final static String START_QUIZ_COMMAND = "start-quiz";

    private final static String EXPECTED_PERSON_NAME = "ExpectedName";
    private final static String EXPECTED_PERSON_SURNAME = "ExpectedSurname";
    private final static String EXPECTED_REGISTER_MESSAGE_PATTERN = "Welcome: %s %s!";


    @BeforeEach
    void setUp() {
        when(personService.registerPerson()).thenReturn(new Person(EXPECTED_PERSON_NAME, EXPECTED_PERSON_SURNAME));
        when(ioMessageService.read()).thenReturn(EXPECTED_PERSON_NAME, EXPECTED_PERSON_SURNAME);
    }

    @Test
    @DisplayName("Выводить ожидаемое сообщение при регистрации пользователя (для всех форм команд)")
    void registerPersonMessageTest() {
        var message = (String) shell.evaluate(() -> REGISTER_SHORT_COMMAND);
        assertThat(message).isEqualTo(String.format(EXPECTED_REGISTER_MESSAGE_PATTERN, EXPECTED_PERSON_NAME, EXPECTED_PERSON_SURNAME));
        message = (String) shell.evaluate(() -> REGISTER_COMMAND);
        assertThat(message).isEqualTo(String.format(EXPECTED_REGISTER_MESSAGE_PATTERN, EXPECTED_PERSON_NAME, EXPECTED_PERSON_SURNAME));
    }

    @Test
    @DisplayName("Возвращать CommandNotCurrentlyAvailable при запуске теста без регистрации пользователя (для всех форм команд)")
    void quizStartWithoutRegistrationTest() {
        Object result = shell.evaluate(() -> START_QUIZ_COMMAND);
        assertThat(result).isInstanceOf(CommandNotCurrentlyAvailable.class);
        result = shell.evaluate(() -> START_QUIZ_SHORT_COMMAND);
        assertThat(result).isInstanceOf(CommandNotCurrentlyAvailable.class);

    }

    @Test
    @DisplayName("Запустить метод класса QuizService для запуска тестирования (для всех форм команд)")
    void quizStartTest() {
        shell.evaluate(() -> REGISTER_SHORT_COMMAND);
        shell.evaluate(() -> START_QUIZ_COMMAND);
        shell.evaluate(() -> START_QUIZ_SHORT_COMMAND);
        verify(quizService, times(2)).start(any());
    }
}