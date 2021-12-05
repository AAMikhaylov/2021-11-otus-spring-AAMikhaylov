package ru.otus.hw02.dao;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.otus.hw02.domain.Question;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QuestionDaoCSVTest {
    private static QuestionDaoCSV questionDaoCSV;


    @BeforeAll
    static void BeforeAll() throws IOException {
        String csvData="id,question,answer\r\n" +
                "3,\"test question 1\",33\r\n"+
                "2,\"test question 5\",1";
        ResourceFileReader resourceFileReader = Mockito.mock(ResourceFileReader.class);
        Mockito.when(resourceFileReader.getFileData()).thenReturn(csvData);
        questionDaoCSV = new QuestionDaoCSV(resourceFileReader);
    }

    @Test
    void getAll() {
        List<Question> questionList=questionDaoCSV.getAll();
        assertEquals(questionList.size(),2);
        Question question=questionList.get(0);
        assertEquals(question.getId(),3);
        assertEquals(question.getContent(),"test question 1");
        assertEquals(question.getAnswer(),"33");
    }

    @Test
    void findById() {
        Question question = questionDaoCSV.findById(2);
        assertEquals(question.getContent(),"test question 5");
        assertEquals(question.getAnswer(),"1");


    }
}