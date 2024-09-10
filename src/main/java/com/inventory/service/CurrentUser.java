package com.inventory.service;

import com.inventory.model.User;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Component
@SessionScope
public class CurrentUser {
    private long id;
    private String username;

    public void login(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
    }

    public boolean isLoggedIn() {
        return id != 0;
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void logout() {
        this.id = 0;
        this.username = "";
    }
}
