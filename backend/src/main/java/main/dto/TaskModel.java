package main.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;


@Data
@Accessors(chain = true)
public class TaskModel {

    private Integer id;

    @NotBlank
    private String title;

    public TaskModel setId(Integer id) {
        this.id = id;
        return this;
    }

    public TaskModel setTitle(String title) {  //это (возвращение себя) для красивой записи в маппере
        this.title = title;
        return this;
    }
}
