<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="java.util.*,java.text.*"%>
<%@ page session="true"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta name="viewport" content="width=device-width">
<meta name="author" content="nbrest">
<meta name="mobile-web-app-capable" content="yes">

<title>KameHouse - Test Module - JSP</title>
<link rel="icon" type="img/ico" href="${pageContext.request.contextPath}/img/favicon.ico" />
<script src="/cordova.js"></script>
<script src="/kame-house/lib/js/jquery.js"></script>
<script src="/kame-house/kamehouse/js/kamehouse.js" id="kamehouse-data"></script>
<script src="/kame-house/js/jsp/test-module/index.js"></script>
<link rel="stylesheet" href="${pageContext.request.contextPath}/lib/css/bootstrap.min.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/kamehouse/css/kamehouse.css" />
</head>
<body>
  <div class="main-body">
  <div class="banner-wrapper">
  <div id="banner" class="fade-in-out-15s banner-athena-saints">
    <div class="default-layout banner-text">
      <h1>JSP</h1>
    </div>
  </div>
  </div>
  <div class="default-layout">
    <div class="info-image-wrapper-m-80-60">
      <table class="info-image-table">
        <caption class="hidden-kh">Image-Info</caption>
        <thead class="hidden-kh"><tr><th>Image-Info</th></tr></thead>
        <tbody>
          <tr>
            <td class="info-image-img">
              <img src="/kame-house/img/banners/saint-seiya/banner-ancient-era-warriors.jpg" alt="info img"/>
            </td>
            <td class="info-image-info">
              <div class="info-image-title info-image-title-bottom">
                KameHouse JSPs
              </div>
              <div class="info-image-desc">
                <p>Ancient era JSP test application in the Test Module</p>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  
    <div class="link-image-wrapper-m-80-60 link-image-wrapper-w-80">
      <table class="link-image-table">
        <caption class="hidden-kh">Image-Links</caption>
        <thead class="hidden-kh"><tr><th>Image-Links</th></tr></thead>
        <tbody>
          <tr>
            <td>
              <a><img class="link-image-img" src="/kame-house/img/dbz/gohan-ssj2-icon.png" alt="Kame Senin Logo" onclick="kameHouse.core.windowLocation('dragonball/users/users-list')"/></a>
            </td>
            <td>
              <div class="link-image-text">DragonBall Users</div>
              <div class="link-image-desc">Control the dragonball users registered in <span class="bold-kh">KameHouse</span></div>
            </td>
          </tr>
          <tr>
            <td>
              <a><img class="link-image-img" src="/kame-house/img/dbz/dragon-radar.png" alt="Kame Senin Logo" onclick="kameHouse.core.windowLocation('dragonball/model-and-view')"/></a>
            </td>
            <td>
              <div class="link-image-text">Model And View</div>
              <div class="link-image-desc">Sample Model And View JSP endpoint</div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <span id="debug-mode-wrapper"></span>
  </div>
  </div>
</body>
</html>
