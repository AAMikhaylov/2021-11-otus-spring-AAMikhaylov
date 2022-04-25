package ru.otus.hw14.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.data.builder.MongoItemWriterBuilder;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.otus.hw14.listeners.ReadListener;
import ru.otus.hw14.listeners.WriteListener;
import ru.otus.hw14.models.h2.AuthorH2;
import ru.otus.hw14.models.mongo.AuthorMongo;

import javax.persistence.EntityManagerFactory;

@Configuration
public class AuthorsStep {
    private final static Logger logger = LoggerFactory.getLogger(AuthorsStep.class);
    @Autowired
    private EntityManagerFactory entityManagerFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private MongoTemplate mongoTemplate;
    private final int writePageSize;
    private final int readPageSize;

    public AuthorsStep(@Value("${application.write-page-size}") int writePageSize, @Value("${application.read-page-size}") int readPageSize) {
        this.writePageSize = writePageSize;
        this.readPageSize = readPageSize;
    }

    @Bean
    public Step authorStep() {
        return stepBuilderFactory.get("AuthorsMigrationStep")
                .<AuthorH2, AuthorMongo>chunk(writePageSize)
                .reader(authorReader())
                .processor(authorProcessor())
                .writer(authorWriter())
                .listener(new ReadListener<>(logger))
                .listener(new WriteListener<>(logger))
                .build();
    }

    private JpaPagingItemReader<AuthorH2> authorReader() {
        return new JpaPagingItemReaderBuilder<AuthorH2>()
                .name("authorReader")
                .pageSize(readPageSize)
                .transacted(true)
                .entityManagerFactory(entityManagerFactory)
                .queryString("select a from AuthorH2 a")
                .build();
    }

    private ItemProcessor<AuthorH2, AuthorMongo> authorProcessor() {
        return AuthorMongo::fromH2;
    }

    private MongoItemWriter<AuthorMongo> authorWriter() {
        return new MongoItemWriterBuilder<AuthorMongo>()
                .collection("authors")
                .template(mongoTemplate)
                .build();
    }
}
