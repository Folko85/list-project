package main.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import main.dto.TaskModel;
import main.mapper.TaskMapper;
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
@RequiredArgsConstructor
public class ApiController {

    private final TaskService service;

    @GetMapping("/api/tasks")
    @PreAuthorize("hasAuthority('user:write')")
    public List<TaskModel> getTasks(Principal principal) {
        return service.findAll(principal.getName()).stream().map(TaskMapper::map).collect(Collectors.toList());
    }

    @GetMapping("/api/tasks/{id}")
    @PreAuthorize("hasAuthority('user:write')")
    public TaskModel getTaskById(@PathVariable Integer id, Principal principal) {
        return TaskMapper.map(service.findById(id, principal.getName()));
    }

    @PostMapping("/api/tasks")
    @PreAuthorize("hasAuthority('user:write')")
    public TaskModel addTask(@Valid @RequestBody TaskModel task, Principal principal) {
        return TaskMapper.map(service.create(task.getTitle(), principal.getName()));
    }

    @PutMapping("/api/tasks")
    @PreAuthorize("hasAuthority('user:write')")
    public TaskModel editTask(@Valid @RequestBody TaskModel task, Principal principal) {
        return TaskMapper.map(service.update(task.getId(), task.getTitle(), principal.getName()));
    }

    @DeleteMapping("/api/tasks/{id}")
    @PreAuthorize("hasAuthority('user:write')")
    public void deleteTaskById(@PathVariable Integer id, Principal principal) {
        service.deleteById(id, principal.getName());
    }

    @DeleteMapping("/api/tasks")
    @PreAuthorize("hasAuthority('user:write')")
    public void deleteTasks(Principal principal) {
        service.deleteAll(principal.getName());
    }
}