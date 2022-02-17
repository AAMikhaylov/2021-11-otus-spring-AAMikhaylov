package ru.otus.hw08.io;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class TranslateBundleService implements TranslateService {
    private final MessageSource msgSource;
    private final Locale locale;

    public TranslateBundleService(MessageSource msgSource, @Value("${application.locale}") String localeTag) {
        this.msgSource = msgSource;
        this.locale = Locale.forLanguageTag(localeTag);
    }

    @Override
    public String getMessage(String messageId, Object... args) {
        return msgSource.getMessage(messageId, args, locale);

    }
}
