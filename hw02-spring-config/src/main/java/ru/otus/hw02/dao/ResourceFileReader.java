package ru.otus.hw02.dao;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Repository
public class ResourceFileReader implements FileReader {
    private final String fileName;

    public ResourceFileReader(@Value("${testing.csvFile}") String fileName) {
        this.fileName = fileName;
    }


    @Override
    public String getFileData() throws IOException {
        try (InputStream inputStream = getClass().getResourceAsStream(fileName)) {
            if (inputStream == null)
                throw new IOException("Can't get resource: " + fileName);
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
