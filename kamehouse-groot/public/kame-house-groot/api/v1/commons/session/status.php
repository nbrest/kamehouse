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

  public function load() {
    global $kameHouse;
    require_once("../../../../api/v1/commons/kamehouse.php");
    require_once("../../../../api/v1/auth/kamehouse-auth.php");
    require_once("kamehouse-session.php");
    $kameHouse->session->getStatus();
  }
}
?>