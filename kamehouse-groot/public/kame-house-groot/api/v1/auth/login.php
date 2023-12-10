<?php
/**
 * Endpoint: /kame-house-groot/api/v1/auth/login.php (POST)
 * 
 * Authenticate the specified user and password.
 * 
 * @author nbrest
 */
$kameHouseGrootLogin = new KameHouseGrootLogin();
$kameHouseGrootLogin->login();

class KameHouseGrootLogin {

  /**
   * Authenticate the user with the received credentials. 
   */
  public function login() {
    global $kameHouse;
    $this->init();

    if (!isset($_POST['username'], $_POST['password'])) {
      $kameHouse->logger->info("Username or password not set");
      $this->redirectLoginError();
    }
    
    $username = $_POST['username'];
    $password = $_POST['password'];

    if ($kameHouse->auth->isAuthorizedUser($username, $password)) {
      $kameHouse->auth->initiateSession($username);
      $this->redirectLoginSuccess();
    } else {
      $kameHouse->logger->info("User '" . $username . "' is not authorized");
      $this->redirectLoginError();
    } 
  }

  /**
   * Init login.
   */
  private function init() {
    $documentRoot = realpath($_SERVER["DOCUMENT_ROOT"]);
    require_once("$documentRoot/kame-house-groot/api/v1/commons/kamehouse.php");
    require_once("$documentRoot/kame-house-groot/api/v1/auth/kamehouse-auth.php");
  }

  /**
   * Redirect after successful login.
   */
  private function redirectLoginSuccess() {
    global $kameHouse;
    $redirectUrl = "/kame-house-groot/";
    if (isset($_POST['referrer']) && $kameHouse->util->string->startsWith($_POST['referrer'], "/kame-house-groot/")) {
      $redirectUrl = $_POST['referrer'];
    }
    header('Location: ' . $redirectUrl);
    exit;
  }

  /**
   * Redirect after a failed login.
   */
  private function redirectLoginError() {
    header('Location: /kame-house-groot/login.html?error=true');
    exit;
  }

} // KameHouseGrootLogin
?>