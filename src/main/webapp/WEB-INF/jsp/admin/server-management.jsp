<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.*,java.text.*"%>
<%@ page session="true"%>
<!DOCTYPE html>
<html>
<head>
<meta name="_csrf" content="${_csrf.token}" />
<meta name="_csrf_header" content="${_csrf.headerName}" />
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta name="viewport" content="width=device-width">
<meta name="author" content="nbrest">

<title>KameHouse - Server Management</title>
<link rel="icon" type="img/ico" href="${pageContext.request.contextPath}/img/favicon.ico" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/lib/css/bootstrap.min.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/general.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/server-management.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/footer.css" />
</head>
<body>
  <div id="headerContainer"></div>
  <div class="container main server-management">
    <h3 class="h3-kh">Server Management</h3>
    <hr>
    
    <h5 class="h5-kh">Screen:</h5>
    <input type="button" onclick="executePost('/kame-house/api/v1/admin/screen/unlock', null)"
      value="Unlock"
      class="btn btn-outline-success btn-margins" />
    
    <input type="button" onclick="executePost('/kame-house/api/v1/admin/screen/wake-up', null)"
      value="Wake-Up"
      class="btn btn-outline-warning btn-margins" />
      
    <input type="button" onclick="executePost('/kame-house/api/v1/admin/screen/lock', null)"
      value="Lock"
      class="btn btn-outline-danger btn-margins" />
    
    <hr>  
    <h5 class="h5-kh">Power Management:</h5>
    <h6 class="h6-kh">Shutdown:</h6>
    <input type="button" onclick="executeAdminShutdownPost('/kame-house/api/v1/admin/power-management/shutdown', 'shutdown_set', 5400)"
      value="90 Min"
      class="btn btn-outline-danger btn-margins" /> 
    
    <input type="button" onclick="executeAdminShutdownPost('/kame-house/api/v1/admin/power-management/shutdown', 'shutdown_set', 60)"
      value="1 Min"
      class="btn btn-outline-danger btn-margins" />

    <input type="button" onclick="executeDelete('/kame-house/api/v1/admin/power-management/shutdown', null)"
      value="Cancel"
      class="btn btn-outline-success btn-margins" />
     
     <input type="button" onclick="executeGet('/kame-house/api/v1/admin/power-management/shutdown')"
      value="Status"
      class="btn btn-outline-primary btn-margins" />
    
    <h6 class="h6-kh">Suspend:</h6>
    <input type="button" onclick="executePost('/kame-house/api/v1/admin/power-management/suspend', null)"
      value="Now"
      class="btn btn-outline-danger btn-margins" />
      
    <hr>
    <h5 class="h5-kh">Command output:</h5>
    <div id="api-call-output"></div>

  </div>
  <div id="footerContainer"></div>
  <script src="${pageContext.request.contextPath}/lib/js/jquery-2.0.3.min.js"></script>
  <script src="${pageContext.request.contextPath}/js/importHeaderFooter.js"></script>
  <script src="${pageContext.request.contextPath}/js/general.js"></script>
  <script src="${pageContext.request.contextPath}/js/admin.server.management.js"></script>
</body>
</html>
