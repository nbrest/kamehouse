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
  <div class="main-body ">
  <div class="vlc-player">
    <div id="vlc-player-main" class="bg-darker-kh">
    <div class="bg-darker-kh"><br></div>
    <div id="media-title" class="bg-default-kh pd-15-d-kh">No media loaded</div>
    <div id="media-time" class="bg-default-kh">
      <table>
      <tr>
      <td>
        <span id="current-time" class="fl-l-d-kh fl-l-m-kh">--:--:--</span>
      </td>
      <td>
        <span id="total-time" class="fl-r-d-kh fl-r-m-kh">--:--:--</span>
      </td>
      </tr>
      </table>
      <table>
      <tr>
      <td>
        <div id="time-slider-wrapper"><input type="range" min="0" max="1000" value="500" id="time-slider" onmouseup="setTimeFromSlider(this.value)" ontouchend="setTimeFromSlider(this.value)"></div> 
      </td>
      </tr>
      </table>
    </div>
    <div class="bg-darker-kh"><br></div>
    <table class="table-mplayer-btns-kh">
      <tr>
        <td>    
           <img src="/kame-house/img/mplayer/previous.png" onclick="executeVlcRcCommandPost('/kame-house/api/v1/vlc-rc/players/localhost/commands', 'pl_previous')"/> 
        </td>
        <td> 
           <img src="/kame-house/img/mplayer/rewind.png" onclick="executeVlcRcCommandPost('/kame-house/api/v1/vlc-rc/players/localhost/commands', 'seek', '-1m')"/> 
        </td>
        <td> 
          <img src="/kame-house/img/mplayer/resume.png" onclick="executeVlcRcCommandPost('/kame-house/api/v1/vlc-rc/players/localhost/commands', 'pl_pause')"/> 
        </td>
        <td> 
          <img src="/kame-house/img/mplayer/fast-forward.png" onclick="executeVlcRcCommandPost('/kame-house/api/v1/vlc-rc/players/localhost/commands', 'seek', '+1m')"/> 
        </td>
        <td> 
          <img src="/kame-house/img/mplayer/next.png" onclick="executeVlcRcCommandPost('/kame-house/api/v1/vlc-rc/players/localhost/commands', 'pl_next')"/> 
        </td>
        <td> 
          <img src="/kame-house/img/mplayer/stop.png" onclick="executeVlcRcCommandPost('/kame-house/api/v1/vlc-rc/players/localhost/commands', 'pl_stop')"/> 
        </td>
      </tr>
    </table>
    <table id="audio-controls" class="table-mplayer-btns-kh"> 
      <colgroup>
        <col class="w-10-pc-kh" />
        <col class="w-10-pc-kh" />
        <col class="w-60-pc-kh" />
        <col class="w-10-pc-kh" />
        <col class="w-10-pc-kh" /> 
      </colgroup>
      <tr>
      <td> 
	      <img class="btn-audio" src="/kame-house/img/mplayer/mute-red-dark.png" onclick="executeVlcRcCommandPost('/kame-house/api/v1/vlc-rc/players/localhost/commands', 'volume', '0')"/> 
	    </td>  
      <td>   
        <img class="btn-audio" src="/kame-house/img/mplayer/audio-down-gray-dark.png" onclick="executeVlcRcCommandPost('/kame-house/api/v1/vlc-rc/players/localhost/commands', 'volume', '-15')"/> 
	    </td> 
      <td>  
        <div id="volume-slider-wrapper"><input type="range" min="0" max="512" value="256" id="volume-slider" onmouseup="setVolumeFromSlider(this.value)"  ontouchend="setVolumeFromSlider(this.value)"></div> 
	    </td>
      <td>   
        <img class="btn-audio" src="/kame-house/img/mplayer/audio-up-gray-dark.png" onclick="executeVlcRcCommandPost('/kame-house/api/v1/vlc-rc/players/localhost/commands', 'volume', '+15')"/>           
	    </td>
      <td>  
        <div id="current-volume"></div>
      </td>
      </tr> 
    </table>
    <table class="table-mplayer-btns-kh">
      <tr> 
        <td> 
          <img src="/kame-house/img/mplayer/shuffle-green.png" onclick="executeVlcRcCommandPost('/kame-house/api/v1/vlc-rc/players/localhost/commands', 'pl_random')"/> 
        </td>
        <td> 
          <img src="/kame-house/img/mplayer/repeat-1-green.png" onclick="executeVlcRcCommandPost('/kame-house/api/v1/vlc-rc/players/localhost/commands', 'pl_repeat')"/>
        </td>
        <td> 
          <img src="/kame-house/img/mplayer/repeat-green.png" onclick="executeVlcRcCommandPost('/kame-house/api/v1/vlc-rc/players/localhost/commands', 'pl_loop')"/>
        </td>
        <td> 
          <img src="/kame-house/img/mplayer/fullscreen-blue.png" onclick="executeVlcRcCommandPost('/kame-house/api/v1/vlc-rc/players/localhost/commands', 'fullscreen')"/>
        </td>
        <td> 
          <img src="/kame-house/img/mplayer/aspect-ratio-16-9-blue.png" onclick="executeVlcRcCommandPost('/kame-house/api/v1/vlc-rc/players/localhost/commands', 'aspectratio', '16:9')"/> 
        </td>
        <td> 
          <img src="/kame-house/img/mplayer/aspect-ratio-4-3-blue.png" onclick="executeVlcRcCommandPost('/kame-house/api/v1/vlc-rc/players/localhost/commands', 'aspectratio', '4:3')"/> 
        </td>
      </tr>
    </table>
    </div>
     
    <div class="bg-darker-kh mar-5-d-kh mar-5-m-kh">
    <div class="default-layout pd-15-d-kh pd-15-m-kh pls-section"> 
    <select class="select-kh-dark btn-margins" id="playlist-category-dropdown" name="playlist-category" onchange="populateVideoPlaylists()"></select>  
    <select class="select-kh-dark btn-margins" id="playlist-dropdown" name="playlist"></select>  
    <button onclick="executeAdminVlcPostWithSelectedPlaylist('/kame-house/api/v1/admin/vlc', 'vlc_start')"
      class="btn btn-outline-primary btn-margins btn-borderless" >
      Load <img class="btn-img" src="/kame-house/img/mplayer/playlist-blue.png"/>
    </button>
    </div> 
    </div> 
    
    <div class="pd-15-d-kh pd-15-m-kh">
    <div class="default-layout ac-section"> 
    <button class="btn btn-outline-secondary btn-margins btn-borderless"
      onclick="window.location.href='/kame-house/admin/server-management'">
      <img class="btn-img" src="/kame-house/img/pc/server-gray.png"/> Management  
    </button>
    <button onclick="executeDelete('/kame-house/api/v1/admin/vlc', null)" 
      class="btn btn-outline-danger btn-margins btn-borderless">
      Close <img class="btn-img" src="/kame-house/img/mplayer/vlc-red.png"/>
    </button>
    </div>
    </div>

    <div id="debug-status" class="bg-darker-kh pd-15-d-kh pd-15-m-kh hidden-kh">
    <div class="default-layout">
    <h5 class="h5-kh txt-l-d-kh txt-l-m-kh">Debug Status</h5>
    <div id="api-call-output"></div>
    </div>
    </div>
  </div>
  </div>
  <div id="footerContainer"></div>
  <script src="${pageContext.request.contextPath}/lib/js/jquery-2.0.3.min.js"></script>
  <script src="/kame-house/lib/js/sockjs.min.js"></script>
  <script src="/kame-house/lib/js/stomp.min.js"></script>
  <script src="${pageContext.request.contextPath}/js/header-footer/headerFooter.js"></script>
  <script src="${pageContext.request.contextPath}/js/global.js"></script>
  <script src="${pageContext.request.contextPath}/js/vlc-player.js"></script>
  <script src="${pageContext.request.contextPath}/js/snippets/api-call-output.js"></script>
</body>
</html>
