package main.controller;

import main.dto.TaskModel;
import main.model.Task;
import main.model.User;
import main.repository.TaskRepository;
import main.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public class ApiControllerTest extends AbstractTest {

    @Autowired
    private TaskRepository taskRepository;                //в тестовом классе конструкторов нельзя

    @Autowired
    private UserRepository userRepository;

    private Task task;


    @BeforeEach
    public void setUpTest() {
        User test = new User().setFirstname("Test").setLastname("User")
                .setUsername("test").setPassword("password");

        userRepository.save(test);
        task = new Task().setTitle("taskText").setUser(test);
        taskRepository.save(task);
    }

    @AfterEach
    public void tearDown() {
        taskRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "test", authorities = "user:write")
    public void testGetTasksSuccess() throws Exception {
        createTask("other", "otherTask");
        mockMvc.perform(MockMvcRequestBuilders
                                .get("/api/tasks")
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
                        .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(task.getId()))
                        .andExpect(MockMvcResultMatchers.jsonPath("$[0].title").value(task.getTitle()));
    }

    @Test
    @WithMockUser(username = "test", authorities = "user:write")
    public void testGetTaskByIdSuccess() throws Exception {
        long id = task.getId();   // абракадабра какая-то
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/tasks/{id}", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                        .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(task.getId()))
                        .andExpect(MockMvcResultMatchers.jsonPath("$.title").value(task.getTitle()));
    }

    @Test
    @WithMockUser(username = "test", authorities = "user:write")
    public void testGetTaskByIdFailure() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/tasks/{id}", 100)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("Task is not exist"));
    }

    @Test
    @WithMockUser(username = "test", authorities = "user:write")
    public void testAddTaskSuccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/tasks")
                        .content(mapper.writeValueAsString(new TaskModel().setTitle("titleText")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists());
        Task createdTask = taskRepository.findAllByUserId(
                        userRepository.findByUsername("test").orElseThrow().getId()).stream()
                .filter(savedTask -> savedTask.getTitle().equals("titleText"))
                .findFirst()
                .orElseThrow();
        org.junit.jupiter.api.Assertions.assertEquals("test", createdTask.getUser().getUsername());
    }

    @Test
    @WithMockUser(username = "test", authorities = "user:write")
    public void testAddTaskFailure() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/tasks")
                        .content(mapper.writeValueAsString(new TaskModel().setTitle("")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Field cannot being empty"));
    }

    @Test
    @WithMockUser(username = "test", authorities = "user:write")
    public void testEditTaskSuccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/tasks")
                        .content(mapper.writeValueAsString(new TaskModel().setId(task.getId()).setTitle("newTitleText")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("newTitleText"));
    }

    @Test
    @WithMockUser(username = "test", authorities = "user:write")
    public void testEditTaskFailure() throws Exception {   // в нашей реализации сделать так не выйдет, ну и фиг с ним
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/tasks")
                        .content(mapper.writeValueAsString(new TaskModel().setId(task.getId()).setTitle("")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Field cannot being empty"));
    }

    @Test
    @WithMockUser(username = "test", authorities = "user:write")
    public void testDeleteTaskByIdSuccess() throws Exception {
        long id = task.getId();   // удаляем последний элемент. Хз, почему эти тесты цепляются друг за дружку
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/tasks/{id}", id))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(username = "test", authorities = "user:write")
    public void testDeleteTaskByIdFailure() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/tasks/{id}", 100))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("Task is not exist"));
    }

    @Test
    @WithMockUser(username = "test", authorities = "user:write")
    public void testDeleteTasksSuccess() throws Exception {
        Task foreignTask = createTask("other", "otherTask");
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/tasks"))
                .andExpect(MockMvcResultMatchers.status().isOk());
        org.junit.jupiter.api.Assertions.assertTrue(taskRepository.findById(foreignTask.getId()).isPresent());
    }

    @Test
    @WithMockUser(username = "test", authorities = "user:write")
    public void testDeleteTasksFailure() throws Exception {
        taskRepository.deleteAll();
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/tasks"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("Tasks is not exist"));
    }

    @Test
    @WithMockUser(username = "test", authorities = "user:write")
    public void testGetTaskOwnedByAnotherUserReturnsNotFound() throws Exception {
        Task foreignTask = createTask("other", "otherTask");

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/tasks/{id}", foreignTask.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("Task is not exist"));
    }

    @Test
    @WithMockUser(username = "test", authorities = "user:write")
    public void testCannotEditTaskOwnedByAnotherUser() throws Exception {
        Task foreignTask = createTask("other", "otherTask");
        TaskModel update = new TaskModel().setId(foreignTask.getId()).setTitle("changed");

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/tasks")
                        .content(mapper.writeValueAsString(update))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("Task is not exist"));

        org.junit.jupiter.api.Assertions.assertEquals("otherTask",
                taskRepository.findById(foreignTask.getId()).orElseThrow().getTitle());
    }

    @Test
    @WithMockUser(username = "test", authorities = "user:write")
    public void testCannotDeleteTaskOwnedByAnotherUser() throws Exception {
        Task foreignTask = createTask("other", "otherTask");

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/tasks/{id}", foreignTask.getId()))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("Task is not exist"));

        org.junit.jupiter.api.Assertions.assertTrue(taskRepository.findById(foreignTask.getId()).isPresent());
    }

    @Test
    @WithAnonymousUser
    public void testTasksRequireAuthentication() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/tasks"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @WithMockUser(username = "test", authorities = "user:write")
    public void testCannotReadAnotherUsersProfile() throws Exception {
        User otherUser = new User().setUsername("other").setPassword("password");
        userRepository.save(otherUser);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/users/{username}", "other")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @WithMockUser(username = "test", authorities = "user:write")
    public void testCanReadOwnProfile() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/users/{username}", "test")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("test"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstname").value("Test"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("User"));
    }

    private Task createTask(String username, String title) {
        User user = new User().setUsername(username).setPassword("password");
        userRepository.save(user);
        return taskRepository.save(new Task().setTitle(title).setUser(user));
    }
}