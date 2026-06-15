package main.model;

import java.time.LocalDate;

import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;

@Entity
@Data
@Accessors(chain = true)
@Table(name = "task_tab")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    private String title;

    @UpdateTimestamp
    @Column(name = "date_of_create")
    private LocalDate dateOfCreate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Task setId(Integer id) {
        this.id = id;
        return this;
    }

    public Task setUser(User user) {
        this.user = user;
        return this;
    }

    public Task setTitle(String title) {
        this.title = title;
        return this;
    }

}
