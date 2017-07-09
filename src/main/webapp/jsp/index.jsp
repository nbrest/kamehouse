<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.*,java.text.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>BaseApp JSP App</title>
<link rel="stylesheet" href="../lib/css/bootstrap.min.css" />
<link rel="stylesheet" href="../css/general.css" />
<link rel="stylesheet" href="../css/header.css" />
<link rel="stylesheet" href="../css/main.css" />
<link rel="stylesheet" href="../css/footer.css" />
</head>
<body>
  <div id="headerContainer"></div>
  <main>
  <div class="container">
    <%!/* variable and method declarations */
  Date currentDate = new Date();

  Date getDate() {
    System.out.println("In getDate() method");
    return currentDate;
  }

  void setDate() {
    System.out.println("In setDate() method");
    currentDate = new Date();
  }%>

    <br> The time is now
    <%=new java.util.Date()%>
    <br> <br> <input type="button" value="Home" class="btn btn-basic custom-width"
      onclick="window.location.href='../'"> <input type="button" value="List DragonBallUsers"
      class="btn btn-primary custom-width"
      onclick="window.location.href='dragonball/users/users-list.jsp'">
  </div>
  </main>
  <div id="footerContainer"></div>
  <script src="../lib/js/jquery-2.0.3.min.js"></script>
  <script src="../js/importHeaderFooter.js"></script>
  <script type="text/javascript">importHeaderAndFooter("../html/")
  </script>
</body>
</html>
