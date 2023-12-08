<?php
/**
 * Endpoint: /kame-house-groot/api/v1/admin/kamehouse-shell/execute.php (GET)
 * 
 * Url parameters: 
 *  - script: Script to execute with relative path to ${HOME}/programs/kamehouse-shell/bin
 *  - args: Arguments to pass to the script
 * 
 * @author nbrest
 */
require_once("kamehouse-shell.php");
$kameHouseShell = new KameHouseShell();
$kameHouseShell->execute();
?>