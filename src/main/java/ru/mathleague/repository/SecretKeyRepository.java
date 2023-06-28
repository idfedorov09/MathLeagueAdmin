package ru.mathleague.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mathleague.entity.SecretKey;

public interface SecretKeyRepository extends JpaRepository<SecretKey, Long> {

    SecretKey findBySecretKey(String secretKey);
}
