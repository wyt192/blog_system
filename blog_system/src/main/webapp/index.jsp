<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>欢迎来到我的博客</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/views/common/header.jsp" />

    <div class="container">
        <h1>最新文章</h1>
        <p>这里将展示博客的最新文章概览。</p>
        <p>点击 <a href="${pageContext.request.contextPath}/articles/list">这里</a> 查看所有文章。</p>

        <!-- 可以加入一些特色文章或统计信息 -->
        <div class="feature-section">
            <h2>特色功能</h2>
            <ul>
                <li>注册/登录账户，开始发布你的文章！</li>
                <li>评论其他用户的文章。</li>
                <li>管理（编辑/删除）你自己的文章和评论。</li>
            </ul>
        </div>
    </div>

</body>
</html>