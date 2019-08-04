<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.*,java.text.*"%>
<%@ page session="true"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta name="viewport" content="width=device-width">
<meta name="author" content="nbrest">

<title>KameHouse - Test Module - JSP</title>
<link rel="icon" type="img/ico" href="${pageContext.request.contextPath}/img/favicon.ico" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/lib/css/bootstrap.min.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/global.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/header-footer/header.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/header-footer/footer.css" />
</head>
<body>
  <div id="headerContainer"></div>
  <div class="main-body">
  <div id="banner" class="banner-sunset">
  <div class="default-layout banner-text">
    <h1>JSP</h1>
    <p>JSP test application in the Test Module</p>
  </div>
  </div>
  <div class="default-layout landing-p-links">
    <br>
    <input type="button" value="DragonBall Users" class="btn btn-block btn-outline-secondary"
      onclick="window.location.href='dragonball/users/users-list'">
  </div>
  </div>
  <div id="footerContainer"></div>
  <script src="${pageContext.request.contextPath}/lib/js/jquery-2.0.3.min.js"></script>
  <script src="${pageContext.request.contextPath}/js/header-footer/headerFooter.js"></script>
</body>
</html>
