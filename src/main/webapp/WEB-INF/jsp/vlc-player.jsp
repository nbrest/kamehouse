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

<title>KameHouse - VLC Player</title>
<link rel="icon" type="img/ico" href="${pageContext.request.contextPath}/img/favicon.ico" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/lib/css/bootstrap.min.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/global.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/vlc-player.css" /> 
</head>
<body>
  <div id="headerContainer"></div>
  <div class="main-body">
  <div class="vlc-player">
    <div id="vlc-player-buttons" class="bg-darker-kh">
    <table class="table-mplayer-btns-kh">
      <tr>
        <td>    
          <input type="button" onclick="executeVlcRcCommandPost('/kame-house/api/v1/vlc-rc/players/localhost/commands', 'pl_previous')"
            value="&#9198;"
            class="btn btn-outline-primary btn-borderless" />
        </td>
        <td>
          <input type="button" onclick="executeVlcRcCommandPost('/kame-house/api/v1/vlc-rc/players/localhost/commands', 'seek', '-1m')"
            value="&#9194;"
            class="btn btn-outline-primary btn-borderless" />
        </td>
        <td>
          <input type="button" onclick="executeVlcRcCommandPost('/kame-house/api/v1/vlc-rc/players/localhost/commands', 'pl_pause')"
            value="&#9199;"
            class="btn btn-outline-primary btn-borderless" />
        </td>
        <td>
          <input type="button" onclick="executeVlcRcCommandPost('/kame-house/api/v1/vlc-rc/players/localhost/commands', 'seek', '+1m')"
            value="&#9193;"
            class="btn btn-outline-primary btn-borderless" />
        </td>
        <td>
          <input type="button" onclick="executeVlcRcCommandPost('/kame-house/api/v1/vlc-rc/players/localhost/commands', 'pl_next')"
            value="&#9197;"
            class="btn btn-outline-primary btn-borderless" />
        </td>
        <td>
          <input type="button" onclick="executeVlcRcCommandPost('/kame-house/api/v1/vlc-rc/players/localhost/commands', 'pl_stop')"
            value="&#9209;"
            class="btn btn-outline-primary btn-borderless" />
        </td>
      </tr>
    </table>
    <table class="table-mplayer-btns-kh">
      <tr>
        <td>
          <input type="button" onclick="executeVlcRcCommandPost('/kame-house/api/v1/vlc-rc/players/localhost/commands', 'volume', '-15')"
            value="&#9836; &#9660;"
            class="btn btn-outline-primary btn-borderless" />
        </td>
        <td>
          <input type="button" onclick="executeVlcRcCommandPost('/kame-house/api/v1/vlc-rc/players/localhost/commands', 'volume', '+15')"
            value="&#9836; &#9650;"
            class="btn btn-outline-primary btn-borderless" />
        </td>
        <td>
          <input type="button" onclick="executeVlcRcCommandPost('/kame-house/api/v1/vlc-rc/players/localhost/commands', 'volume', '0')"
            value="&#128263;"
            class="btn btn-outline-primary btn-borderless" />
        </td>
      </tr>
    </table>
    <table class="table-mplayer-btns-kh">
      <tr> 
        <td>
          <input type="button" onclick="executeVlcRcCommandPost('/kame-house/api/v1/vlc-rc/players/localhost/commands', 'pl_random')"
            value="&#128256;"
            class="btn btn-outline-info btn-borderless" />
        </td>
        <td>
          <input type="button" onclick="executeVlcRcCommandPost('/kame-house/api/v1/vlc-rc/players/localhost/commands', 'pl_loop')"
            value="&#8634; Loop"
            class="btn btn-outline-info btn-borderless" />
        </td>
        <td>
          <input type="button" onclick="executeVlcRcCommandPost('/kame-house/api/v1/vlc-rc/players/localhost/commands', 'pl_repeat')"
            value="&#8635; Repeat"
            class="btn btn-outline-info btn-borderless" />
        </td>
      </tr>
    </table>
    <table class="table-mplayer-btns-kh">
      <tr>
        <td>
          <input type="button" onclick="executeVlcRcCommandPost('/kame-house/api/v1/vlc-rc/players/localhost/commands', 'fullscreen')"
            value="Fullscreen"
            class="btn btn-outline-secondary btn-borderless" />
        </td>
        <td>
          <input type="button" onclick="executeVlcRcCommandPost('/kame-house/api/v1/vlc-rc/players/localhost/commands', 'aspectratio', '16:9')"
            value="16:9"
            class="btn btn-outline-secondary btn-borderless" />
        </td>
        <td>
          <input type="button" onclick="executeVlcRcCommandPost('/kame-house/api/v1/vlc-rc/players/localhost/commands', 'aspectratio', '4:3')"
            value="4:3"
            class="btn btn-outline-secondary btn-borderless" />
        </td>
      </tr>
    </table>
    </div>
     
    <div class="bg-darker-kh mar-5-d-kh mar-5-m-kh">
    <div class="default-layout pd-15-d-kh pd-15-m-kh pls-section"> 
    <select class="select-kh-dark btn-margins" id="playlist-category-dropdown" name="playlist-category" onchange="populateVideoPlaylists()"></select>  
    <select class="select-kh-dark btn-margins" id="playlist-dropdown" name="playlist"></select>  
    <input type="button" onclick="executeAdminVlcPostWithSelectedPlaylist('/kame-house/api/v1/admin/vlc', 'vlc_start')"
      value="Load"
      class="btn btn-outline-primary btn-margins btn-borderless" />
    </div> 
    </div> 
    
    <div class="pd-15-d-kh pd-15-m-kh">
    <div class="default-layout ac-section"> 
    <input type="button" value="Server Management" class="btn btn-outline-secondary btn-margins btn-borderless"
      onclick="window.location.href='/kame-house/admin/server-management'">
    <input type="button" onclick="executeDelete('/kame-house/api/v1/admin/vlc', null)"
      value="Close VLC"
      class="btn btn-outline-danger btn-margins btn-borderless" />
    </div>
    </div>

    <div class="bg-darker-kh pd-15-d-kh pd-15-m-kh">
    <div class="default-layout">
    <h5 class="h5-kh txt-l-d-kh txt-l-m-kh">Status</h5>
    <div id="api-call-output"></div>
    </div>
    </div>
  </div>
  </div>
  <div id="footerContainer"></div>
  <script src="${pageContext.request.contextPath}/lib/js/jquery-2.0.3.min.js"></script>
  <script src="${pageContext.request.contextPath}/js/header-footer/headerFooter.js"></script>
  <script src="${pageContext.request.contextPath}/js/global.js"></script>
  <script src="${pageContext.request.contextPath}/js/vlc-player.js"></script>
  <script src="${pageContext.request.contextPath}/js/snippets/api-call-output.js"></script>
</body>
</html>
