package com.blog.service;

import com.blog.dao.UserDAO;
import com.blog.model.User;

public class UserService {
    private UserDAO userDAO = new UserDAO();

    public User login(String username, String password) {
        if (userDAO.validateUser(username, password)) {
            return userDAO.getUserByUsername(username);
        }
        return null;
    }

    public boolean register(User user) {
        // 检查用户名是否已存在
        if (userDAO.getUserByUsername(user.getUsername()) != null) {
            return false; // 用户名已存在
        }
        return userDAO.registerUser(user);
    }

    public User getUserByUsername(String username) {
        return userDAO.getUserByUsername(username);
    }
}