<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
    <table>
        <tr>
            <th>ID</th>
            <th>姓名</th>
            <th>年龄</th>
            <th>权限</th>
        </tr>
        <c:forEach items="${userList}" var="users">
        <tr>
                <td>${users.id}</td>
                <td>${users.name}</td>
                <td>${users.age}</td>
                <c:if test="${users.permission==1}"><td>管理员</td></c:if>
                <c:if test="${users.permission==0}"><td>用户</td></c:if>

        </tr>
        </c:forEach>

    </table>
</body>
</html>