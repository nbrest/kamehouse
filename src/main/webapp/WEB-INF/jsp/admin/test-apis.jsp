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

<title>KameHouse - Test APIs</title>
<link rel="icon" type="img/ico" href="${pageContext.request.contextPath}/img/favicon.ico" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/lib/css/bootstrap.min.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/global.css" /> 
</head>
<body>
  <div id="headerContainer"></div>
  <div class="default-layout main-body"> 
    <h3 class="h3-kh">Test APIs</h3> 
    <hr>
    <h5 class="h5-kh">Request Output</h5>
    <div id="api-call-output"></div>
     
    <br><br><h5 class="h5-kh">VLC Load Playlists</h5>
    <input type="button" onclick="executeAdminVlcPost('/kame-house/api/v1/admin/vlc', 'vlc_start', 'D:\\Series\\game_of_thrones\\GameOfThrones.m3u')"
      value="&#128194; Game Of Thrones Windows"
      class="btn btn-outline-primary btn-margins" />
    <input type="button" onclick="executeAdminVlcPost('/kame-house/api/v1/admin/vlc', 'vlc_start', '/home/nbrest/Videos/lleyton.hewitt.m3u')"
      value="&#128194; LH Linux"
      class="btn btn-outline-primary btn-margins" /> 
    
    <br><br><h5 class="h5-kh">VLC Status</h5>
    <input type="button" onclick="executeGet('/kame-house/api/v1/vlc-rc/players/localhost/status')"
      value="VLC-RC Status"
      class="btn btn-outline-info btn-margins btn-borderless" />
    <input type="button" onclick="executeGet('/kame-house/api/v1/admin/vlc')"
      value="VLC Process Status"
      class="btn btn-outline-info btn-margins btn-borderless" /> 
    <br><br><h5 class="h5-kh">/api/v1/dragonball Requests</h5>
    <input type="button" onclick="executeGet('/kame-house/api/v1/dragonball/users')"
      value="/kame-house/api/v1/dragonball/users GET"
      class="btn btn-outline-success" />
    <br><br>
    <input type="button" onclick="executeGet('/kame-house/api/v1/dragonball/users/username/goku')"
      value="/kame-house/api/v1/dragonball/users/username/goku GET"
      class="btn btn-outline-success" />
    <br><br>
  </div>
  <div id="footerContainer"></div>
  <script src="${pageContext.request.contextPath}/lib/js/jquery-2.0.3.min.js"></script>
  <script src="${pageContext.request.contextPath}/js/header-footer/headerFooter.js"></script>
  <script src="${pageContext.request.contextPath}/js/global.js"></script>
  <script src="${pageContext.request.contextPath}/js/admin/test-apis.js"></script>
  <script src="${pageContext.request.contextPath}/js/snippets/api-call-output.js"></script>
</body>
</html>
