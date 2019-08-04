<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta charset="utf-8">
<meta name="viewport" content="width=device-width">
<meta name="author" content="nbrest">
<meta name="description" content="kame-house login">
<meta name="keywords" content="kame-house nicobrest nbrest">

<title>KameHouse - Login</title>
<link rel="icon" type="img/ico" href="/kame-house/img/favicon.ico" />
<link href="<c:url value='/lib/css/bootstrap.min.css' />" rel="stylesheet" />
<link rel="stylesheet" type="text/css" href="<c:url value='/lib/css/font-awesome.css' />" />
<link rel="stylesheet" href="/kame-house/css/global.css" /> 
<link rel="stylesheet" href="/kame-house/css/login.css" /> 
</head>
<body>
  <div id="headerContainer"></div>
  <div class="default-layout main-body">
    <c:url var="loginUrl" value="/login" />
    <form action="${loginUrl}" method="post" class="form-horizontal login-form">
      <c:if test="${param.error != null}">
        <div class="alert alert-danger">
          <p>Invalid username and password.</p>
        </div>
      </c:if>
      <c:if test="${param.logout != null}">
        <div class="alert alert-success">
          <p>You have been logged out successfully.</p>
        </div>
      </c:if>
      <div class="input-group input-sm">
        <label class="input-group-addon" for="username">
          <i class="fa fa-user"></i>
        </label>
        <input type="text" class="form-control" id="username" name="username"
          placeholder="Enter Username" required>
      </div>
      <br>
      <div class="input-group input-sm">
        <label class="input-group-addon" for="password">
          <i class="fa fa-lock"></i>
        </label>
        <input type="password" class="form-control" id="password" name="password"
          placeholder="Enter Password" required>
      </div>
      <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
      <br>
      <div class="form-actions">
        <input type="submit" class="btn btn-block btn-outline-info btn-default" value="Log in">
      </div>
    </form>
  </div>
  <div id="footerContainer"></div>
  <script src="/kame-house/lib/js/jquery-2.0.3.min.js"></script>
  <script src="/kame-house/js/global.js"></script>
  <script src="/kame-house/js/header-footer/headerFooter.js"></script>
</body>
</html>