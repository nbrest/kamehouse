<%@ page import="java.util.*"%>
<%@ page import="ar.com.nicobrest.mobileinspections.service.DragonBallUserService"%>
<%@ page import="ar.com.nicobrest.mobileinspections.model.DragonBallUser"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Edit Form</title>
</head>
<body>
  <%-- TODO: I had to use the username as parameter because the id is of type Long, and
  since the method getDragonBallUser is overloaded for strings and longs, when I passed
  the id from the jsp, the method was invoked with a String parameter searching by the
  username instead of a Long parameter searching by id.
  See how I can do to pass the id so it is recognized as a Long when the method is overloaded --%>
  <c:set var="username" value="${param.username}"></c:set>
  <c:set var="dragonBallUser" value="${dragonBallUserService.getDragonBallUser(username)}"></c:set>

  <h1>Edit Form</h1>
  <form action="users-edit-action.jsp" method="post">
    <input type="hidden" name="id" value="${dragonBallUser.getId()}" />
    <table>
      <tr>
        <td>Username:</td>
        <td><input type="text" name="username" value="${dragonBallUser.getUsername()}" /></td>
      </tr>
      <tr>
        <td>Email:</td>
        <td><input type="email" name="email" value="${dragonBallUser.getEmail()}" /></td>
      </tr>
      <tr>
        <td>Age:</td>
        <td><input type="text" name="age" value="${dragonBallUser.getAge()}" /></td>
      </tr>
      <tr>
        <td>Power Level:</td>
        <td><input type="text" name="powerLevel" value="${dragonBallUser.getPowerLevel()}" /></td>
      </tr>
      <tr>
        <td>Stamina:</td>
        <td><input type="text" name="stamina" value="${dragonBallUser.getStamina()}" /></td>
      </tr>
      <tr>
        <td colspan="2"><input type="submit" value="Edit User" /></td>
      </tr>
    </table>
  </form>

</body>
</html>
