<%@ page import="java.util.*"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="true"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta name="viewport" content="width=device-width">
<meta name="author" content="nbrest">
<meta name="mobile-web-app-capable" content="yes">

<title>DragonBallUsers List</title>
<link rel="icon" type="img/ico" href="${pageContext.request.contextPath}/img/favicon.ico" />
<script src="/cordova.js"></script>
<script src="/kame-house/lib/js/jquery.js"></script>
<script src="/kame-house/kamehouse/js/kamehouse.js" id="kamehouse-data" data-authorized-roles="ROLE_SAIYAJIN"></script>
<script src="/kame-house/js/jsp/test-module/dragonball/users/dragonball-user-service-jsp.js"></script>
<script src="/kame-house/js/jsp/test-module/dragonball/users/dragonball-users-list.js"></script>
<link rel="stylesheet" href="${pageContext.request.contextPath}/lib/css/bootstrap.min.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/kamehouse/css/kamehouse.css" />
<link rel="stylesheet" href="/kame-house/css/test-module/test-module.css" />
</head>
<body> 
  <div class="main-body">
    <div class="banner-wrapper">
      <div id="banner" class="fade-in-out-15s banner-hades">
        <div class="default-layout banner-text">
          <h1>Dragonball Users JSP</h1>
        </div>
      </div>
    </div>
    <div class="default-layout">
      <div class="info-image-wrapper-m-80-60">
        <table class="info-image-table info-image-table-reverse">
          <caption class="hidden-kh">Image-Info</caption>
          <thead class="hidden-kh"><tr><th>Image-Info</th></tr></thead>
          <tbody>
            <tr>
              <td class="info-image-img">
                <img src="/kame-house/img/dbz/shen-long-dragonballs.jpg" alt="info image"/>
              </td>
              <td class="info-image-info">
                <div class="info-image-title">
                  Raise your Ki to the limit
                </div>
                <div class="info-image-desc">
                  <p>Join Goku and Bulma to find the 7 dragonballs and make your wishes come true with Shen Long</p>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
    <div class="default-layout p-15-m-kh">
      <h3 class="h3-kh txt-l-d-kh txt-l-m-kh">DragonBall Users</h3>
      <br>
      <%
          Enumeration<String> paramNames = request.getParameterNames();
          while (paramNames.hasMoreElements()) {
              String paramName = (String) paramNames.nextElement();
              System.out.print(paramName + " : ");
              String paramValue = request.getParameter(paramName);
              System.out.println(paramValue);
          }
      %>
      <table class="table-kh">
        <caption class="hidden-kh">DragonBall Users</caption>
        <thead class="hidden-kh"><tr><th id="header-row">Header</th></tr></thead>
        <tbody id="dragonball-users-tbody">
        <!--
          <c:forEach items="${dragonBallUsers}" var="dragonBallUser">
            <tr>
              <td>${dragonBallUser.getId()}</td>
              <td>${dragonBallUser.getUsername()}</td>
              <td>${dragonBallUser.getEmail()}</td>
              <td>${dragonBallUser.getAge()}</td>
              <td>${dragonBallUser.getPowerLevel()}</td>
              <td>${dragonBallUser.getStamina()}</td>
              <td>
                <input type="button" value="edit"
                  class="btn" onclick="window.location.href='users-edit?username=${dragonBallUser.getUsername()}'">
                <form action="users-delete-action" method="post">
                  <input type="hidden" name="id" value="${dragonBallUser.getId()}" />
                  <input type="submit" value="delete" class="btn" />
                </form>
              </td>
            </tr>
          </c:forEach>
          -->
        </tbody>
      </table>
      <br>
      <img class="img-btn-kh" onclick="window.location.href='users-add'"
        src="/kame-house/img/other/add-circle-gray-dark.png" alt="Add User" title="Add User"/>
    </div>
    <span id="debug-mode-wrapper"></span>
  </div>
</body>
</html>
