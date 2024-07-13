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
    require_once(realpath($_SERVER["DOCUMENT_ROOT"]) . "/kame-house-groot/api/v1/kamehouse/commons/kamehouse.php");
    $kameHouse->loader->loadSession();
    $kameHouse->session->getStatus();
  }
  
}
?>