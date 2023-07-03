package ru.mathleague.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mathleague.entity.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Void> {

    User findByUsername(String username);

    List<User> findAllByOrderById();

    User findById(Long id);
}
