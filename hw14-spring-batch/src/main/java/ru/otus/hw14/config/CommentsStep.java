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
import ru.otus.hw14.listeners.ReadListener;
import ru.otus.hw14.listeners.WriteListener;
import ru.otus.hw14.models.h2.CommentH2;
import ru.otus.hw14.models.mongo.CommentMongo;
import ru.otus.hw14.writers.MongoCommentsWriter;

import javax.persistence.EntityManagerFactory;

@Configuration
public class CommentsStep {
    private final static Logger logger = LoggerFactory.getLogger(CommentsStep.class);
    @Autowired
    private EntityManagerFactory entityManagerFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private MongoTemplate mongoTemplate;

    private final int writePageSize;
    private final int readPageSize;

    public CommentsStep(@Value("${application.write-page-size}") int writePageSize, @Value("${application.read-page-size}") int readPageSize) {
        this.writePageSize = writePageSize;
        this.readPageSize = readPageSize;
    }

    @Bean
    public Step commentStep() {
        return stepBuilderFactory.get("CommentsMigrationStep")
                .<CommentH2, CommentMongo>chunk(writePageSize)
                .reader(commentReader())
                .processor(commentProcessor())
                .writer(new MongoCommentsWriter(mongoTemplate))
                .listener(new ReadListener<>(logger))
                .listener(new WriteListener<>(logger))
                .build();
    }

    private JpaPagingItemReader<CommentH2> commentReader() {
        return new JpaPagingItemReaderBuilder<CommentH2>()
                .name("commentReader")
                .pageSize(readPageSize)
                .transacted(true)
                .entityManagerFactory(entityManagerFactory)
                .queryString("select c from CommentH2 c")
                .build();
    }

    private ItemProcessor<CommentH2, CommentMongo> commentProcessor() {
        return CommentMongo::fromH2;
    }
}
