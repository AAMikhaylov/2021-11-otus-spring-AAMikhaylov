package ru.otus.hw14.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.otus.hw14.writers.MongoBooksWriter;
import ru.otus.hw14.listeners.ReadListener;
import ru.otus.hw14.listeners.WriteListener;
import ru.otus.hw14.models.h2.BookH2;
import ru.otus.hw14.models.mongo.BookMongo;

import javax.persistence.EntityManagerFactory;

@Configuration
public class BooksStep {
    private final static Logger logger = LoggerFactory.getLogger(BooksStep.class);
    @Autowired
    private EntityManagerFactory entityManagerFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private MongoTemplate mongoTemplate;

    private final int writePageSize;
    private final int readPageSize;

    public BooksStep(@Value("${application.write-page-size}") int writePageSize, @Value("${application.read-page-size}") int readPageSize) {
        this.writePageSize = writePageSize;
        this.readPageSize = readPageSize;
    }

    @Bean
    public Step bookStep() {
        return stepBuilderFactory.get("BooksMigrationStep")
                .<BookH2, BookMongo>chunk(writePageSize)
                .reader(bookReader())
                .processor(bookProcessor())
                .writer(new MongoBooksWriter(mongoTemplate))
                .listener(new ReadListener<>(logger))
                .listener(new WriteListener<>(logger))
                .build();

    }

    private JpaPagingItemReader<BookH2> bookReader() {
        return new JpaPagingItemReaderBuilder<BookH2>()
                .name("bookReader")
                .pageSize(readPageSize)
                .transacted(true)
                .entityManagerFactory(entityManagerFactory)
                .queryString("select b from BookH2 b")
                .build();
    }

    private ItemProcessor<BookH2, BookMongo> bookProcessor() {
        return BookMongo::fromH2;
    }
}
