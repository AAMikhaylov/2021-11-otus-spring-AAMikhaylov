package ru.otus.hw08.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.otus.hw08.models.Author;

@Repository
public interface AuthorRepository extends MongoRepository<Author, String> {
}
