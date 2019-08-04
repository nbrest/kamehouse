<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.*,java.text.*"%>
<%@ page session="true"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width">
<meta name="author" content="nbrest">
<meta name="description" content="kame-house main application">
<meta name="keywords" content="kame-house nicobrest nbrest">

<title>KameHouse - Home</title>
<link rel="icon" type="img/ico" href="/kame-house/img/favicon.ico" />
<link rel="stylesheet" href="/kame-house/lib/css/bootstrap.min.css" />
<link rel="stylesheet" href="/kame-house/css/general.css" />
<link rel="stylesheet" href="/kame-house/css/header.css" />
<link rel="stylesheet" href="/kame-house/css/footer.css" />
<link rel="stylesheet" href="/kame-house/css/home.css" />
</head>
<body>
  <div id="headerContainer"></div>
  <div id="banner" class="banner-space">
    <div class="container banner-text">
      <h1>KameHouse</h1>
      <p>Mada mada dane :)</p>
    </div>
  </div>
  <div class="bg-lighter-kh">
    <br><br>
  </div>
  <div class="container">
  <div id="boxes">
      <div class="box">
        <a href="/kame-house/vlc-player"><img src="./img/dbz/kamesenin-logo.png" /></a>
        <h3>VLC Player</h3>
        <p>Control multiple VLC Players</p>
      </div>
      <div class="box">
        <a href="/kame-house/test-module"><img src="./img/dbz/kaio-sama-logo.png" /></a>
        <h3>Test Module</h3>
        <p>Test db on the test module</p>
      </div>
      <div class="box">
        <a href="/kame-house/contact-us"><img src="./img/dbz/goku-go-logo.png" /></a>
        <h3>Contact Us</h3>
        <p>Let us know your thoughts!</p>
      </div> 
  </div>
  </div>
  <div id="newsletter" ></div>
  <div id="footerContainer"></div>
  <script src="/kame-house/lib/js/jquery-2.0.3.min.js"></script>
  <script src="/kame-house/js/general.js"></script>
  <script src="/kame-house/js/importHeaderFooter.js"></script>
</body>
</html>
