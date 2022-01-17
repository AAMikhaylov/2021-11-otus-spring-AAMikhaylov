package ru.otus.hw04.providers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class CsvFileProvider implements LocaleResourceProvider {

    private final Locale locale;
    private final String csvFileName;

    public CsvFileProvider(@Value("${application.csvFile}") String csvFileName, @Value("${application.locale}") String localeTag) {
        this.csvFileName = csvFileName;
        this.locale = Locale.forLanguageTag(localeTag);
    }

    @Override
    public String getFileName() {
        return csvFileName;
    }

    @Override
    public String getLocalFileName() {
        int dotPos = (csvFileName.contains(".") ? csvFileName.lastIndexOf(".") : csvFileName.length());
        return csvFileName.substring(0, dotPos) + "_" + locale + csvFileName.substring(dotPos);
    }

}
