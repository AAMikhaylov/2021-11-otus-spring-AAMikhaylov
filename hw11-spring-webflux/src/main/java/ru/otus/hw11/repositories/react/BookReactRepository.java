package ru.otus.hw11.repositories.react;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import ru.otus.hw11.domain.Book;


@Repository
public interface BookReactRepository extends ReactiveMongoRepository<Book, String> {
    Mono<Long> countBookByAuthorId(String authorId);
    Mono<Long> countBookByGenresId(String id);
}
