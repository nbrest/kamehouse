<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.*,java.text.*"%>
<%@ page session="true"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta name="viewport" content="width=device-width">
<meta name="author" content="nbrest">

<title>kame House - Admin</title>
<link rel="icon" type="img/ico" href="/kame-house/img/favicon.ico" />
<link rel="stylesheet" href="/kame-house/lib/css/bootstrap.min.css" />
<link rel="stylesheet" href="/kame-house/css/general.css" />
<link rel="stylesheet" href="/kame-house/css/header.css" />
<link rel="stylesheet" href="/kame-house/css/footer.css" />
</head>
<body>
  <div id="headerContainer"></div>
  <section id="banner">
  <div class="container">
    <h1>Kame House - Admin</h1>
    <p>Manage configurations of the website</p>
  </div>
  </section>
  <div class="container home-links">
    <br>
    <input type="button" value="EhCache" class="btn btn-block btn-outline-secondary custom-width"
      onclick="window.location.href='ehcache'">
  </div>
  <div id="footerContainer"></div>
  <script src="/kame-house/lib/js/jquery-2.0.3.min.js"></script>
  <script src="/kame-house/js/importHeaderFooter.js"></script>
  <script type="text/javascript">importHeaderAndFooter("/kame-house/html/", "${requestScope.username}")
  </script>
</body>
</html>
