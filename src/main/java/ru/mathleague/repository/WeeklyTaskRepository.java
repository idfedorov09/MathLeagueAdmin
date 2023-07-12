package ru.mathleague.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mathleague.entity.User;
import ru.mathleague.entity.WeeklyTask;

import java.util.List;

public interface WeeklyTaskRepository extends JpaRepository<WeeklyTask, Void> {

    WeeklyTask findByPriority(Long priority);

    WeeklyTask findById(Long id);

    List<WeeklyTask> findAllByOrderByPriority();
}
