package ru.otus.hw06.repositories;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Repository;
import ru.otus.hw06.models.Comment;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class CommentRepositoryJpa implements CommentRepository {
    @PersistenceContext
    private final EntityManager em;

    @Override
    public long count() {
        val query = em.createQuery("select count(c) from Comment c", Long.class);
        return query.getSingleResult();

    }

    @Override
    public Comment save(Comment comment) {
        if (comment.getId() == 0) {
            em.persist(comment);
            return comment;
        } else {
            return em.merge(comment);
        }

    }

    @Override
    public Optional<Comment> findById(long id) {
        return Optional.ofNullable(em.find(Comment.class, id));
    }

    @Override
    public List<Comment> findAll() {
        val query = em.createQuery("select c from Comment c left join fetch c.book b left join fetch b.author", Comment.class);
        return query.getResultList();
    }

    @Override
    public void deleteById(long id) {
        val comment = em.find(Comment.class, id);
        if (comment == null)
            return;
        em.remove(comment);
    }
}
