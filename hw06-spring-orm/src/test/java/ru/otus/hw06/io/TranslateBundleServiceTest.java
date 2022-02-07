package ru.otus.hw06.io;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import ru.otus.hw06.io.TranslateBundleService;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = {TranslateBundleService.class})
@EnableAutoConfiguration
@DisplayName("Метод getMessage класса TranslateBundleService должен:")
class TranslateBundleServiceTest {
    @Autowired
    TranslateBundleService translateService;

    @Test
    @DisplayName("Формировать сообщение с параметрами на указанном в application.yml языке")
    void testGetMessage() {
        String actualMessage = translateService.getMessage("messages.book.update.titleBook", "Test title");
        assertThat(actualMessage)
                .contains("Test title")
                .contains("Book title")
                .contains("Enter new book title")
                .hasSizeGreaterThan(20);
    }
}