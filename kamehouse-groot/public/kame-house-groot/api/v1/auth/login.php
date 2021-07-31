<?php
/**
 * Authenticate the specified user and password.
 * 
 * @author nbrest
 */
  main();
?>

<?php

  /**
   * Authenticate the user with the received credentials. 
   * If successful redirect to home. If login unsuccessful redirect back to login page.
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

  function initiateSession($username) {
    session_start();
    session_regenerate_id();
    $_SESSION['logged-in'] = true;
    $_SESSION['username'] = $username;
  }

  function redirectLoginSuccess() {
    $redirectUrl = "/kame-house-groot/";
    if (isset($_POST['referrer']) && startsWith($_POST['referrer'], "/kame-house-groot/")) {
      $redirectUrl = $_POST['referrer'];
    }
    header('Location: ' . $redirectUrl);
    exit;
  }

  function redirectLoginError() {
    header('Location: /kame-house-groot/login.html?error');
    exit;
  }
?>