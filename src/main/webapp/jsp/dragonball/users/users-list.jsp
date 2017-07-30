<%@ page import="java.util.*"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>DragonBallUsers List</title>
<link rel="stylesheet" href="../../../lib/css/bootstrap.min.css" />
<link rel="stylesheet" href="../../css/app.css" />
<link rel="stylesheet" href="../../../css/general.css" />
<link rel="stylesheet" href="../../../css/header.css" />
<link rel="stylesheet" href="../../../css/main.css" />
<link rel="stylesheet" href="../../../css/footer.css" />
</head>
<body>
  <div id="headerContainer"></div>
  <main>
  <div class="container">
    <div class="panel panel-default">
      <div class="panel-heading">
        <span class="lead">List of DragonBallUsers</span>
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
      <table class="table table-hover">
        <thead>
          <tr>
            <th>Id</th>
            <th>Username</th>
            <th>Email</th>
            <th>Age</th>
            <th>PowerLevel</th>
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
    <input type="button" value="Jsp Home" class="btn btn-basic custom-width"
      style="margin-right: 5px" onclick="window.location.href='../../'">
    <input type="button" value="Add DragonBallUser" class="btn btn-primary custom-width"
      onclick="window.location.href='users-add-form.jsp'">
  </div>
  </main>
  <div id="footerContainer"></div>
  <script src="../../../lib/js/jquery-2.0.3.min.js"></script>
  <script src="../../../js/importHeaderFooter.js"></script>
  <script type="text/javascript">importHeaderAndFooter("../../../html/")
  </script>
</body>
</html>
