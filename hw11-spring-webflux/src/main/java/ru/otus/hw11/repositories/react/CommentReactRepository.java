package ru.otus.hw11.repositories.react;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw11.domain.Comment;

@Repository
public interface CommentReactRepository extends ReactiveMongoRepository<Comment, String> {
    Flux<Comment> findAllByBookId(String bookId);

    Mono<Void> deleteAllByBookId(String bookId);
}
