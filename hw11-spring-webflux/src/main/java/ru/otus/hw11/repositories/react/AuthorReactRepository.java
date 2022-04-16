package ru.otus.hw11.repositories.react;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import ru.otus.hw11.domain.Author;

@Repository
public interface AuthorReactRepository extends ReactiveMongoRepository<Author, String> {
}
