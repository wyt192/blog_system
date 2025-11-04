<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
    <title>${requestScope.article.title}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/views/common/header.jsp" />

    <div class="container article-detail">
        <c:if test="${not empty requestScope.error}">
            <p class="error-message">${requestScope.error}</p>
        </c:if>
        <c:if test="${param.commentDeleted == 'true'}">
            <p class="success-message">评论删除成功！</p>
        </c:if>

        <div class="article-content">
            <h1>${requestScope.article.title}</h1>
            <p class="article-meta">
                作者: ${requestScope.article.authorUsername} |
                发布于: <fmt:formatDate value="${requestScope.article.createdAt}" pattern="yyyy-MM-dd HH:mm"/>
                <c:if test="${requestScope.article.createdAt.time ne requestScope.article.updatedAt.time}">
                     (最后更新: <fmt:formatDate value="${requestScope.article.updatedAt}" pattern="yyyy-MM-dd HH:mm"/>)
                </c:if>
            </p>
            <div class="article-body">
                <p>${requestScope.article.content}</p>
            </div>

            <c:if test="${sessionScope.currentUser != null && sessionScope.currentUser.id == requestScope.article.userId}">
                <div class="article-actions">
                    <a href="${pageContext.request.contextPath}/articles/edit?id=${requestScope.article.id}" class="button secondary">编辑</a>
                    <form action="${pageContext.request.contextPath}/articles/delete" method="post" style="display:inline;" onsubmit="return confirm('确定要删除这篇文章吗？');">
                        <input type="hidden" name="id" value="${requestScope.article.id}">
                        <button type="submit" class="button danger">删除</button>
                    </form>
                </div>
            </c:if>
        </div>

        <div class="comments-section" id="comments">
            <h2>评论 (${requestScope.comments.size()})</h2>
            <c:if test="${sessionScope.currentUser != null}">
                <div class="add-comment-form">
                    <h3>发表评论</h3>
                    <form action="${pageContext.request.contextPath}/articles/comment" method="post">
                        <input type="hidden" name="articleId" value="${requestScope.article.id}">
                        <div class="form-group">
                            <label for="commentContent">你的评论:</label>
                            <textarea id="commentContent" name="content" rows="5" required></textarea>
                        </div>
                        <div class="form-group">
                            <button type="submit" class="button primary">提交评论</button>
                        </div>
                    </form>
                </div>
            </c:if>
            <c:if test="${sessionScope.currentUser == null}">
                <p>请 <a href="${pageContext.request.contextPath}/auth/login">登录</a> 后发表评论。</p>
            </c:if>


            <div class="comment-list">
                <c:choose>
                    <c:when test="${not empty requestScope.comments}">
                        <c:forEach var="comment" items="${requestScope.comments}">
                            <div class="comment-item">
                                <p class="comment-meta">
                                    <strong>${comment.authorUsername}</strong> 于
                                    <fmt:formatDate value="${comment.createdAt}" pattern="yyyy-MM-dd HH:mm"/> 说:
                                </p>
                                <p class="comment-content">${comment.content}</p>
                                <c:if test="${sessionScope.currentUser != null && sessionScope.currentUser.id == comment.userId}">
                                    <form action="${pageContext.request.contextPath}/articles/deleteComment" method="post" style="display:inline;" onsubmit="return confirm('确定要删除这条评论吗？');">
                                        <input type="hidden" name="commentId" value="${comment.id}">
                                        <input type="hidden" name="articleId" value="${requestScope.article.id}">
                                        <button type="submit" class="button delete-comment-btn">删除</button>
                                    </form>
                                </c:if>
                            </div>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <p>目前还没有评论。</p>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>

</body>
</html>