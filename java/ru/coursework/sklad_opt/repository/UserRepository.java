package ru.coursework.sklad_opt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.coursework.sklad_opt.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}
