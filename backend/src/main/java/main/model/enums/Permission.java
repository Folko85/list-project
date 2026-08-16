package main.model.enums;

import lombok.Getter;

@Getter
public enum Permission {
    USER("user:write"),
    MODERATE("user:moderate"),
    ADMIN("user:administrate");
    private final String permission;

    Permission(String permission) {
        this.permission = permission;
    }

}
