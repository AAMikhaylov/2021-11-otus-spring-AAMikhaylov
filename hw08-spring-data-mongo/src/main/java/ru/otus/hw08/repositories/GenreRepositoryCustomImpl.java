package ru.otus.hw08.repositories;

import lombok.RequiredArgsConstructor;
import lombok.val;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import ru.otus.hw08.models.Book;
import ru.otus.hw08.models.Genre;

@Repository
@RequiredArgsConstructor
public class GenreRepositoryCustomImpl implements GenreRepositoryCustom {
    private final MongoTemplate mongoTemplate;

    @Override
    public Genre cascadeUpdate(Genre genre) {
        val booksQuery = Query.query(Criteria.where("genres._id").is(genre.getId()));
//        val updateGenresQuery=new Update().set("genres.$[element].name",genre.getName()).filterArray(Criteria.where("element._id").is(genre.getId()));
        val updateGenresQuery = new Update().set("genres.$.name", genre.getName());
        val updatedGenre = mongoTemplate.save(genre);
        mongoTemplate.updateMulti(booksQuery, updateGenresQuery, Book.class);
        return updatedGenre;
    }
}
