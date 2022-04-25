package ru.otus.hw14.config;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import ru.otus.hw14.models.mongo.AuthorMongo;
import ru.otus.hw14.models.mongo.BookMongo;
import ru.otus.hw14.models.mongo.GenreMongo;

@RequiredArgsConstructor
@Configuration
public class FinallyStep {
    private final StepBuilderFactory stepBuilderFactory;
    private final MongoTemplate mongoTemplate;

    public Tasklet clearMongoTempTasklet() {
        return (stepContribution, chunkContext) -> {
            val query= new Query();
            val updateQuery = new Update().unset("h2Id");
            mongoTemplate.updateMulti(query, updateQuery, AuthorMongo.class);
            mongoTemplate.updateMulti(query, updateQuery, GenreMongo.class);
            mongoTemplate.updateMulti(query, updateQuery, BookMongo.class);
            query.addCriteria(Criteria.where("genres").exists(true));
            updateQuery.unset("genres.$[].h2Id");
            mongoTemplate.updateMulti(query, updateQuery, BookMongo.class);
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Step clearMongoTempStep() {
        return this.stepBuilderFactory.get("clearMongoTempStep")
                .tasklet(clearMongoTempTasklet())
                .build();
    }

}

