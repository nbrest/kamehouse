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

<title>kame House - Test APIs</title>
<link rel="icon" type="img/ico" href="${pageContext.request.contextPath}/img/favicon.ico" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/lib/css/bootstrap.min.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/general.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/test-general.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/footer.css" />
</head>
<body>
  <div id="headerContainer"></div>
  <div class="container main">
    <div>
      <h3 id="ehcache-header">Test APIs</h3>
    </div>
    <hr>
    <br><h5>Request Output</h5> <br>
    <div id="api-call-output"></div>
    
    <br><br><h5>/api/v1/vlc-rc/players/localhost/commands Requests</h5><br>
    <input type="button" onclick="executeVlcRcCommandPost('/kame-house/api/v1/vlc-rc/players/localhost/commands', 'pl_previous')"
      value="VLC play prev"
      class="btn btn-outline-success" />
    <input type="button" onclick="executeVlcRcCommandPost('/kame-house/api/v1/vlc-rc/players/localhost/commands', 'pl_next')"
      value="VLC play next"
      class="btn btn-outline-success" />
    <br><br>
    <input type="button" onclick="executeVlcRcCommandPost('/kame-house/api/v1/vlc-rc/players/localhost/commands', 'pl_random')"
      value="VLC random on/off"
      class="btn btn-outline-success" /> 
    <br><br><h5>/api/v1/admin/vlc Requests</h5><br>
    <select class="custom-select sources" id="playlist-category-dropdown" name="playlist-category" onchange="populateVideoPlaylists()"></select> 
    <br><br>
    <select class="custom-select sources" id="playlist-dropdown" name="playlist"></select> 
    <br><br>  
    <input type="button" onclick="executeAdminVlcPostWithSelectedPlaylist('/kame-house/api/v1/admin/vlc', 'start')"
      value="Start VLC with selected playlist"
      class="btn btn-outline-primary" />
    <br><br>
    <input type="button" onclick="executeGet('/kame-house/api/v1/admin/vlc')"
      value="Get VLC status"
      class="btn btn-outline-success" />
    <br><br>
    <input type="button" onclick="executeAdminVlcPost('/kame-house/api/v1/admin/vlc', 'start', 'D:\\Series\\game_of_thrones\\GameOfThrones.m3u')"
      value="Start VLC with Win GoT pls"
      class="btn btn-outline-primary" />
    <br><br> 
    <input type="button" onclick="executeAdminVlcPost('/kame-house/api/v1/admin/vlc', 'start', '/home/nbrest/Videos/lleyton.hewitt.m3u')"
      value="Start VLC with Linux LH pls"
      class="btn btn-outline-primary" />
    <br><br>
    <input type="button" onclick="executeDelete('/kame-house/api/v1/admin/vlc', null)"
      value="Stop VLC player"
      class="btn btn-outline-danger" />
      
    <br><br><h5>/api/v1/admin/shutdown Requests</h5><br>
    <input type="button" onclick="executeGet('/kame-house/api/v1/admin/shutdown')"
      value="Get Shutdown status"
      class="btn btn-outline-warn" />
    <br><br>
    <input type="button" onclick="executeAdminShutdownPost('/kame-house/api/v1/admin/shutdown', 'set', 5400)"
      value="Set Shutdown in 90 min"
      class="btn btn-outline-danger" /> 
    <br><br>
    <input type="button" onclick="executeAdminShutdownPost('/kame-house/api/v1/admin/shutdown', 'set', 60)"
      value="Set Shutdown in 1 min"
      class="btn btn-outline-danger" />
    <br><br>
    <input type="button" onclick="executeDelete('/kame-house/api/v1/admin/shutdown', null)"
      value="Cancel Shutdown"
      class="btn btn-outline-success" />
    
    <br><br><h5>/api/v1/admin/unlock-screen Request</h5><br>
    <input type="button" onclick="executePost('/kame-house/api/v1/admin/lock-screen', null)"
      value="Lock Screen"
      class="btn btn-outline-warn" />
    <br><br>
    <input type="button" onclick="executePost('/kame-house/api/v1/admin/unlock-screen', null)"
      value="Unlock Screen"
      class="btn btn-outline-warn" />
    
    <br><br><h5>/api/v1/media/video/playlists Requests</h5><br>
    <input type="button" onclick="executeGet('/kame-house/api/v1/media/video/playlists')"
      value="/kame-house/api/v1/media/video/playlists GET"
      class="btn btn-outline-success" />   
    
    <br><br><h5>/api/v1/dragonball Requests</h5><br>
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
  <script src="${pageContext.request.contextPath}/js/importHeaderFooter.js"></script>
  <script src="${pageContext.request.contextPath}/js/admin.test.apis.js"></script>
</body>
</html>
