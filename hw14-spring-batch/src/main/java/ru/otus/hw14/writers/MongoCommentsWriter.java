package ru.otus.hw14.writers;

import lombok.val;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.data.mongodb.core.MongoTemplate;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import ru.otus.hw14.models.mongo.BookMongo;
import ru.otus.hw14.models.mongo.CommentMongo;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MongoCommentsWriter extends MongoItemWriter<CommentMongo> {
    private final MongoTemplate mongoTemplate;

    public MongoCommentsWriter(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
        setTemplate(mongoTemplate);
    }

    @Override
    public void write(List<? extends CommentMongo> comments) throws Exception {
        val dependBooks = getDependBooks(comments);
        comments.forEach(c -> c.setBook(dependBooks.get(c.getBookH2Id())));
        super.write(comments);
    }

    private Map<Long, BookMongo> getDependBooks(List<? extends CommentMongo> comments) {
        val bookIds = comments.stream()
                .map(CommentMongo::getBookH2Id)
                .distinct()
                .collect(Collectors.toList());
        val query = new Query();
        query.addCriteria(Criteria.where("h2Id").in(bookIds));
        val books = mongoTemplate.find(query, BookMongo.class);
        return books.stream()
                .collect(Collectors.toMap(BookMongo::getH2Id, Function.identity()));
    }


}
