package main.controller;

import main.mapper.TaskMapper;
import main.model.Task;
import main.model.User;
import main.repository.TaskRepository;
import main.repository.UserRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

public class ApiControllerTest extends AbstractTest {

    @Autowired
    private TaskRepository taskRepository;                //в тестовом классе конструкторов нельзя

    @Autowired
    private UserRepository userRepository;

    private Task task;


    @Before              //обычное Before выполняется перед каждым тестом
    public void setUpTest() {
        User test = new User();
        test.setUsername("test");
        test.setPassword("password");

        userRepository.save(test);
        task = new Task().setTitle("taskText").setUser(test);
        taskRepository.save(task);
    }

    @After                      //поэтому после каждого теста удаляем все таски
    public void tearDown() {
        taskRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "test")
    public void testGetTasksSuccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/tasks")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(List.of(TaskMapper.map(task)))));
    }

    @Test
    @WithMockUser(username = "test")
    public void testGetTaskByIdSuccess() throws Exception {
        long id = task.getId();   // абракадабра какая-то
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/tasks/{id}", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(TaskMapper.map(task))));
    }

    @Test
    @WithMockUser(username = "test")
    public void testGetTaskByIdFailure() throws Exception {
        taskRepository.deleteAll();  // для этого теста нужны особые условия
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/tasks/{id}", 100)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("Task is not exist"));
    }

    @Test
    @WithMockUser(username = "test")
    public void testAddTaskSuccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/tasks")
                        .content(mapper.writeValueAsString(new Task().setTitle("titleText")))   //постим задачу
                        .contentType(MediaType.APPLICATION_JSON)                          //тип на входе json
                        .accept(MediaType.APPLICATION_JSON))                              //вернуть должно json
                .andExpect(MockMvcResultMatchers.status().isOk())                      //статус 200
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists());   //у возвращённых объектов есть id
    }

    @Test
    @WithMockUser(username = "test")
    public void testAddTaskFailure() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/tasks")
                        .content(mapper.writeValueAsString(new Task().setTitle("")))   //постим задачу
                        .contentType(MediaType.APPLICATION_JSON)                          //тип на входе json
                        .accept(MediaType.APPLICATION_JSON))                              //вернуть должно json
                .andExpect(MockMvcResultMatchers.status().isBadRequest())                      //статус 400
                .andExpect(MockMvcResultMatchers.content().string("Field cannot being empty"));   //у возвращённых объектов есть id
    }

    @Test
    @WithMockUser(username = "test")
    public void testEditTaskSuccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/tasks")
                        .content(mapper.writeValueAsString(new Task().setTitle("newTitleText")))  //меняем текст на этот
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("newTitleText")); //проверяем, что сменился
    }

    @Test
    @WithMockUser(username = "test")
    public void testEditTaskFailure() throws Exception {   // в нашей реализации сделать так не выйдет, ну и фиг с ним
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/tasks")
                        .content(mapper.writeValueAsString(new Task().setTitle("")))  //меняем текст на этот
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Field cannot being empty")); //проверяем, что сменился
    }

    @Test
    @WithMockUser(username = "test")
    public void testDeleteTaskByIdSuccess() throws Exception {
        long id = task.getId();   // удаляем последний элемент. Хз, почему эти тесты цепляются друг за дружку
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/tasks/{id}", id))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(username = "test")
    public void testDeleteTaskByIdFailure() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/tasks/{id}", 100))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("Task is not exist"));
    }

    @Test
    @WithMockUser(username = "test")
    public void testDeleteTasksSuccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/tasks"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(username = "test")
    public void testDeleteTasksFailure() throws Exception {
        taskRepository.deleteAll();   // тоже особые условия
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/tasks"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("Tasks is not exist"));
    }
}