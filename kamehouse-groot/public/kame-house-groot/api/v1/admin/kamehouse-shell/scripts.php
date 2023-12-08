<?php
/**
 * Endpoint: /kame-house-groot/api/v1/admin/kamehouse-shell/scripts.php (GET)
 * 
 * Gets the list of scripts as csv from the server.
 * 
 * @author nbrest
 */
require_once("kamehouse-shell.php");
$kameHouseShell = new KameHouseShell();
$kameHouseShell->getScripts();
?>