<?php
/**
 * Endpoint: /kame-house-groot/api/v1/auth/login.php (POST)
 * 
 * Authenticate the specified user and password.
 * 
 * @author nbrest
 */
  main();
?>

<?php

  /**
   * Authenticate the user with the received credentials. 
   */
  function main() {
    init();

    if (!isset($_POST['username'], $_POST['password'])) {
      logToErrorFile("Username or password not set");
      redirectLoginError();
    }
    
    $username = $_POST['username'];
    $password = $_POST['password'];

    if (isAuthorizedUser($username, $password)) {
      initiateSession($username);
      redirectLoginSuccess();
    } else {
      logToErrorFile("User '" . $username . "' is not authorized");
      redirectLoginError();
    } 
  }

  function init() {
    require_once("../../../api/v1/commons/global.php");
    require_once("auth-functions.php");
  }

  /**
   * Redirect after successful login.
   */
  function redirectLoginSuccess() {
    $redirectUrl = "/kame-house-groot/";
    if (isset($_POST['referrer']) && startsWith($_POST['referrer'], "/kame-house-groot/")) {
      $redirectUrl = $_POST['referrer'];
    }
    header('Location: ' . $redirectUrl);
    exit;
  }

  /**
   * Redirect after a failed login.
   */
  function redirectLoginError() {
    header('Location: /kame-house-groot/login.html?error');
    exit;
  }
?>