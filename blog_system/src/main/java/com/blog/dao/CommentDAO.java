package com.blog.dao;

import com.blog.model.Comment;
import com.blog.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentDAO {

    public List<Comment> getCommentsByArticleId(int articleId) {
        List<Comment> comments = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT c.id, c.article_id, c.user_id, c.content, c.created_at, u.username AS author_username "
                    +
                    "FROM comments c JOIN users u ON c.user_id = u.id WHERE c.article_id = ? ORDER BY c.created_at ASC";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, articleId);
            rs = ps.executeQuery();
            while (rs.next()) {
                Comment comment = new Comment();
                comment.setId(rs.getInt("id"));
                comment.setArticleId(rs.getInt("article_id"));
                comment.setUserId(rs.getInt("user_id"));
                comment.setContent(rs.getString("content"));
                comment.setCreatedAt(rs.getTimestamp("created_at"));
                comment.setAuthorUsername(rs.getString("author_username"));
                comments.add(comment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(rs, ps, conn);
        }
        return comments;
    }

    public boolean addComment(Comment comment) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "INSERT INTO comments (article_id, user_id, content) VALUES (?, ?, ?)";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, comment.getArticleId());
            ps.setInt(2, comment.getUserId());
            ps.setString(3, comment.getContent());
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.close(ps, conn);
        }
    }

    public boolean deleteComment(int commentId, int userId) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "DELETE FROM comments WHERE id = ? AND user_id = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, commentId);
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