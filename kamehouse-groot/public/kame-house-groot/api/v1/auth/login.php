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
      redirectLoginError();
    }
    
    $username = $_POST['username'];
    $password = $_POST['password'];

    if (isAuthorizedUser($username, $password)) {
      initiateSession($username);
      redirectLoginSuccess();
    } else {
      redirectLoginError();
    } 
  }

  function init() {
    require_once("../../../api/v1/commons/global.php");
    require_once("auth-functions.php");
  }

  /**
   * Start a new session.
   */
  function initiateSession($username) {
    ini_set('session.gc_maxlifetime', 0);
    session_set_cookie_params(0);
    session_start();
    session_regenerate_id();
    $_SESSION['logged-in'] = true;
    $_SESSION['username'] = $username;
    unlockSession();
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