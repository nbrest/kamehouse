<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page session="true"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta name="viewport" content="width=device-width">
<meta name="author" content="nbrest">
<meta name="mobile-web-app-capable" content="yes">

<title>dragonball ModelAndView Test Endpoint</title>
<link rel="icon" type="img/ico" href="${pageContext.request.contextPath}/img/favicon.ico" />
<script src="/cordova.js"></script>
<script src="/kame-house/lib/js/jquery.js"></script>
<script src="/kame-house/kamehouse/js/kamehouse.js"></script>
<link rel="stylesheet" href="${pageContext.request.contextPath}/lib/css/bootstrap.min.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/kamehouse.css" />
</head>
<body>
  <div class="default-layout main-body"> 
      <h2>dragonball ModelAndView Test Endpoint</h2>
      <h3>name: ${name}</h3>
      <h3>message: ${message}</h3>
      <br>
      Call this view from:
      <a href="/kame-house/api/v1/ui/sample/dragonball/model-and-view?name=gohan">
        Model And View Sample Controller</a>
      to test it with parameters
  </div>
</body>
</html>
