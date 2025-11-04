package com.blog.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBUtil {
    private static Properties properties = new Properties();

    static {
        try (InputStream input = DBUtil.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                System.err.println("Sorry, unable to find db.properties");
                System.exit(1);
            }
            properties.load(input);
            Class.forName("com.mysql.cj.jdbc.Driver"); // 加载MySQL驱动
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error loading database properties or driver", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                properties.getProperty("db.url"),
                properties.getProperty("db.username"),
                properties.getProperty("db.password"));
    }

    public static void close(AutoCloseable... autoCloseables) {
        for (AutoCloseable ac : autoCloseables) {
            if (ac != null) {
                try {
                    ac.close();
                } catch (Exception e) {
                    System.err.println("Error closing resource: " + e.getMessage());
                }
            }
        }
    }
}