package ru.otus.hw14.writers;

import lombok.val;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import ru.otus.hw14.models.mongo.AuthorMongo;
import ru.otus.hw14.models.mongo.BookMongo;
import ru.otus.hw14.models.mongo.GenreMongo;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MongoBooksWriter extends MongoItemWriter<BookMongo> {
    private final MongoTemplate mongoTemplate;

    public MongoBooksWriter(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
        setTemplate(mongoTemplate);
    }

    @Override
    public void write(List<? extends BookMongo> books) throws Exception {
        val dependAuthors = getDependAuthors(books);
        val dependGenres = getDependGenres(books);
        books.forEach(book -> {
                    val bookGenres = book.getGenres().stream()
                            .map(g -> dependGenres.get(g.getH2Id()))
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());
                    book.setAuthor(dependAuthors.get(book.getAuthorH2Id()));
                    book.setGenres(bookGenres);
                }
        );
        super.write(books);
    }

    private Map<Long, AuthorMongo> getDependAuthors(List<? extends BookMongo> books) {
        val authorIds = books.stream()
                .map(BookMongo::getAuthorH2Id)
                .distinct()
                .collect(Collectors.toList());
        val query = new Query();
        query.addCriteria(Criteria.where("h2Id").in(authorIds));
        val authors = mongoTemplate.find(query, AuthorMongo.class);
        return authors.stream()
                .collect(Collectors.toMap(AuthorMongo::getH2Id, Function.identity()));
    }

    private Map<Long, GenreMongo> getDependGenres(List<? extends BookMongo> books) {
        val genreIds = books.stream()
                .flatMap(b -> b.getGenres().stream())
                .map(GenreMongo::getH2Id)
                .distinct()
                .collect(Collectors.toList());
        val query = new Query();
        query.addCriteria(Criteria.where("h2Id").in(genreIds));
        val genres = mongoTemplate.find(query, GenreMongo.class);
        return genres.stream()
                .collect(Collectors.toMap(GenreMongo::getH2Id, Function.identity()));
    }


}
