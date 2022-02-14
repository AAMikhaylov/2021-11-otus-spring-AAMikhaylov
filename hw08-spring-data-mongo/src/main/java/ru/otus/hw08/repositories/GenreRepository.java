package ru.otus.hw08.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.otus.hw08.models.Genre;

import java.util.List;

@Repository
public interface GenreRepository extends MongoRepository<Genre, String>, GenreRepositoryCustom {
    List<Genre> findByName(String name);
}
