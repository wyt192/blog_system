package com.blog.controller;

import com.blog.model.Article;
import com.blog.model.Comment;
import com.blog.model.User;
import com.blog.service.ArticleService;
import com.blog.service.CommentService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/articles/*") // 匹配 /articles/list, /articles/view, /articles/create, /articles/edit,
                           // /articles/delete
public class ArticleServlet extends HttpServlet {
    private ArticleService articleService;
    private CommentService commentService;

    @Override
    public void init() throws ServletException {
        super.init();
        articleService = new ArticleService();
        commentService = new CommentService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String action = request.getPathInfo();

        if (action == null || action.equals("/") || action.equals("/list")) {
            handleArticleList(request, response);
        } else if (action.equals("/view")) {
            handleArticleView(request, response);
        } else if (action.equals("/create")) {
            if (!isAuthenticated(request, response))
                return;
            request.getRequestDispatcher("/WEB-INF/views/article/create.jsp").forward(request, response);
        } else if (action.equals("/edit")) {
            if (!isAuthenticated(request, response))
                return;
            handleArticleEditForm(request, response);
        } else if (action.equals("/delete")) {
            if (!isAuthenticated(request, response))
                return;
            handleArticleDelete(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8"); // 确保编码设置
        response.setContentType("text/html;charset=UTF-8"); // 确保编码设置
        String action = request.getPathInfo();

        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/articles/list");
            return;
        }

        switch (action) {
            case "/create":
                if (!isAuthenticated(request, response))
                    return;
                handleArticleCreate(request, response);
                break;
            case "/edit":
                if (!isAuthenticated(request, response))
                    return;
                handleArticleUpdate(request, response);
                break;
            case "/delete":
                if (!isAuthenticated(request, response))
                    return;
                handleArticleDelete(request, response);
                break;
            case "/comment":
                if (!isAuthenticated(request, response))
                    return;
                handleAddComment(request, response);
                break;
            case "/deleteComment":
                if (!isAuthenticated(request, response))
                    return;
                handleDeleteComment(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                break;
        }
    }

    private void handleArticleList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Article> articles = articleService.getAllArticles();
        request.setAttribute("articles", articles);
        request.getRequestDispatcher("/WEB-INF/views/article/list.jsp").forward(request, response);
    }

    private void handleArticleView(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Article ID is required.");
            return;
        }
        try {
            int articleId = Integer.parseInt(idStr);
            Article article = articleService.getArticleById(articleId);
            if (article == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Article not found.");
                return;
            }
            List<Comment> comments = commentService.getCommentsByArticleId(articleId);
            request.setAttribute("article", article);
            request.setAttribute("comments", comments);
            request.getRequestDispatcher("/WEB-INF/views/article/view.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Article ID format.");
        }
    }

    private void handleArticleCreate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        if (currentUser == null) { // 双重检查，实际isAuthenticated已处理
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }

        String title = request.getParameter("title");
        String content = request.getParameter("content");

        if (title == null || title.trim().isEmpty() || content == null || content.trim().isEmpty()) {
            request.setAttribute("error", "标题和内容不能为空。");
            request.getRequestDispatcher("/WEB-INF/views/article/create.jsp").forward(request, response);
            return;
        }

        Article article = new Article();
        article.setUserId(currentUser.getId());
        article.setTitle(title);
        article.setContent(content);

        if (articleService.createArticle(article)) {
            response.sendRedirect(request.getContextPath() + "/articles/view?id=" + article.getId());
        } else {
            request.setAttribute("error", "文章创建失败。");
            request.getRequestDispatcher("/WEB-INF/views/article/create.jsp").forward(request, response);
        }
    }

    private void handleArticleEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        String idStr = request.getParameter("id");

        if (currentUser == null || idStr == null || idStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }

        try {
            int articleId = Integer.parseInt(idStr);
            Article article = articleService.getArticleById(articleId);

            if (article == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Article not found.");
                return;
            }
            // 检查当前用户是否是文章作者
            if (article.getUserId() != currentUser.getId()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "You are not authorized to edit this article.");
                return;
            }

            request.setAttribute("article", article);
            request.getRequestDispatcher("/WEB-INF/views/article/edit.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Article ID format.");
        }
    }

    private void handleArticleUpdate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        String idStr = request.getParameter("id");
        String title = request.getParameter("title");
        String content = request.getParameter("content");

        if (currentUser == null || idStr == null || idStr.isEmpty() || title == null || title.trim().isEmpty()
                || content == null || content.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }

        try {
            int articleId = Integer.parseInt(idStr);
            Article existingArticle = articleService.getArticleById(articleId);

            if (existingArticle == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Article not found.");
                return;
            }
            if (existingArticle.getUserId() != currentUser.getId()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "You are not authorized to update this article.");
                return;
            }

            existingArticle.setTitle(title);
            existingArticle.setContent(content);

            if (articleService.updateArticle(existingArticle)) {
                response.sendRedirect(request.getContextPath() + "/articles/view?id=" + existingArticle.getId());
            } else {
                request.setAttribute("error", "文章更新失败。");
                request.setAttribute("article", existingArticle); // 再次设置，以便在页面上显示旧数据
                request.getRequestDispatcher("/WEB-INF/views/article/edit.jsp").forward(request, response);
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Article ID format.");
        }
    }

    private void handleArticleDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        String idStr = request.getParameter("id");

        if (currentUser == null || idStr == null || idStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }

        try {
            int articleId = Integer.parseInt(idStr);
            // 检查是否是作者本人
            Article article = articleService.getArticleById(articleId);
            if (article == null || article.getUserId() != currentUser.getId()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "You are not authorized to delete this article.");
                return;
            }

            if (articleService.deleteArticle(articleId, currentUser.getId())) {
                response.sendRedirect(request.getContextPath() + "/articles/list?deleted=true");
            } else {
                response.sendRedirect(
                        request.getContextPath() + "/articles/view?id=" + articleId + "&error=deleteFailed");
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Article ID format.");
        }
    }

    private void handleAddComment(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        String articleIdStr = request.getParameter("articleId");
        String content = request.getParameter("content");

        if (currentUser == null || articleIdStr == null || articleIdStr.isEmpty() || content == null
                || content.trim().isEmpty()) {
            // 如果未登录或缺少参数，直接重定向回文章详情页并显示错误
            response.sendRedirect(
                    request.getContextPath() + "/articles/view?id=" + articleIdStr + "&error=missingCommentInfo");
            return;
        }

        try {
            int articleId = Integer.parseInt(articleIdStr);
            Comment comment = new Comment();
            comment.setArticleId(articleId);
            comment.setUserId(currentUser.getId());
            comment.setContent(content);

            if (commentService.addComment(comment)) {
                response.sendRedirect(request.getContextPath() + "/articles/view?id=" + articleId + "#comments"); // 添加评论后跳转回文章详情并定位到评论区
            } else {
                response.sendRedirect(
                        request.getContextPath() + "/articles/view?id=" + articleId + "&error=commentFailed");
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Article ID format for comment.");
        }
    }

    private void handleDeleteComment(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        String commentIdStr = request.getParameter("commentId");
        String articleIdStr = request.getParameter("articleId"); // 需要知道文章ID以便重定向

        if (currentUser == null || commentIdStr == null || commentIdStr.isEmpty() || articleIdStr == null
                || articleIdStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/auth/login"); // 或者返回错误信息
            return;
        }

        try {
            int commentId = Integer.parseInt(commentIdStr);
            int articleId = Integer.parseInt(articleIdStr);

            if (commentService.deleteComment(commentId, currentUser.getId())) {
                response.sendRedirect(
                        request.getContextPath() + "/articles/view?id=" + articleId + "&commentDeleted=true");
            } else {
                response.sendRedirect(
                        request.getContextPath() + "/articles/view?id=" + articleId + "&error=commentDeleteFailed");
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid ID format for comment deletion.");
        }
    }

    // 辅助方法：检查用户是否已登录
    private boolean isAuthenticated(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login?redirect=" + request.getRequestURI()); // 未登录则跳转到登录页
            return false;
        }
        return true;
    }
}