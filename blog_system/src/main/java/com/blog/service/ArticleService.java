package com.blog.service;

import com.blog.dao.ArticleDAO;
import com.blog.model.Article;

import java.util.List;

public class ArticleService {
    private ArticleDAO articleDAO = new ArticleDAO();

    public List<Article> getAllArticles() {
        return articleDAO.getAllArticles();
    }

    public Article getArticleById(int id) {
        return articleDAO.getArticleById(id);
    }

    public boolean createArticle(Article article) {
        return articleDAO.createArticle(article);
    }

    public boolean updateArticle(Article article) {
        return articleDAO.updateArticle(article);
    }

    public boolean deleteArticle(int articleId, int userId) {
        return articleDAO.deleteArticle(articleId, userId);
    }
}