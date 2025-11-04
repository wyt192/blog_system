package com.blog.dao;

import com.blog.model.User;
import com.blog.util.DBUtil;
import org.mindrot.jbcrypt.BCrypt; // 导入BCrypt

import java.sql.*;

public class UserDAO {

    public User getUserByUsername(String username) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        User user = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT id, username, password, email, created_at FROM users WHERE username = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            rs = ps.executeQuery();
            if (rs.next()) {
                user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setEmail(rs.getString("email"));
                user.setCreatedAt(rs.getTimestamp("created_at"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(rs, ps, conn);
        }
        return user;
    }

    public boolean registerUser(User user) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
            ps = conn.prepareStatement(sql);
            ps.setString(1, user.getUsername());
            ps.setString(2, BCrypt.hashpw(user.getPassword(), BCrypt.gensalt())); // 哈希密码
            ps.setString(3, user.getEmail());
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.close(ps, conn);
        }
    }

    public boolean validateUser(String username, String password) {
        User user = getUserByUsername(username);
        if (user != null) {
            return BCrypt.checkpw(password, user.getPassword()); // 验证哈希密码
        }
        return false;
    }
}