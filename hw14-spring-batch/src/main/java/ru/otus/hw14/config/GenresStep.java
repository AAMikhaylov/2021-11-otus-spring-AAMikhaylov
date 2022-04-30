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
import ru.otus.hw14.models.h2.GenreH2;
import ru.otus.hw14.models.mongo.GenreMongo;

import javax.persistence.EntityManagerFactory;

@Configuration
public class GenresStep {
    private final static Logger logger = LoggerFactory.getLogger(GenresStep.class);
    @Autowired
    private EntityManagerFactory entityManagerFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private MongoTemplate mongoTemplate;

    private final int writePageSize;
    private final int readPageSize;

    public GenresStep(@Value("${application.write-page-size}") int writePageSize, @Value("${application.read-page-size}") int readPageSize) {
        this.writePageSize = writePageSize;
        this.readPageSize = readPageSize;
    }

    @Bean
    public Step genreStep() {
        return stepBuilderFactory.get("genresMigrationStep")
                .<GenreH2, GenreMongo>chunk(writePageSize)
                .reader(genreReader())
                .processor(genreProcessor())
                .writer(genreWriter())
                .listener(new ReadListener<>(logger))
                .listener(new WriteListener<>(logger))
                .build();
    }

    private JpaPagingItemReader<GenreH2> genreReader() {
        return new JpaPagingItemReaderBuilder<GenreH2>()
                .name("genreReader")
                .pageSize(readPageSize)
                .transacted(true)
                .entityManagerFactory(entityManagerFactory)
                .queryString("select g from GenreH2 g")
                .build();
    }

    private ItemProcessor<GenreH2, GenreMongo> genreProcessor() {
        return GenreMongo::fromH2;
    }

    private MongoItemWriter<GenreMongo> genreWriter() {
        return new MongoItemWriterBuilder<GenreMongo>()
                .collection("genres")
                .template(mongoTemplate)
                .build();
    }


}
