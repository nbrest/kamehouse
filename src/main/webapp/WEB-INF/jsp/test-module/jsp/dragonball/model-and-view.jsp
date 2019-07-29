<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page session="true"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta name="viewport" content="width=device-width">
<meta name="author" content="nbrest">

<title>dragonball ModelAndView Test Endpoint</title>
<link rel="icon" type="img/ico" href="${pageContext.request.contextPath}/img/favicon.ico" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/lib/css/bootstrap.min.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/general.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/footer.css" />
</head>
<body>
  <div id="headerContainer"></div>
  <div class="container main">
    <center>
      <h2>dragonball ModelAndView Test Endpoint</h2>
      <h2>${message}${name}</h2>
    </center>
  </div>
  <div id="footerContainer"></div>
  <script src="${pageContext.request.contextPath}/lib/js/jquery-2.0.3.min.js"></script>
  <script src="${pageContext.request.contextPath}/js/importHeaderFooter.js"></script>
</body>
</html>
