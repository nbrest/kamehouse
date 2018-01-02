<%@ page session="true"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta name="viewport" content="width=device-width">
<meta name="author" content="nbrest">

<title>Add DragonBallUser Form</title>
<link rel="icon" type="img/ico" href="${pageContext.request.contextPath}/img/favicon.ico" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/lib/css/bootstrap.min.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/jsp/css/app.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/general.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/footer.css" />
</head>
<body>
  <div id="headerContainer"></div>
  <main>
  <div class="container main">
    <div class="panel panel-default">
      <h3 class="page-title">Add DragonBall User</h3>
      <div class="formcontainer">
        <form action="users-add-action" method="post" class="form-horizontal mi-form-horizontal">
          <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
          <div class="row">
            <div class="form-group col-md-12">
              <label class="col-md-2 control-lable" for="username">Username</label>
              <div class="col-md-7">
                <input type="text" name="username" class="form-control input-sm" />
                </td>
              </div>
            </div>
          </div>

          <div class="row">
            <div class="form-group col-md-12">
              <label class="col-md-2 control-lable" for="email">Email</label>
              <div class="col-md-7">
                <input type="email" name="email" class="form-control input-sm" value="@dbz.com" />
                </td>
              </div>
            </div>
          </div>

          <div class="row">
            <div class="form-group col-md-12">
              <label class="col-md-2 control-lable" for="age">Age</label>
              <div class="col-md-7">
                <input type="text" name="age" class="form-control input-sm" value="1" />
                </td>
              </div>
            </div>
          </div>

          <div class="row">
            <div class="form-group col-md-12">
              <label class="col-md-2 control-lable" for="powerLevel">Power Level</label>
              <div class="col-md-7">
                <input type="text" name="powerLevel" class="form-control input-sm" value="1" />
                </td>
              </div>
            </div>
          </div>

          <div class="row">
            <div class="form-group col-md-12">
              <label class="col-md-2 control-lable" for="stamina">Stamina</label>
              <div class="col-md-7">
                <input type="text" name="stamina" class="form-control input-sm" value="1" />
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
    <input type="button" value="List DragonBall Users" class="btn btn-outline-secondary btn-block custom-width"
      onclick="window.location.href='users-list'">
  </div>
  </main>
  <div id="footerContainer"></div>
  <script src="${pageContext.request.contextPath}/lib/js/jquery-2.0.3.min.js"></script>
  <script src="${pageContext.request.contextPath}/js/importHeaderFooter.js"></script>
</body>
</html>
