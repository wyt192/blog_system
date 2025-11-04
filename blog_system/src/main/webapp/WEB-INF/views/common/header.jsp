<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<header class="header">
    <div class="container">
        <div class="logo">
            <a href="${pageContext.request.contextPath}/">我的博客</a>
        </div>
        <nav class="nav">
            <ul>
                <li><a href="${pageContext.request.contextPath}/articles/list">所有文章</a></li>
                <c:choose>
                    <c:when test="${sessionScope.currentUser != null}">
                        <li><a href="${pageContext.request.contextPath}/articles/create">发布文章</a></li>
                        <li><span>欢迎, ${sessionScope.currentUser.username}</span></li>
                        <li><a href="${pageContext.request.contextPath}/auth/logout">退出登录</a></li>
                    </c:when>
                    <c:otherwise>
                        <li><a href="${pageContext.request.contextPath}/auth/login">登录</a></li>
                        <li><a href="${pageContext.request.contextPath}/auth/register">注册</a></li>
                    </c:otherwise>
                </c:choose>
            </ul>
        </nav>
    </div>
</header>