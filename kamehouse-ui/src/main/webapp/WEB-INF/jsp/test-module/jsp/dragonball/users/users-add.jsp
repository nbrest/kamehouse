<%@ page session="true"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta name="viewport" content="width=device-width">
<meta name="author" content="nbrest">
<meta name="mobile-web-app-capable" content="yes">

<title>Add DragonBallUser Form</title>
<link rel="icon" type="img/ico" href="${pageContext.request.contextPath}/img/favicon.ico" />
<script src="/cordova.js"></script>
<script src="/kame-house/lib/js/jquery.js"></script>
<script src="/kame-house/js/kamehouse.js"></script>
<script src="/kame-house/js/test-module/jsp/dragonball/users/dragonball-user-service-jsp.js"></script>
<script src="/kame-house/js/test-module/jsp/dragonball/users/dragonball-users-add.js"></script>
<link rel="stylesheet" href="${pageContext.request.contextPath}/lib/css/bootstrap.min.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/kamehouse.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/header-footer/header.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/header-footer/footer.css" />
</head>
<body> 
  <div class="default-layout main-body p-15-m-kh">
    <br>
    <h3 class="h3-kh txt-l-d-kh txt-l-m-kh">Add DragonBall User</h3>
    <br>
    <form class="form-kh">
      <label for="input-username">Username</label>
      <input type="text" class="form-input-kh" id="input-username" value="user" required/>
      <br>

      <label for="input-email">Email</label>
      <input type="email" class="form-input-kh" id="input-email" value="user@dbz.com" required/>
      <br>

      <label for="input-age">Age</label>
      <input type="text" class="form-input-kh" id="input-age" value="1"/>
      <br>

      <label for="input-powerLevel">Power Level</label>
      <input type="text" class="form-input-kh" id="input-powerLevel" value="2"/>
      <br>

      <label for="input-stamina">Stamina</label>
      <input type="text" class="form-input-kh" id="input-stamina" value="3"/>
      <br>

      <div class="form-submit-wrapper-kh">
        <img class="img-btn-kh m-15-d-r-kh" onclick="dragonBallUserServiceJsp.addDragonBallUser()" 
        src="/kame-house/img/other/submit-gray-dark.png" alt="Add User" title="Add User"/>
        <img class="img-btn-kh fl-r-d-kh" onclick="window.location.href='users-list'"
          src="/kame-house/img/other/list-bullet-gray-dark.png" alt="List Users" title="List Users"/>
      </div>
    </form>
    <br><br>
    <div class="default-layout txt-c-d-kh txt-c-m-kh">
      <span id="debug-mode-button-wrapper" class="debug-mode-btn-hidden"></span>
    </div>
  </div>
  <span id="debug-mode-wrapper"></span>
  <script src="/kame-house/js/snippets/kamehouse-debugger.js"></script>
  <script src="/kame-house/js/snippets/kamehouse-modal.js"></script>
</body>
</html>
