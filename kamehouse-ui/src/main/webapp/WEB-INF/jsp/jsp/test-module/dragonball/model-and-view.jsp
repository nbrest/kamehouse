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
      <div id="banner" class="fade-in-out-15s banner-camus">
        <div class="default-layout banner-text">
          <h1>Dragonball ModelAndView JSP</h1>
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
                <img src="/kame-house/img/saint-seiya/dohko-shion-previous-era.jpg" alt="info image"/>
              </td>
              <td class="info-image-info">
                <div class="info-image-title">
                  Model And View
                </div>
                <div class="info-image-desc">
                  <p>Antique JSP model and view endpoint belonging to the previous holly war</p>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

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

      <div class="link-image-wrapper-m-80-60 link-image-wrapper-w-80">
        <table class="link-image-table">
          <caption class="hidden-kh">Image-Links</caption>
          <thead class="hidden-kh"><tr><th>Image-Links</th></tr></thead>
          <tbody>
            <tr>
              <td>
                <a><img class="link-image-img" src="/kame-house/img/dbz/scouter.png" alt="Kame Senin Logo" onclick="window.location.href='/kame-house/api/v1/ui/sample/dragonball/model-and-view?name=gohan'"/></a>
              </td>
              <td>
                <div class="link-image-text">Use Parameters</div>
                <div class="link-image-desc">Click to call this view with some sample parameters and see the updated view</div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

    </div>
    <span id="debug-mode-wrapper"></span>
  </div>
</body>
</html>
