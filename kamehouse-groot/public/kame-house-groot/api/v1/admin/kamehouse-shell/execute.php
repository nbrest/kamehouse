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
$executeApi = new ExecuteApi();
$executeApi->main();

class ExecuteApi {

  /**
   * Execute the kamehouse shell script.
   */
  public function main() {
    $documentRoot = realpath($_SERVER["DOCUMENT_ROOT"]);
    require_once("$documentRoot/kame-house-groot/api/v1/kamehouse/commons/kamehouse.php");
    require_once("$documentRoot/kame-house-groot/api/v1/kamehouse/admin/kamehouse-shell/kamehouse-shell.php");
    require_once("$documentRoot/kame-house-groot/api/v1/kamehouse/auth/kamehouse-auth.php");
    $kameHouse->auth->authorizeApi();
    $kameHouse->shell->execute();
  }
  
}
?>