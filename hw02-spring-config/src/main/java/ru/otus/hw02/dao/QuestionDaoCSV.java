package ru.otus.hw02.dao;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Repository;
import ru.otus.hw02.domain.Question;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class QuestionDaoCSV implements QuestionDao {
    private final FileReader fileReader;

    public QuestionDaoCSV(FileReader fileReader) {
        this.fileReader = fileReader;
    }

    @Override
    public List<Question> getAll() {
        List<Question> result = new ArrayList<>();
        try {
            CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                    .setHeader("id", "question", "answer")
                    .setSkipHeaderRecord(true)
                    .build();
            String csvData = fileReader.getFileData();
            Iterable<CSVRecord> records = CSVParser.parse(csvData, csvFormat);
            for (CSVRecord record : records) {
                result.add(new Question(Integer.parseInt(record.get("id")), record.get("question"), record.get("answer")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Question findById(int id) {
        try {
            CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                    .setHeader("id", "question", "answer")
                    .setSkipHeaderRecord(true)
                    .build();
            String csvData = fileReader.getFileData();
            Iterable<CSVRecord> records = CSVParser.parse(csvData, csvFormat);
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
