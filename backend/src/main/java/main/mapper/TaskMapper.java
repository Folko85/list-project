package main.mapper;

import main.dto.TaskModel;
import main.model.Task;

public class TaskMapper {

    private TaskMapper() {
    }

    public static TaskModel map(Task item) {
        return new TaskModel()
                .setId(item.getId())
                .setTitle(item.getTitle());
    }

}
