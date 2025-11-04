<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>注册</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/views/common/header.jsp" />

    <div class="container auth-form">
        <h1>用户注册</h1>
        <c:if test="${not empty requestScope.error}">
            <p class="error-message">${requestScope.error}</p>
        </c:if>
        <form action="${pageContext.request.contextPath}/auth/register" method="post">
            <div class="form-group">
                <label for="username">用户名:</label>
                <input type="text" id="username" name="username" required>
            </div>
            <div class="form-group">
                <label for="password">密码:</label>
                <input type="password" id="password" name="password" required>
            </div>
            <div class="form-group">
                <label for="email">邮箱:</label>
                <input type="email" id="email" name="email" required>
            </div>
            <div class="form-group">
                <button type="submit" class="button primary">注册</button>
            </div>
            <p>已有账户？<a href="${pageContext.request.contextPath}/auth/login">去登录</a></p>
        </form>
    </div>

</body>
</html>