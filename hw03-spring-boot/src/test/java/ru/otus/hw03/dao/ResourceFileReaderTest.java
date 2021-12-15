package ru.otus.hw03.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.otus.hw03.providers.LocaleResourceProvider;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
@DisplayName("Метод getFileData класса ResourceFileReader должен:")
class ResourceFileReaderTest {
    @Configuration
    public static class TestContextConfig {
        @MockBean
        LocaleResourceProvider resourceProvider;

        @Bean
        ResourceFileReader resourceFileReader(LocaleResourceProvider resourceProvider) {
            return new ResourceFileReader(resourceProvider);
        }
    }

    @Autowired
    private ResourceFileReader rsFileReader;
    @Autowired
    private LocaleResourceProvider resourceProvider;
    private final static String EXPECTED_STRING = "id,question,answer" + System.lineSeparator() +
            "1,\"Question 1\",\"Answer 1\"" + System.lineSeparator() +
            "2,\"Question 2\",\"Answer 2\"" + System.lineSeparator() +
            "3,\"Question 3\",\"Answer 3\"" + System.lineSeparator() +
            "4,\"Question 4\",\"Answer 4\"";

    private final static String EXPECTED_LOCAL_STRING = "id,question,answer" + System.lineSeparator() +
            "1,\"Вопрос 1\",\"Ответ 1\"" + System.lineSeparator() +
            "2,\"Вопрос 2\",\"Ответ 2\"" + System.lineSeparator() +
            "3,\"Вопрос 3\",\"Ответ 3\"" + System.lineSeparator() +
            "4,\"Вопрос 4\",\"Ответ 4\"";


    @Test
    @DisplayName("Считывать данные из локализованного файла")
    void testGetFileLocalData() throws IOException {
        when(resourceProvider.getFileName()).thenReturn("/i18n/questions_test.csv");
        when(resourceProvider.getLocalFileName()).thenReturn("/i18n/questions_test_ru_RU.csv");
        String actualString = rsFileReader.getFileData();
        actualString = actualString.replace("\r", "").replace("\n", System.lineSeparator());
        assertThat(actualString).isEqualTo(EXPECTED_LOCAL_STRING);
    }

    @Test
    @DisplayName("Считывать данные из файла по умолчанию, если локализованного нет")
    void testGetFileData() throws IOException {
        when(resourceProvider.getFileName()).thenReturn("/i18n/questions_test.csv");
        when(resourceProvider.getLocalFileName()).thenReturn("/i18n/fileNotPresent.csv");
        String actualString = rsFileReader.getFileData();
        actualString = actualString.replace("\r", "").replace("\n", System.lineSeparator());
        assertThat(actualString).isEqualTo(EXPECTED_STRING);
    }

    @Test
    @DisplayName("Бросать исключение, если файл по умолчанию также отсутствует")
    void testGetFileDataException() {
        when(resourceProvider.getFileName()).thenReturn("/i18n/fileAbsent.csv");
        when(resourceProvider.getLocalFileName()).thenReturn("/i18n/fileNotPresent.csv");
        assertThatThrownBy(rsFileReader::getFileData)
                .isInstanceOf(IOException.class)
                .hasMessageContaining("Can't get resource");
    }


}