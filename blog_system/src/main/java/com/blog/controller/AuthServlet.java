package com.blog.controller;

import com.blog.model.User;
import com.blog.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/auth/*") // 匹配 /auth/login, /auth/register, /auth/logout
public class AuthServlet extends HttpServlet {
    private UserService userService;

    @Override
    public void init() throws ServletException {
        super.init();
        userService = new UserService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String action = request.getPathInfo(); // 获取路径信息，如 /login, /register

        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/"); // 重定向到首页
            return;
        }

        switch (action) {
            case "/login":
                request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
                break;
            case "/register":
                request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
                break;
            case "/logout":
                HttpSession session = request.getSession(false); // 不创建新session
                if (session != null) {
                    session.invalidate(); // 使session失效
                }
                response.sendRedirect(request.getContextPath() + "/"); // 重定向到首页
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        String action = request.getPathInfo();

        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        switch (action) {
            case "/login":
                handleLogin(request, response);
                break;
            case "/register":
                handleRegister(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                break;
        }
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        User user = userService.login(username, password);

        if (user != null) {
            HttpSession session = request.getSession();
            session.setAttribute("currentUser", user); // 将用户对象存入session
            response.sendRedirect(request.getContextPath() + "/"); // 登录成功，重定向到首页
        } else {
            request.setAttribute("error", "用户名或密码错误。");
            request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
        }
    }

    private void handleRegister(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String email = request.getParameter("email");

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password); // 密码将在Service层进行哈希
        newUser.setEmail(email);

        if (userService.register(newUser)) {
            response.sendRedirect(request.getContextPath() + "/auth/login?registered=true"); // 注册成功，跳转到登录页
        } else {
            request.setAttribute("error", "注册失败，用户名可能已存在或信息有误。");
            request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
        }
    }
}