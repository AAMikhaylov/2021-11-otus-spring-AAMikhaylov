package ru.otus.hw11.repositories.react;

import reactor.core.publisher.Mono;
import ru.otus.hw11.domain.Genre;

public interface GenreRepositoryCustom {
    Mono<Genre> cascadeUpdate(Genre genre);

}
