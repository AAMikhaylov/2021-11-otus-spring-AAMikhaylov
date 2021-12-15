package ru.otus.hw03.providers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


@SpringBootTest
@DisplayName("Методы  класса CsvFileProvider должны:")
class CsvFileProviderTest {
    @Configuration
    public static class TestContextConfig {
        @Bean
        CsvFileProvider csvFileProvider(@Value("${application.csvFile}") String csvFileName, @Value("${application.locale}") String localeTag) {
            return new CsvFileProvider(csvFileName, localeTag);
        }
    }
@Autowired
CsvFileProvider csvFileProvider;

    @Test
    @DisplayName("Возвращать имя файла с данными по умолчанию")
    void testGetFileName() {
        assertThat(csvFileProvider.getFileName()).isEqualTo("/i18n/questions_test.csv");
    }

    @Test
    @DisplayName("Возвращать имя файла с локализованными данными")
    void testGetLocalFileName() {
        assertThat(csvFileProvider.getLocalFileName()).isEqualTo("/i18n/questions_test_ru_RU.csv");
    }
}