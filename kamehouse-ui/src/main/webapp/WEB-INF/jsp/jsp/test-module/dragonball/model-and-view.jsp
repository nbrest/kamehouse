<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page session="true"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta name="viewport" content="width=device-width">
<meta name="author" content="nbrest">
<meta name="mobile-web-app-capable" content="yes">

<title>dragonball ModelAndView JSP</title>
<link rel="icon" type="img/ico" href="${pageContext.request.contextPath}/img/favicon.ico" />
<script src="/cordova.js"></script>
<script src="/kame-house/lib/js/jquery.js"></script>
<script src="/kame-house/kamehouse/js/kamehouse.js" id="kamehouse-data"></script>
<link rel="stylesheet" href="${pageContext.request.contextPath}/lib/css/bootstrap.min.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/kamehouse/css/kamehouse.css" />
</head>
<body>
  <div class="main-body">
    <div class="banner-wrapper">
      <div id="banner" class="fade-in-out-15s banner-ancient-era-warriors">
        <div class="default-layout banner-text">
          <h1>Dragonball ModelAndView JSP</h1>
        </div>
      </div>
    </div>
    <div class="default-layout">
      <br> 
      <table class="table-kh">
        <caption class="hidden-kh">Caption</caption>
        <thead class="hidden-kh"><tr><th id="header-row">Header</th></tr></thead>
        <tbody>
        <tr>
          <td class="table-kh-header">Name: </td>
          <td>${name}</td>
        </tr>
        <tr>
          <td class="table-kh-header">Message</td>
          <td>${message}</td>
        </tr>
        </tbody>
      </table>
      <br>
      Call this view from:
      <a href="/kame-house/api/v1/ui/sample/dragonball/model-and-view?name=gohan">
        Model And View Sample Controller</a>
      to test it with parameters
    </div>
  </div>
  <span id="debug-mode-wrapper"></span>
</body>
</html>
