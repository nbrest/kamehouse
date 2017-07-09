<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.*,java.text.*"%>
<!DOCTYPE html>
<html>
<head>
<title>BaseApp Home</title>
<link rel="stylesheet" href="lib/css/bootstrap.min.css" />
<link rel="stylesheet" href="css/general.css" />
<link rel="stylesheet" href="css/header.css" />
<link rel="stylesheet" href="css/main.css" />
<link rel="stylesheet" href="css/footer.css" />
</head>
<body>
  <div id="headerContainer"></div>
  <main>
  <div class="container">
    <input type="button" value="Jsp App" class="btn btn-primary custom-width"
      style="margin-right: 5px" onclick="window.location.href='jsp/'"> <input type="button"
      value="Angular App" class="btn btn-primary custom-width" onclick="window.location.href='app/index.html'">
  </div>
  </main>
  <div id="footerContainer"></div>
  <script src="lib/js/jquery-2.0.3.min.js"></script>
  <script src="js/importHeaderFooter.js"></script>
  <script type="text/javascript">importHeaderAndFooter("html/")
  </script>
</body>
</html>