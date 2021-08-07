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

<title>Edit DragonBallUser Form</title>
<link rel="icon" type="img/ico" href="${pageContext.request.contextPath}/img/favicon.ico" />
<script src="/kame-house/lib/js/jquery-2.0.3.min.js"></script>
<script src="/kame-house/js/global.js"></script>
<script src="/kame-house/js/test-module/jsp/dragonball/users/dragonball-user-service-jsp.js"></script>
<script src="/kame-house/js/test-module/jsp/dragonball/users/dragonball-users-edit.js"></script>
<link rel="stylesheet" href="${pageContext.request.contextPath}/lib/css/bootstrap.min.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/test-module/jsp/app.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/global.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/header-footer/header.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/header-footer/footer.css" />
</head>
<body> 
  <div class="default-layout main-body p-15-m-kh">
    <div class="panel panel-default">
      <h3 class="h3-kh txt-l-d-kh txt-l-m-kh">Edit DragonBall User</h3>
      <div class="formcontainer">
        <div id="form-edit" class="form-horizontal mi-form-horizontal">
          <input type="hidden" id="input-id" name="id"/>
          <div class="row">
            <div class="form-group col-md-12">
              <label class="col-md-2 control-lable" for="username">Username</label>
              <div class="col-md-7">
                <input id="input-username" type="text" name="username" class="form-control input-sm"/>
              </div>
            </div>
          </div>

          <div class="row">
            <div class="form-group col-md-12">
              <label class="col-md-2 control-lable" for="email">Email</label>
              <div class="col-md-7">
                <input id="input-email" type="email" name="email" class="form-control input-sm"/>
              </div>
            </div>
          </div>

          <div class="row">
            <div class="form-group col-md-12">
              <label class="col-md-2 control-lable" for="age">Age</label>
              <div class="col-md-7">
                <input id="input-age" type="text" name="age" class="form-control input-sm"/>
              </div>
            </div>
          </div>

          <div class="row">
            <div class="form-group col-md-12">
              <label class="col-md-2 control-lable" for="powerLevel">Power Level</label>
              <div class="col-md-7">
                <input id="input-powerLevel" type="text" name="powerLevel" class="form-control input-sm"/>
              </div>
            </div>
          </div>

          <div class="row">
            <div class="form-group col-md-12">
              <label class="col-md-2 control-lable" for="stamina">Stamina</label>
              <div class="col-md-7">
                <input id="input-stamina" type="text" name="stamina" class="form-control input-sm"/>
              </div>
            </div>
          </div>

          <div class="row">
            <div class="dragonball-user-form-buttons">
              <img class="img-btn-kh m-15-d-r-kh" onclick="dragonBallUserServiceJsp.updateDragonBallUser()" 
                src="/kame-house/img/mplayer/play-blue.png" alt="Update User" title="Update User"/>
              <img class="img-btn-kh" onclick="window.location.href='users-list'"
                src="/kame-house/img/other/list-bullet-blue.png" alt="List Users" title="List Users"/>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="default-layout txt-c-d-kh txt-c-m-kh">
      <br>
      <span id="debug-mode-button-wrapper"></span>
    </div>
  </div>
  <span id="debug-mode-wrapper"></span>
  <script src="/kame-house/js/snippets/kamehouse-debugger.js"></script>
</body>
</html>
