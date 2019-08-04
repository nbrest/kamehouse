<%@ page session="true"%>
<!DOCTYPE html>
<html>
<head>
<meta name="_csrf" content="${_csrf.token}"/>
<meta name="_csrf_header" content="${_csrf.headerName}"/>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta name="viewport" content="width=device-width">
<meta name="author" content="nbrest">

<title>KameHouse - Test Module - Angular-1</title>
<link rel="icon" type="img/ico" href="/kame-house/img/favicon.ico" />
<link rel="stylesheet" href="/kame-house/lib/css/bootstrap.min.css" />
<link rel="stylesheet" href="/kame-house/css/general.css" />
<link rel="stylesheet" href="/kame-house/css/header.css" />
<link rel="stylesheet" href="/kame-house/css/footer.css" />
</head>
<body ng-app="myApp" class="ng-cloak">
  <div id="headerContainer"></div>
  <div id="main-content" class="main-body" ng-view></div>
  <div id="footerContainer"></div>
  <script src="/kame-house/lib/js/jquery-2.0.3.min.js"></script>
  <script src="/kame-house/js/importHeaderFooter.js"></script>
  <script src="/kame-house/lib/js/angular.js"></script>
  <script src="/kame-house/lib/js/angular-route.js"></script>
  <script src="/kame-house/test-module/angular-1/js/app.js"></script>
  <script src="/kame-house/test-module/angular-1/js/service/dragonball-user-service.js"></script>
  <script src="/kame-house/test-module/angular-1/js/controller/dragonball-user-controller.js"></script>
</body>
</html>