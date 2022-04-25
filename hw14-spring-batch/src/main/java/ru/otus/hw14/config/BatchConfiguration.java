package ru.otus.hw14.config;

import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableBatchProcessing
public class BatchConfiguration {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private Step cleanMongoDbStep;
    @Autowired
    private Step authorStep;
    @Autowired
    private Step genreStep;
    @Autowired
    private Step bookStep;
    @Autowired
    private Step commentStep;
    @Autowired
    private Step clearMongoTempStep;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Bean
    public Job migrationToMongoDataJob() {
        return jobBuilderFactory.get("MigrationH2ToMongo")
                .incrementer(new RunIdIncrementer())
                .flow(cleanMongoDbStep)
                .next(authorStep)
                .next(genreStep)
                .next(bookStep)
                .next(commentStep)
                .next(clearMongoTempStep)
                .end()
                .listener(new JobExecutionListener() {
                    @Override
                    public void beforeJob(@NonNull JobExecution jobExecution) {
                        logger.info("Start MigrationH2ToMongo job");
                    }

                    @Override
                    public void afterJob(@NonNull JobExecution jobExecution) {
                        logger.info("MigrationH2ToMongo job complete");
                    }
                })
                .build();
    }
}
