package ru.otus.hw04.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw04.providers.LocaleResourceProvider;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class ResourceFileReader implements FileReader {
    private final LocaleResourceProvider resourceProvider;

    private String getFileData(String fname) throws IOException {
        if (fname.isEmpty())
            throw new IOException("Can't get resource. Empty file name.");
        try (InputStream inputStream = getClass().getResourceAsStream(fname)) {
            if (inputStream == null)
                throw new IOException("Can't get resource: " + fname);
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    @Override
    public String getFileData() throws IOException {
        try {
            return getFileData(resourceProvider.getLocalFileName());
        } catch (IOException e) {
            return getFileData(resourceProvider.getFileName());
        }
    }
}
