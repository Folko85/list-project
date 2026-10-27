package main.repository;

import main.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {

    List<Task> findAllByUserId(Integer userId);

    Optional<Task> findByIdAndUserId(Integer id, Integer userId);
}
