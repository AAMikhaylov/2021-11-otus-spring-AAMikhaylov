package ru.otus.hw04.providers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest(classes = {CsvFileProvider.class})
@DisplayName("Методы  класса CsvFileProvider должны:")
class CsvFileProviderTest {
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