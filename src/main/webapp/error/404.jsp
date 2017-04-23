<%@ page isErrorPage="true" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.*,java.text.*"%>
<!DOCTYPE html>
<html>
<head>
<title>MobileInspections 404 Not Found</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/lib/css/bootstrap.min.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/general.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/footer.css" />
</head>
<body>
  <div id="headerContainer"></div>
  <main>
  <div class="container">
    <center>
      <h2>MobileInspections 404 Not Found</h2>
    </center>
    <% if (exception != null) {%>
      <%= exception.getMessage() %><br>
      <%exception.printStackTrace();
    }%>
  </div>
  </main>
  <div id="footerContainer"></div>
  <script src="${pageContext.request.contextPath}/lib/js/jquery-2.0.3.min.js"></script>
  <script src="${pageContext.request.contextPath}/js/importHeaderFooter.js"></script>
  <script type="text/javascript">importHeaderAndFooter("${pageContext.request.contextPath}/html/")
  </script>
</body>
</html>