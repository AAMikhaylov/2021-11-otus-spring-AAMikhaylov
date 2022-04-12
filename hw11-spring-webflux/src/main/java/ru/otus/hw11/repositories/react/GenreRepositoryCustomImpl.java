package ru.otus.hw11.repositories.react;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import ru.otus.hw11.domain.Book;
import ru.otus.hw11.domain.Genre;

@Repository
@RequiredArgsConstructor
public class GenreRepositoryCustomImpl implements GenreRepositoryCustom {
    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<Genre> cascadeUpdate(Genre genre) {
        val booksQuery = Query.query(Criteria.where("genres._id").is(genre.getId()));
        val updateGenresQuery = new Update().set("genres.$.name", genre.getName());
        return mongoTemplate.updateMulti(booksQuery, updateGenresQuery, Book.class)
                .then(mongoTemplate.save(genre));
    }
}
