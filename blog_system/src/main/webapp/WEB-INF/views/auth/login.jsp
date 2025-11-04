<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>登录</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/views/common/header.jsp" />

    <div class="container auth-form">
        <h1>用户登录</h1>
        <c:if test="${not empty requestScope.error}">
            <p class="error-message">${requestScope.error}</p>
        </c:if>
        <c:if test="${param.registered == 'true'}">
            <p class="success-message">注册成功，请登录！</p>
        </c:if>
        <form action="${pageContext.request.contextPath}/auth/login" method="post">
            <div class="form-group">
                <label for="username">用户名:</label>
                <input type="text" id="username" name="username" required>
            </div>
            <div class="form-group">
                <label for="password">密码:</label>
                <input type="password" id="password" name="password" required>
            </div>
            <div class="form-group">
                <button type="submit" class="button primary">登录</button>
            </div>
            <p>还没有账户？<a href="${pageContext.request.contextPath}/auth/register">立即注册</a></p>
        </form>
    </div>

</body>
</html>