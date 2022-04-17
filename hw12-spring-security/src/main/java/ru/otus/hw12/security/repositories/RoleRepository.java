package ru.otus.hw12.security.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.otus.hw12.security.domain.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
}
