<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
    <title>所有文章</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/views/common/header.jsp" />

    <div class="container article-list">
        <h1>所有文章</h1>

        <c:if test="${param.deleted == 'true'}">
            <p class="success-message">文章删除成功！</p>
        </c:if>

        <c:if test="${empty requestScope.articles}">
            <p>目前还没有文章。</p>
            <c:if test="${sessionScope.currentUser != null}">
                <p><a href="${pageContext.request.contextPath}/articles/create" class="button primary">立即发布第一篇文章</a></p>
            </c:if>
        </c:if>

        <div class="articles-grid">
            <c:forEach var="article" items="${requestScope.articles}">
                <div class="article-card">
                    <h2><a href="${pageContext.request.contextPath}/articles/view?id=${article.id}">${article.title}</a></h2>
                    <p class="article-meta">
                        作者: ${article.authorUsername} |
                        发布日期: <fmt:formatDate value="${article.createdAt}" pattern="yyyy-MM-dd HH:mm"/>
                    </p>
                    <div class="article-summary">
                        <c:set var="maxLength" value="200" />
<c:set var="contentLength" value="${article.content.length()}" />
<c:set var="displayLength" value="${contentLength < maxLength ? contentLength : maxLength}" />
<p>${article.content.substring(0, displayLength)}...</p>
                    </div>
                    <a href="${pageContext.request.contextPath}/articles/view?id=${article.id}" class="read-more">阅读全文 &rarr;</a>
                </div>
            </c:forEach>
        </div>
    </div>

</body>
</html>