<%@ page session="true"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta name="viewport" content="width=device-width">
<meta name="author" content="nbrest">
<meta name="mobile-web-app-capable" content="yes">

<title>Add DragonBallUser Form</title>
<link rel="icon" type="img/ico" href="${pageContext.request.contextPath}/img/favicon.ico" />
<script src="/kame-house/lib/js/jquery-2.0.3.min.js"></script>
<script src="/kame-house/js/global.js"></script>
<link rel="stylesheet" href="${pageContext.request.contextPath}/lib/css/bootstrap.min.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/test-module/jsp/app.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/global.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/header-footer/header.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/header-footer/footer.css" />
</head>
<body> 
  <div class="default-layout main-body">
    <div class="panel panel-default">
      <h3 class="h3-kh txt-l-d-kh txt-l-m-kh">Add DragonBall User</h3>
      <div class="formcontainer">
        <form action="users-add-action" method="post" class="form-horizontal mi-form-horizontal">
          <div class="row">
            <div class="form-group col-md-12">
              <label class="col-md-2 control-lable" for="username">Username</label>
              <div class="col-md-7">
                <input type="text" name="username" class="form-control input-sm" />
              </div>
            </div>
          </div>

          <div class="row">
            <div class="form-group col-md-12">
              <label class="col-md-2 control-lable" for="email">Email</label>
              <div class="col-md-7">
                <input type="email" name="email" class="form-control input-sm" value="@dbz.com" />
              </div>
            </div>
          </div>

          <div class="row">
            <div class="form-group col-md-12">
              <label class="col-md-2 control-lable" for="age">Age</label>
              <div class="col-md-7">
                <input type="text" name="age" class="form-control input-sm" value="1" />
              </div>
            </div>
          </div>

          <div class="row">
            <div class="form-group col-md-12">
              <label class="col-md-2 control-lable" for="powerLevel">Power Level</label>
              <div class="col-md-7">
                <input type="text" name="powerLevel" class="form-control input-sm" value="1" />
              </div>
            </div>
          </div>

          <div class="row">
            <div class="form-group col-md-12">
              <label class="col-md-2 control-lable" for="stamina">Stamina</label>
              <div class="col-md-7">
                <input type="text" name="stamina" class="form-control input-sm" value="1" />
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
</body>
</html>
