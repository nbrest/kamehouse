<?php
/**
 * Endpoint: /kame-house-groot/api/v1/auth/login.php (POST)
 * 
 * Authenticate the specified user and password.
 * 
 * @author nbrest
 */
$loginApi = new LoginApi();
$loginApi->main();

class LoginApi {

  /**
   * Authenticate the user with the received credentials. 
   */
  public function main() {
    $documentRoot = realpath($_SERVER["DOCUMENT_ROOT"]);
    require_once("$documentRoot/kame-house-groot/api/v1/kamehouse/commons/kamehouse.php");
    require_once("$documentRoot/kame-house-groot/api/v1/kamehouse/admin/kamehouse-shell/kamehouse-shell.php");
    require_once("$documentRoot/kame-house-groot/api/v1/kamehouse/auth/kamehouse-auth.php");
    $kameHouse->auth->login();
  }

}
?>