package ru.otus.hw01.dao;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import ru.otus.hw01.domain.Question;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class QuestionDaoCSV implements QuestionDao {
    private final String csvFile;


    public QuestionDaoCSV(String csvFile) {
        this.csvFile = csvFile;
    }

    @Override
    public List<Question> getAll() {
        List<Question> result = new ArrayList<>();
        try (InputStream is = getClass().getResourceAsStream(csvFile)) {
            if (is == null)
                throw new IOException("Can't get resource: " + csvFile);
            CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                    .setHeader("id", "question", "answer")
                    .setSkipHeaderRecord(true)
                    .build();
            Iterable<CSVRecord> records = CSVParser.parse(is, StandardCharsets.UTF_8, csvFormat);
            for (CSVRecord record : records) {
                result.add(new Question(Integer.parseInt(record.get("id")), record.get("question"), record.get("answer")));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Question findById(int id) {
        try (InputStream is = getClass().getResourceAsStream(csvFile)) {
            if (is == null)
                throw new IOException("Can't get resource: " + csvFile);
            CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                    .setHeader("id", "question", "answer")
                    .setSkipHeaderRecord(true)
                    .build();
            Iterable<CSVRecord> records = CSVParser.parse(is, StandardCharsets.UTF_8, csvFormat);
            for (CSVRecord record : records) {
                int recId = Integer.parseInt(record.get("id"));
                if (id == recId)
                    return new Question(recId, record.get("question"), record.get("answer"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
