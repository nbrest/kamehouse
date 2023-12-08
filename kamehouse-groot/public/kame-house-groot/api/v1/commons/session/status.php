<?php 
/**
 * Endpoint: /kame-house-groot/api/v1/commons/session/status.php (GET)
 * 
 * Get the current session status.
 * 
 * @author nbrest
 */
require_once("kamehouse-session.php");
$kameHouseSession = new KameHouseSession();
$kameHouseSession->getStatus();
?>