package main.mapper;

import main.dto.TaskModel;
import main.model.Task;
import main.model.User;
import main.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class TaskMapper {

    private TaskMapper() {
    }

    public static TaskModel map(Task item) {
        return new TaskModel()
                .setId(item.getId())
                .setTitle(item.getTitle());
    }

    public static Task reverseMap(TaskModel item, UserRepository userRepository) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetail = (UserDetails) auth.getPrincipal();
        User user = userRepository.findByUsernameAndPassword(userDetail.getUsername(), userDetail.getPassword());
        return new Task()
                .setId(item.getId())
                .setTitle(item.getTitle())
                .setUser(user);
    }

}
