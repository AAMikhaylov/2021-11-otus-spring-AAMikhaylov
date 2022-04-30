package ru.otus.hw14.listeners;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.batch.core.ItemReadListener;

@RequiredArgsConstructor
public class ReadListener<T> implements ItemReadListener<T> {
    private final Logger logger;

    @Override
    public void beforeRead() {
    }

    @Override
    public void afterRead(T t) {
        logger.info("Read: "+t.toString());

    }

    @Override
    public void onReadError(Exception e) {
        logger.info("Reading error: " + e.getMessage());
    }
}
