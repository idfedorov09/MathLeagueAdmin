package ru.mathleague.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mathleague.entity.UsedSecretKey;

import java.util.List;

public interface UsedSecretKeyRepository extends JpaRepository<UsedSecretKey, Long> {

    UsedSecretKey findBySecretKey(String secretKey);

    List<UsedSecretKey> findAllByOrderById();

}
