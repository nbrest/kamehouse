<%@ page isErrorPage="true"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.*,java.text.*,java.io.*"%>
<!DOCTYPE html>
<html>
<head>
<title>kameHouse 409 Conflict</title>
<link rel="icon" type="img/ico" href="${pageContext.request.contextPath}/img/favicon.ico" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/lib/css/bootstrap.min.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/general.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/footer.css" />
</head>
<body>
    <%
      response.setStatus(409);
    %>
  <div id="headerContainer"></div>
  <main>
  <div class="container main">
    <center>
      <h2>kameHouse 409 Conflict</h2>
    </center>
    <%
      if (exception != null) {
    %>
    Message:
    <%=exception.getMessage()%>

    StackTrace:
    <%
      StringWriter stringWriter = new StringWriter();
    				PrintWriter printWriter = new PrintWriter(stringWriter);
    				exception.printStackTrace(printWriter);
    				out.println(stringWriter);
    				printWriter.close();
    				stringWriter.close();
    			}
    %>
  </div>
  </main>
  <div id="footerContainer"></div>
  <script src="${pageContext.request.contextPath}/lib/js/jquery-2.0.3.min.js"></script>
  <script src="${pageContext.request.contextPath}/js/importHeaderFooter.js"></script>
</body>
</html>