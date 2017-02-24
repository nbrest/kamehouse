<%@ page import="java.util.*"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>DragonBallUsers List</title>
</head>
<body>
  <h1>DragonBallUsers List</h1>

  <c:set var="dragonBallUsers" scope="page" value="${dragonBallUserService.getAllDragonBallUsers()}" />
  <%
    Enumeration<String> paramNames = request.getParameterNames();

    while (paramNames.hasMoreElements()) {
      String paramName = (String) paramNames.nextElement();
      System.out.print(paramName + " : ");
      String paramValue = request.getParameter(paramName);
      System.out.println(paramValue);
    }
  %>
  <table border="1" width="90%">
    <tr>
      <th>Id</th>
      <th>Username</th>
      <th>Email</th>
      <th>Age</th>
      <th>PowerLevel</th>
      <th>Stamina</th>
      <th>Edit</th>
      <th>Delete</th>
    </tr>
    <c:forEach items="${dragonBallUsers}" var="dragonBallUser">
      <tr>
        <td>${dragonBallUser.getId()}</td>
        <td>${dragonBallUser.getUsername()}</td>
        <td>${dragonBallUser.getEmail()}</td>
        <td>${dragonBallUser.getAge()}</td>
        <td>${dragonBallUser.getPowerLevel()}</td>
        <td>${dragonBallUser.getStamina()}</td>
        <td><a href="users-edit-form.jsp?username=${dragonBallUser.getUsername()}">Edit</a></td>
        <td><a href="users-delete.jsp?id=${dragonBallUser.getId()}">Delete</a></td>
      </tr>
    </c:forEach>
  </table>
  <br />
  <a href="users-add-form.jsp">Add New DragonBallUser</a><br/>
  <a href="../../">Go home</a>
</body>
</html>
