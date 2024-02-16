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
    require_once(realpath($_SERVER["DOCUMENT_ROOT"]) . "/kame-house-groot/api/v1/kamehouse/commons/kamehouse.php");
    $kameHouse->auth->logout();
  }

}
?>