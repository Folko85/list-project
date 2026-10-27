package main.service;


import main.exception.EntityNotFoundException;
import main.model.Task;
import main.model.User;
import main.repository.TaskRepository;
import main.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class TaskService {

    private final TaskRepository taskRepository;

    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public Task findById(Integer id, String username) {
        User user = findUser(username);
        return taskRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Task is not exist"));
    }

    public List<Task> findAll(String username) {
        User user = findUser(username);
        return taskRepository.findAllByUserId(user.getId());
    }

    public Task create(String title, String username) {
        User user = findUser(username);
        return taskRepository.save(new Task().setTitle(title).setUser(user));
    }

    public Task update(Integer id, String title, String username) {
        if (id == null) {
            throw new EntityNotFoundException("Task is not exist");
        }
        Task task = findById(id, username);
        task.setTitle(title);
        return taskRepository.save(task);
    }

    public void deleteById(Integer id, String username) {
        taskRepository.delete(findById(id, username));
    }

    public void deleteAll(String username) {
        User user = findUser(username);
        List<Task> tasks = taskRepository.findAllByUserId(user.getId());
        if (tasks.isEmpty()) {
            throw new EntityNotFoundException("Tasks is not exist");
        }
        taskRepository.deleteAll(tasks);
    }

    private User findUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Нет такого пользователя"));
    }
}
