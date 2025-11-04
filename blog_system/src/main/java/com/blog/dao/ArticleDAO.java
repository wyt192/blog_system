package com.blog.dao;

import com.blog.model.Article;
import com.blog.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ArticleDAO {

    public List<Article> getAllArticles() {
        List<Article> articles = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT a.id, a.user_id, a.title, a.content, a.created_at, a.updated_at, u.username AS author_username "
                    +
                    "FROM articles a JOIN users u ON a.user_id = u.id ORDER BY a.created_at DESC";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Article article = new Article();
                article.setId(rs.getInt("id"));
                article.setUserId(rs.getInt("user_id"));
                article.setTitle(rs.getString("title"));
                article.setContent(rs.getString("content"));
                article.setCreatedAt(rs.getTimestamp("created_at"));
                article.setUpdatedAt(rs.getTimestamp("updated_at"));
                article.setAuthorUsername(rs.getString("author_username"));
                articles.add(article);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(rs, ps, conn);
        }
        return articles;
    }

    public Article getArticleById(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Article article = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT a.id, a.user_id, a.title, a.content, a.created_at, a.updated_at, u.username AS author_username "
                    +
                    "FROM articles a JOIN users u ON a.user_id = u.id WHERE a.id = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                article = new Article();
                article.setId(rs.getInt("id"));
                article.setUserId(rs.getInt("user_id"));
                article.setTitle(rs.getString("title"));
                article.setContent(rs.getString("content"));
                article.setCreatedAt(rs.getTimestamp("created_at"));
                article.setUpdatedAt(rs.getTimestamp("updated_at"));
                article.setAuthorUsername(rs.getString("author_username"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(rs, ps, conn);
        }
        return article;
    }

    public boolean createArticle(Article article) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "INSERT INTO articles (user_id, title, content) VALUES (?, ?, ?)";
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS); // 获取生成的主键
            ps.setInt(1, article.getUserId());
            ps.setString(2, article.getTitle());
            ps.setString(3, article.getContent());
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        article.setId(generatedKeys.getInt(1)); // 设置文章ID
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.close(ps, conn);
        }
    }

    public boolean updateArticle(Article article) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "UPDATE articles SET title = ?, content = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ? AND user_id = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, article.getTitle());
            ps.setString(2, article.getContent());
            ps.setInt(3, article.getId());
            ps.setInt(4, article.getUserId());
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.close(ps, conn);
        }
    }

    public boolean deleteArticle(int articleId, int userId) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "DELETE FROM articles WHERE id = ? AND user_id = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, articleId);
            ps.setInt(2, userId);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.close(ps, conn);
        }
    }
}