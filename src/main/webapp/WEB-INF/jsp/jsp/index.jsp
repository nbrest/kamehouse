<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.*,java.text.*"%>
<%@ page session="true"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta name="viewport" content="width=device-width">
<meta name="author" content="nbrest">

<title>kame House - JSP App</title>
<link rel="icon" type="img/ico" href="${pageContext.request.contextPath}/img/favicon.ico" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/lib/css/bootstrap.min.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/general.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/footer.css" />
</head>
<body>
  <div id="headerContainer"></div>
  <section id="banner">
  <div class="container">
    <h1>JSP Kame House Homepage</h1>
    <p>Mada mada dane. Kamehame-ha. Pegasus Ryu Sei Ken. Tiger shot. Masenko. Final flash. Genki
      dama. Tsubame gaeshi. Twist serve. Zero shiki drop shot.</p>
  </div>
  </section>
  <div class="container home-links">
    <br>
    <input type="button" value="DragonBall Users" class="btn btn-block btn-outline-secondary custom-width"
      onclick="window.location.href='dragonball/users/users-list'">
  </div>
  <div id="footerContainer"></div>
  <script src="${pageContext.request.contextPath}/lib/js/jquery-2.0.3.min.js"></script>
  <script src="${pageContext.request.contextPath}/js/importHeaderFooter.js"></script>
  <script type="text/javascript">importHeaderAndFooter("/kame-house/html/", "${requestScope.username}")
  </script>
</body>
</html>
