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
$kameHouseShellExecutor = new KameHouseShellExecutor();
$kameHouseShellExecutor->execute();

class KameHouseShellExecutor {

  /**
   * Execute the kamehouse shell script.
   */
  public function execute() {
    global $kameHouse;
    require_once("../../../../api/v1/commons/kamehouse.php");
    require_once("../../../../api/v1/auth/kamehouse-auth.php");
    require_once("kamehouse-shell.php");
    $kameHouse->shell->initSession();
    require_once("../../../../api/v1/auth/authorize-admin-api.php");
    $kameHouse->shell->execute();
  }
}
?>