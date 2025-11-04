<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>编辑文章: ${requestScope.article.title}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/views/common/header.jsp" />

    <div class="container article-form">
        <h1>编辑文章</h1>
        <c:if test="${not empty requestScope.error}">
            <p class="error-message">${requestScope.error}</p>
        </c:if>
        <form action="${pageContext.request.contextPath}/articles/edit" method="post">
            <input type="hidden" name="id" value="${requestScope.article.id}">
            <div class="form-group">
                <label for="title">文章标题:</label>
                <input type="text" id="title" name="title" value="${requestScope.article.title}" required>
            </div>
            <div class="form-group">
                <label for="content">文章内容:</label>
                <textarea id="content" name="content" rows="15" required>${requestScope.article.content}</textarea>
            </div>
            <div class="form-group">
                <button type="submit" class="button primary">更新</button>
                <a href="${pageContext.request.contextPath}/articles/view?id=${requestScope.article.id}" class="button secondary">取消</a>
            </div>
        </form>
    </div>

</body>
</html>