package ru.otus.hw03.io;

import org.springframework.stereotype.Service;

@Service
public class IOMessageService {
    private final IOService ioService;
    private final TranslateService translateService;

    public IOMessageService(IOService ioService, TranslateService translateService) {
        this.ioService = ioService;
        this.translateService = translateService;
    }

    public void write(String message) {
        ioService.write(message);
    }

    public void writeLocal(String messageId, String... args) {
        ioService.write(translateService.getMessage(messageId, args));
    }

    public String read() {
        return ioService.read();
    }
}
