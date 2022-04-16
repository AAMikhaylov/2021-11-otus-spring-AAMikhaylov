package ru.otus.hw11.repositories.react;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import ru.otus.hw11.domain.Genre;

@Repository
public interface GenreReactRepository extends ReactiveMongoRepository<Genre, String>, GenreRepositoryCustom {
    Flux<Genre> findByName(String name);
}
