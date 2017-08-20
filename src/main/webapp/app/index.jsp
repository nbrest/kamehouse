<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta name="viewport" content="width=device-width">
<meta name="author" content="nbrest">

<title>kame House - Angular App</title>
<link rel="icon" type="img/ico" href="${pageContext.request.contextPath}/img/favicon.ico" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/lib/css/bootstrap.min.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/general.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/footer.css" />
</head>
<body ng-app="myApp" class="ng-cloak">
  <div id="headerContainer"></div>
  <div ng-view></div>
  <div id="footerContainer"></div>
  <script src="${pageContext.request.contextPath}/lib/js/jquery-2.0.3.min.js"></script>
  <script src="${pageContext.request.contextPath}/js/importHeaderFooter.js"></script>
  <script type="text/javascript">importHeaderAndFooter("../html/")
  </script>
  <script src="${pageContext.request.contextPath}/lib/js/angular.js"></script>
  <script src="${pageContext.request.contextPath}/lib/js/angular-route.js"></script>
  <script src="js/app.js"></script>
  <script src="js/service/dragonball-user-service.js"></script>
  <script src="js/controller/dragonball-user-controller.js"></script>
</body>
</html>