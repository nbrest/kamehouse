<%@ page import="java.util.*"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta name="viewport" content="width=device-width">
<meta name="author" content="nbrest">

<title>DragonBallUsers List</title>
<link rel="icon" type="img/ico" href="${pageContext.request.contextPath}/img/favicon.ico" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/lib/css/bootstrap.min.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/jsp/css/app.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/general.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/footer.css" />
</head>
<body>
  <div id="headerContainer"></div>
  <main>
  <div class="container">
    <div class="panel panel-default">
      <div class="panel-heading">
        <span class="lead">List of DragonBall Users</span>
      </div>
    </div>
    <c:set var="dragonBallUsers" scope="page"
      value="${dragonBallUserService.getAllDragonBallUsers()}" />
    <%
      Enumeration<String> paramNames = request.getParameterNames();

    			while (paramNames.hasMoreElements()) {
    				String paramName = (String) paramNames.nextElement();
    				System.out.print(paramName + " : ");
    				String paramValue = request.getParameter(paramName);
    				System.out.println(paramValue);
    			}
    %>
    <div class="tablecontainer">
      <table class="table table-hover table-dragonball-users">
        <thead>
          <tr>
            <th>Id</th>
            <th>Name</th>
            <th>Email</th>
            <th>Age</th>
            <th>Power Level</th>
            <th>Stamina</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          <c:forEach items="${dragonBallUsers}" var="dragonBallUser">
            <tr>
              <td>${dragonBallUser.getId()}</td>
              <td>${dragonBallUser.getUsername()}</td>
              <td>${dragonBallUser.getEmail()}</td>
              <td>${dragonBallUser.getAge()}</td>
              <td>${dragonBallUser.getPowerLevel()}</td>
              <td>${dragonBallUser.getStamina()}</td>
              <td><input type="button" value="edit" class="btn btn-success custom-width"
                  onclick="window.location.href='users-edit-form.jsp?username=${dragonBallUser.getUsername()}'">
                <input type="button" value="delete" class="btn btn-danger custom-width"
                  onclick="window.location.href='users-delete.jsp?id=${dragonBallUser.getId()}'">
              </td>
            </tr>
          </c:forEach>
        </tbody>
      </table>
    </div>
    <input type="button" value="Add DragonBall User" class="btn btn-primary custom-width"
      onclick="window.location.href='users-add-form.jsp'">
  </div>
  </main>
  <div id="footerContainer"></div>
  <script src="${pageContext.request.contextPath}/lib/js/jquery-2.0.3.min.js"></script>
  <script src="${pageContext.request.contextPath}/js/importHeaderFooter.js"></script>
  <script type="text/javascript">importHeaderAndFooter("../../../html/")
  </script>
</body>
</html>
