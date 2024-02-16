<?php
/**
 * Endpoint: /kame-house-groot/api/v1/admin/kamehouse-shell/scripts.php (GET)
 * 
 * Gets the list of scripts as csv from the server.
 * 
 * @author nbrest
 */
$scriptsApi = new ScriptsApi();
$scriptsApi->main();

class ScriptsApi {

  /**
   * Load kamehouse shell scripts.
   */
  public function main() {
    require_once(realpath($_SERVER["DOCUMENT_ROOT"]) . "/kame-house-groot/api/v1/kamehouse/commons/kamehouse.php");
    $kameHouse->auth->authorizeApi();
    $kameHouse->shell->getScripts("common/csv/csv-kamehouse-shell.sh");
  }  
  
}
?>