package ru.otus.hw05.exceptions;

import lombok.RequiredArgsConstructor;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;
import ru.otus.hw05.io.IOMessageService;

@Component
@RequiredArgsConstructor
public class ExceptionHandler {
    private final IOMessageService ioMessageService;

    public void handle(Throwable t) {
        try {
            ioMessageService.writeLocal(t.getMessage());
        } catch (NoSuchMessageException e) {
            ioMessageService.write(t.getMessage());
        } catch (Exception e) {
            ioMessageService.write(e.getMessage());
        }
    }
}
