<?php 
/**
 * Endpoint: /kame-house-groot/api/v1/commons/session/status.php (GET)
 * 
 * Get the current session status.
 * 
 * @author nbrest
 */
$kameHouseSessionStatus = new KameHouseSessionStatus();
$kameHouseSessionStatus->load();

class KameHouseSessionStatus {

  /**
   * Load the session status.
   */
  public function load() {
    global $kameHouse;
    $documentRoot = realpath($_SERVER["DOCUMENT_ROOT"]);
    require_once("$documentRoot/kame-house-groot/api/v1/commons/kamehouse.php");
    require_once("$documentRoot/kame-house-groot/api/v1/auth/kamehouse-auth.php");
    require_once("$documentRoot/kame-house-groot/api/v1/commons/session/kamehouse-session.php");
    $kameHouse->session->getStatus();
  }
}
?>