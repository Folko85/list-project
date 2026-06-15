package main.controller;

import jakarta.validation.Valid;
import main.dto.TaskModel;
import main.mapper.TaskMapper;
import main.repository.UserRepository;
import main.service.TaskService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ApiController {

    private final TaskService service;

    private final UserRepository userRepository;

    public ApiController(TaskService service, UserRepository userRepository) {
        this.service = service;
        this.userRepository = userRepository;
    }

    @GetMapping("/api/tasks")
    @PreAuthorize("hasAuthority('user:write')")
    public List<TaskModel> getTasks(Principal principal) {
        return service.findAll(principal).stream().map(TaskMapper::map).collect(Collectors.toList());
    }

    @GetMapping("/api/tasks/{id}")
    @PreAuthorize("hasAuthority('user:write')")
    public TaskModel getTaskById(@PathVariable Integer id) {
        return TaskMapper.map(service.findById(id));
    }

    @PostMapping("/api/tasks")
    @PreAuthorize("hasAuthority('user:write')")
    public TaskModel addTask(@Valid @RequestBody TaskModel task) {
        return TaskMapper.map(service.save(TaskMapper.reverseMap(task, userRepository)));
    }

    @PutMapping("/api/tasks")
    @PreAuthorize("hasAuthority('user:write')")
    public TaskModel editTask(@Valid @RequestBody TaskModel task) {
        return TaskMapper.map(service.save(TaskMapper.reverseMap(task, userRepository)));
    }

    @DeleteMapping("/api/tasks/{id}")
    @PreAuthorize("hasAuthority('user:write')")
    public void deleteTaskById(@PathVariable Integer id) {
        service.deleteById(id);
    }

    @DeleteMapping("/api/tasks")
    @PreAuthorize("hasAuthority('user:write')")
    public void deleteTasks() {
        service.deleteAll();
    }
}