package ru.mathleague.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.mathleague.entity.User;
import ru.mathleague.entity.WeeklyTask;

import java.util.List;

public interface WeeklyTaskRepository extends JpaRepository<WeeklyTask, Void> {

    WeeklyTask findByPriority(Long priority);

    WeeklyTask findById(Long id);

    List<WeeklyTask> findAllByOrderByPriority();

    @Transactional
    @Modifying
    @Query("UPDATE WeeklyTask w SET w.priority = w.priority - 1")
    void decreasePriorityForAllTasks();
}
