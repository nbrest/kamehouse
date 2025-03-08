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
    require_once(realpath($_SERVER["DOCUMENT_ROOT"]) . "/kame-house-groot/api/v1/kamehouse/commons/kamehouse.php");
    $kameHouse->logger->info("Started execute.php");
    $kameHouse->auth->authorizeApi();
    $kameHouse->shell->execute();
  }
  
}
?>