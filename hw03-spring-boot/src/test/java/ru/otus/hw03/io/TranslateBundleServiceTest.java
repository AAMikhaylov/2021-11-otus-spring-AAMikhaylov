package ru.otus.hw03.io;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@DisplayName("Метод getMessage класса TranslateBundleService должен:")
class TranslateBundleServiceTest {
    @Configuration
    @EnableAutoConfiguration
    public static class TestContextConfig {
        @Bean
        TranslateBundleService translateBundleService(MessageSource msgSource, @Value("${application.locale}") String localeTag) {
            return new TranslateBundleService(msgSource, localeTag);

        }
    }

    @Autowired
    TranslateBundleService translateService;

    @Test
    @DisplayName("Формировать сообщение с параметрами на указанном в application.yml языке")
    void testGetMessage() {
        String actualMessage = translateService.getMessage("messages.failResult", "Name", "Surname", "SCORE");
        assertThat(actualMessage)
                .contains("SCORE")
                .contains("Name")
                .contains("SCORE")
                .hasSizeGreaterThan(20);
    }
}