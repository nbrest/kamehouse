<%@ page isErrorPage="true"%>
<%@ page import="java.util.*"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>DragonBallUser Add Error</title>
</head>
<body>
  <p>Sorry, an error occurred when processing your request.</p>

  <a href="users-list.jsp">Go to users list</a>
  <br />
  <a href="../../">Go home</a>

  <br />
  Status:
  <%=response.getStatus()%>
  <br />
  Message:
  <br />
  <%=exception.getMessage()%>
  <br />
  Stack trace:
  <br />
  <%
    StackTraceElement[] stackTraceArray = exception.getStackTrace();
    for (int i = 0; i < stackTraceArray.length; i++) {
      out.println(stackTraceArray[i].toString());
    }
  %>
</body>
</html>