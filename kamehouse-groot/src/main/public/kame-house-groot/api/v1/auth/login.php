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
    require_once(realpath($_SERVER["DOCUMENT_ROOT"]) . "/kame-house-groot/api/v1/kamehouse/commons/kamehouse.php");
    $kameHouse->auth->login();
  }

}
?>