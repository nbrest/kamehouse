<!DOCTYPE html>
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta charset="utf-8">
<meta name="viewport" content="width=device-width">
<meta name="author" content="nbrest">
<meta name="description" content="kame-house login">
<meta name="keywords" content="kame-house nicobrest nbrest">
<meta name="mobile-web-app-capable" content="yes">

<title>KameHouse - Login</title>

<link rel="icon" type="img/ico" href="/kame-house/img/favicon.ico" />
<script src="/kame-house/lib/js/jquery-2.0.3.min.js"></script>
<script src="/kame-house/js/global.js"></script>
<script src="/kame-house/js/login.js"></script>
<link rel="stylesheet" href="/kame-house/lib/css/bootstrap.min.css" />
<link rel="stylesheet" href="/kame-house/css/global.css" /> 
<link rel="stylesheet" href="/kame-house/css/login.css" /> 
</head>
<body>
  <div class="default-layout main-body">
    <form action="/kame-house/login" method="post" class="form-horizontal login-form">

      <div id="login-alert-group-error" class="hidden-kh">
        <div class="login-alert-group">
          <img class="login-icon"
            src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNgYAAAAAMAASsJTYQAAAAASUVORK5CYII="
            alt="alert-error" />
          <div class="login-alert-error">Invalid username and password.</div>
        </div>
      </div>
      <div id="login-alert-group-logout" class="hidden-kh">
        <div class="login-alert-group">
          <img class="login-icon"
            src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNgYAAAAAMAASsJTYQAAAAASUVORK5CYII="
            alt="alert-success" />
          <div class="login-alert-success">You have been logged out successfully.</div>
        </div>
      </div>

      <div class="login-input-group">
        <label for="username" class="login-label">
          <img class="login-icon" src="/kame-house/img/dbz/goku-dark-gray.png" alt="username"/>
        </label>
        <input type="text" class="login-input-form" id="username" name="username"
          placeholder="Enter Username" required>
      </div>
      <br>
      <div class="login-input-group">
        <label for="password" class="login-label">
          <img class="login-icon" src="/kame-house/img/pc/password-dark-gray.png" alt="password"/>
        </label>
        <input type="password" class="login-input-form" id="password" name="password"
          placeholder="Enter Password" required>
      </div>
      <br>
      <div class="login-input-group">
        <label for="submit" class="login-label">
          <!-- transparent 1 pixel img -->
          <img class="login-icon"
            src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNgYAAAAAMAASsJTYQAAAAASUVORK5CYII="
            alt="submit"/>
        </label>
        <input type="submit" class="btn btn-block btn-outline-secondary btn-default"
             value="Enter KameHouse">
      </div>
    </form>
  </div>
</body>
</html>