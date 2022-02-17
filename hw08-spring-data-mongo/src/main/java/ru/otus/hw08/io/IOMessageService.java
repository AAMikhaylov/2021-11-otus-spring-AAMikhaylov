package ru.otus.hw08.io;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IOMessageService {
    private final IOService ioService;
    private final TranslateService translateService;

    public void write(String message) {
        ioService.write(message);
    }

    public void writeLocal(String messageId, Object... args) {
        ioService.write(translateService.getMessage(messageId, args));
    }

    public String read() {
        return ioService.read();
    }
}
