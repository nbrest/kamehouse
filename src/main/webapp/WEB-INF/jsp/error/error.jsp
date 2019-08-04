<%@ page isErrorPage="true"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.*,java.text.*,java.io.*"%>
<!DOCTYPE html>
<html>
<head>
<title>kameHouse 500 Server Error</title>
<link rel="icon" type="img/ico" href="${pageContext.request.contextPath}/img/favicon.ico" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/lib/css/bootstrap.min.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/global.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/header-footer/header.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/header-footer/footer.css" />
</head>
<body>
    <%
      response.setStatus(500);
    %>
  <div id="headerContainer"></div> 
  <div class="default-layout main-body">
    <center>
      <h2>kameHouse 500 Server Error</h2>
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
  <div id="footerContainer"></div>
  <script src="${pageContext.request.contextPath}/lib/js/jquery-2.0.3.min.js"></script>
  <script src="${pageContext.request.contextPath}/js/header-footer/headerFooter.js"></script>
</body>
</html>