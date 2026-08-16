package main.service;


import main.exception.EntityNotFoundException;
import main.model.Task;
import main.model.User;
import main.repository.TaskRepository;
import main.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class TaskService {

    private final TaskRepository taskRepository;

    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public Task findById(Integer id) {
        return taskRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Task is not exist"));
    }

    public List<Task> findAll(Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow(() -> new UsernameNotFoundException("Нет такого пользователя"));
        return taskRepository.findAll().stream().filter(task -> task.getUser() != null)
                .filter(t -> t.getUser().getId().equals(user.getId()))
                .collect(Collectors.toList());
    }

    public Task save(Task task) {
        return taskRepository.save(task);
    }

    public void deleteById(Integer id) {
        if (taskRepository.findById(id).isEmpty()) {
            throw new EntityNotFoundException("Task is not exist");
        } else taskRepository.deleteById(id);
    }

    public void deleteAll() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetail = (UserDetails) auth.getPrincipal();
        User user = userRepository.findByUsernameAndPassword(userDetail.getUsername(), userDetail.getPassword());
        List<Task> tasks = taskRepository.findAll().stream()
                .filter(t -> t.getUser().getId().equals(user.getId()))
                .toList();
        if (tasks.isEmpty()) {
            throw new EntityNotFoundException("Tasks is not exist");
        } else {
            tasks.forEach(task -> taskRepository.deleteById(task.getId()));
        }

    }
}
