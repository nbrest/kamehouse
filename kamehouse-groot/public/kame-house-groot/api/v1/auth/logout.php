<?php
/**
 * Endpoint: /kame-house-groot/api/v1/auth/logout.php (GET)
 * 
 * Logout the current user.
 * 
 * @author nbrest
 */
$logoutApi = new LogoutApi();
$logoutApi->main();

class LogoutApi {

  /**
   * Logout the current session.
   */
  public function main() {
    $documentRoot = realpath($_SERVER["DOCUMENT_ROOT"]);
    require_once("$documentRoot/kame-house-groot/api/v1/kamehouse/commons/kamehouse.php");
    require_once("$documentRoot/kame-house-groot/api/v1/kamehouse/auth/kamehouse-auth.php");
    $kameHouse->auth->logout();
  }

}
?>