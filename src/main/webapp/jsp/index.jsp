<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.*,java.text.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta name="viewport" content="width=device-width">
<meta name="author" content="nbrest">

<title>kame House - JSP App</title>
<link rel="stylesheet" href="../lib/css/bootstrap.min.css" />
<link rel="stylesheet" href="../css/general.css" />
<link rel="stylesheet" href="../css/header.css" />
<link rel="stylesheet" href="../css/footer.css" />
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
  <section class="dark"> <br>
  </section>
  <div class="container home-links">
    <br>
    <input type="button" value="List DragonBall Users" class="btn btn-basic btn-block custom-width"
      onclick="window.location.href='dragonball/users/users-list.jsp'">
  </div>
  <div id="footerContainer"></div>
  <script src="../lib/js/jquery-2.0.3.min.js"></script>
  <script src="../js/importHeaderFooter.js"></script>
  <script type="text/javascript">importHeaderAndFooter("../html/")
  </script>
</body>
</html>
