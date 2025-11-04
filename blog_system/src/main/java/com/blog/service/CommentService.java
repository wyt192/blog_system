package com.blog.service;

import com.blog.dao.CommentDAO;
import com.blog.model.Comment;

import java.util.List;

public class CommentService {
    private CommentDAO commentDAO = new CommentDAO();

    public List<Comment> getCommentsByArticleId(int articleId) {
        return commentDAO.getCommentsByArticleId(articleId);
    }

    public boolean addComment(Comment comment) {
        return commentDAO.addComment(comment);
    }

    public boolean deleteComment(int commentId, int userId) {
        return commentDAO.deleteComment(commentId, userId);
    }
}