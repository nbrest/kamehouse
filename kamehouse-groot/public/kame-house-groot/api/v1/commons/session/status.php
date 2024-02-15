<?php 
/**
 * Endpoint: /kame-house-groot/api/v1/commons/session/status.php (GET)
 * 
 * Get the current session status.
 * 
 * @author nbrest
 */
$statusApi = new StatusApi();
$statusApi->main();

class StatusApi {

  /**
   * Load the session status.
   */
  public function main() {
    $documentRoot = realpath($_SERVER["DOCUMENT_ROOT"]);
    require_once("$documentRoot/kame-house-groot/api/v1/kamehouse/commons/kamehouse.php");
    require_once("$documentRoot/kame-house-groot/api/v1/kamehouse/admin/kamehouse-shell/kamehouse-shell.php");
    require_once("$documentRoot/kame-house-groot/api/v1/kamehouse/auth/kamehouse-auth.php");
    require_once("$documentRoot/kame-house-groot/api/v1/kamehouse/commons/session/kamehouse-session.php");
    $kameHouse->session->getStatus();
  }
  
}
?>