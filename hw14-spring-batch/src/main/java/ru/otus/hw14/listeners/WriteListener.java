package ru.otus.hw14.listeners;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.batch.core.ItemWriteListener;

import java.util.List;

@RequiredArgsConstructor
public class WriteListener<S> implements ItemWriteListener<S> {
    private final Logger logger;

    @Override
    public void beforeWrite(List<? extends S> list) {

    }

    @Override
    public void afterWrite(List<? extends S> list) {
        logger.info("Write complete: " + list.size() + " entities.");
    }

    @Override
    public void onWriteError(Exception e, List<? extends S> list) {
        logger.info("Writing error: " + e.getMessage());
    }
}
