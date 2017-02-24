<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Add User Form</title>
</head>
<body>

  <h1>Add New User</h1>
  <form action="users-add-action.jsp" method="post">
    <table>
      <tr>
        <td>Username:</td>
        <td><input type="text" name="username" /></td>
      </tr>
      <tr>
        <td>Email:</td>
        <td><input type="email" name="email" value="@dbz.com" /></td>
      </tr>
      <tr>
        <td>Age:</td>
        <td><input type="text" name="age" value="1" /></td>
      </tr>
      <tr>
        <td>Power Level:</td>
        <td><input type="text" name="powerLevel" value="1" /></td>
      </tr>
      <tr>
        <td>Stamina:</td>
        <td><input type="text" name="stamina" value="1" /></td>
      </tr>
      <tr>
        <td colspan="2"><input type="submit" value="Add User" /></td>
      </tr>
    </table>
  </form>

  <a href="users-list.jsp">View All DragonBallUsers</a>
  <br />
  <a href="../../">Go home</a>
</body>
</html>
