package ru.otus.hw06.repositories;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.hibernate.cfg.NotYetImplementedException;
import org.springframework.stereotype.Repository;
import ru.otus.hw06.models.Genre;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class GenreRepositoryJpa implements GenreRepository {
    @PersistenceContext
    private final EntityManager em;

    @Override
    public long count() {
        val query = em.createQuery("select count(g) from Genre g", Long.class);
        return query.getSingleResult();
    }

    @Override
    public Genre save(Genre genre) {
        if (genre.getId() == 0) {
            em.persist(genre);
            return genre;
        } else {
            return em.merge(genre);
        }

    }

    @Override
    public Optional<Genre> findById(long id) {
        return Optional.ofNullable(em.find(Genre.class, id));
    }

    @Override
    public List<Genre> findAll() {
        val query = em.createQuery("select g from Genre g", Genre.class);
        return query.getResultList();

    }

    @Override
    public void deleteById(long id) {
        val genre = em.find(Genre.class, id);
        if (genre == null)
            return;
        em.remove(genre);
    }
}
