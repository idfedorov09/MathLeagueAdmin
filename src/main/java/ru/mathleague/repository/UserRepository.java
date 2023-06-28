package ru.mathleague.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mathleague.entity.User;

public interface UserRepository extends JpaRepository<User, Void> {

    User findByUsername(String username);
}
