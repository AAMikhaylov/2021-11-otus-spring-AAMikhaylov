package ru.otus.hw14.config;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@RequiredArgsConstructor
@Configuration
public class PrepareStep {
    private final StepBuilderFactory stepBuilderFactory;
    private final MongoTemplate mongoTemplate;

    public Tasklet clearDbTasklet() {
        return (stepContribution, chunkContext) -> {
            mongoTemplate.getDb().drop();
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Step cleanMongoDbStep() {
        return this.stepBuilderFactory.get("cleanMongoDbStep")
                .tasklet(clearDbTasklet())
                .build();
    }

}
