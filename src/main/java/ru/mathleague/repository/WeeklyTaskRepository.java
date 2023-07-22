package ru.mathleague.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Transactional
    @Modifying
    @Query("UPDATE WeeklyTask e SET e.priority = e.priority - 1 WHERE e.priority > :a AND e.priority <= :b")
    void decreasePriorityBetween(long a, long b);

    @Transactional
    @Modifying
    @Query("UPDATE WeeklyTask e SET e.priority = e.priority - 1 WHERE e.priority > :a")
    void decreasePriorityAfter(long a);

    @Transactional
    @Modifying
    @Query("UPDATE WeeklyTask e SET e.priority = e.priority + 1 WHERE e.priority >= :a AND e.priority < :b")
    void increasePriorityBetween(long a, long b);

    @Transactional
    @Modifying
    @Query("UPDATE WeeklyTask e SET e.priority = :newPriority WHERE e.id = :id")
    void changePriorityById(@Param("id") long id, long newPriority);

    @Query("SELECT e.id FROM WeeklyTask e WHERE e.priority = :priority")
    Long findIdByPriority(@Param("priority") long priority);
}
