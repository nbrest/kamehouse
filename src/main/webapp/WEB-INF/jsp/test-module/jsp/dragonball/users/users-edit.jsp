<%@ page import="java.util.*"%>
<%@ page import="com.nicobrest.kamehouse.testmodule.service.DragonBallUserService"%>
<%@ page import="com.nicobrest.kamehouse.testmodule.model.DragonBallUser"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="true"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta name="viewport" content="width=device-width">
<meta name="author" content="nbrest">

<title>Edit DragonBallUser Form</title>
<link rel="icon" type="img/ico" href="${pageContext.request.contextPath}/img/favicon.ico" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/lib/css/bootstrap.min.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/test-module/jsp/css/app.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/global.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/header-footer/header.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/header-footer/footer.css" />
</head>
<body>
  <div id="headerContainer"></div> 
  <div class="default-layout main-body">
    <%-- TODO: I had to use the username as parameter because the id is of type Long, and
  since the method getDragonBallUser is overloaded for strings and longs, when I passed
  the id from the jsp, the method was invoked with a String parameter searching by the
  username instead of a Long parameter searching by id.
  See how I can do to pass the id so it is recognized as a Long when the method is overloaded --%>
    <c:set var="username" value="${param.username}"></c:set>
    <%-- TODO: Check username for null and assign it an empty string or something, otherwise, it throws 500 error, if I access the page directly --%>
    <c:set var="dragonBallUser" value="${dragonBallUserService.getDragonBallUser(username)}"></c:set>

    <div class="panel panel-default">
      <h3 class="h3-kh txt-l-kh">Edit DragonBall User</h3>
      <div class="formcontainer">
        <form action="users-edit-action" method="post"
          class="form-horizontal mi-form-horizontal">
          <input type="hidden" name="id" value="${dragonBallUser.getId()}" />
          <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
          <div class="row">
            <div class="form-group col-md-12">
              <label class="col-md-2 control-lable" for="username">Username</label>
              <div class="col-md-7">
                <input type="text" name="username" class="form-control input-sm"
                  value="${dragonBallUser.getUsername()}" />
                </td>
              </div>
            </div>
          </div>

          <div class="row">
            <div class="form-group col-md-12">
              <label class="col-md-2 control-lable" for="email">Email</label>
              <div class="col-md-7">
                <input type="email" name="email" class="form-control input-sm"
                  value="${dragonBallUser.getEmail()}" />
                </td>
              </div>
            </div>
          </div>

          <div class="row">
            <div class="form-group col-md-12">
              <label class="col-md-2 control-lable" for="age">Age</label>
              <div class="col-md-7">
                <input type="text" name="age" class="form-control input-sm"
                  value="${dragonBallUser.getAge()}" />
                </td>
              </div>
            </div>
          </div>

          <div class="row">
            <div class="form-group col-md-12">
              <label class="col-md-2 control-lable" for="powerLevel">Power Level</label>
              <div class="col-md-7">
                <input type="text" name="powerLevel" class="form-control input-sm"
                  value="${dragonBallUser.getPowerLevel()}" />
                </td>
              </div>
            </div>
          </div>

          <div class="row">
            <div class="form-group col-md-12">
              <label class="col-md-2 control-lable" for="stamina">Stamina</label>
              <div class="col-md-7">
                <input type="text" name="stamina" class="form-control input-sm"
                  value="${dragonBallUser.getStamina()}" />
                </td>
              </div>
            </div>
          </div>

          <div class="row">
            <div class="dragonball-user-form-buttons">
              <input type="submit" value="Submit" class="btn btn-outline-info btn-sm" />
            </div>
          </div>
        </form>
      </div>
    </div>
    <input type="button" value="List DragonBall Users" class="btn btn-outline-secondary btn-block"
      onclick="window.location.href='users-list'">
  </div> 
  <div id="footerContainer"></div>
  <script src="${pageContext.request.contextPath}/lib/js/jquery-2.0.3.min.js"></script>
  <script src="${pageContext.request.contextPath}/js/header-footer/headerFooter.js"></script>
</body>
</html>
