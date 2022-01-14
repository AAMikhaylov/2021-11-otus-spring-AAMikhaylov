package ru.otus.hw04.dao;

import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Repository;
import ru.otus.hw04.domain.Question;
import ru.otus.hw04.exceptions.QuestionDaoException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class QuestionDaoCSV implements QuestionDao {
    private final FileReader fileReader;

    @Override
    public List<Question> getAll() throws QuestionDaoException {
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
            throw new QuestionDaoException("Can't get questions.", e);
        }
        return result;
    }

    @Override
    public Question findById(int id) throws QuestionDaoException {
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
            throw new QuestionDaoException("Can't get question by id.", e);
        }
        return null;
    }
}
