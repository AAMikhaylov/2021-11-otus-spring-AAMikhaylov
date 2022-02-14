package ru.otus.hw08.repositories;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.otus.hw08.models.Book;

@Repository
public interface BookRepository extends MongoRepository<Book, String> {
    long countBookByAuthorId(String authorId);
    long countBookByGenresId(String id);
}
