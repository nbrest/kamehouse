<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.*,java.text.*"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width">
<meta name="author" content="nbrest">
<meta name="description" content="kame-house main application">
<meta name="keywords" content="kame-house nicobrest nbrest">

<title>Kame House - Home</title>
<link rel="icon" type="img/ico" href="img/favicon.ico" />
<link rel="stylesheet" href="lib/css/bootstrap.min.css" />
<link rel="stylesheet" href="css/general.css" />
<link rel="stylesheet" href="css/header.css" />
<link rel="stylesheet" href="css/footer.css" />
</head>
<body>
  <div id="headerContainer"></div>
  <section id="banner">
    <div class="container">
      <h1>Kame House - Home</h1>
      <p>Mada mada dane. Kamehame-ha. Pegasus Ryu Sei Ken. Tiger shot. Masenko. Final flash.
        Genki dama. Tsubame gaeshi. Twist serve. Zero shiki drop shot.</p>
    </div>
  </section>
  <section id="newsletter">
    <div class="container">
      <h1>Subscribe to out Newsletter</h1>
      <form>
        <input type="email" placeholder="Enter email" class="form-control form-input" />
        <button type="submit" class="btn btn-danger custom-width"
          onclick="siteUnderCostructionAlert()">Subscribe</button>
      </form>
    </div>
  </section>
  <section id="boxes">
    <div class="container">
      <div class="box">
        <img src="./img/dbz-kamesenin.png" />
        <h3>DBZ</h3>
        <p>Mada mada dane. Echizen kun. Pegasus Seiya</p>
      </div>
      <div class="box">
        <img src="./img/ss-ikki.jpg" />
        <h3>SS</h3>
        <p>Mada mada dane. Echizen kun. Pegasus Seiya</p>
      </div>
      <div class="box">
        <img src="./img/pot-yukimura.jpg" />
        <h3>POT</h3>
        <p>Mada mada dane. Echizen kun. Pegasus Seiya</p>
      </div>
    </div>
  </section>
  <div id="footerContainer"></div>
  <script src="lib/js/jquery-2.0.3.min.js"></script>
  <script src="js/general.js"></script>
  <script src="js/importHeaderFooter.js"></script>
  <script type="text/javascript">importHeaderAndFooter("html/")
  </script>
</body>
</html>
