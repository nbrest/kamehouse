<?php
/**
 * Endpoint: /kame-house-groot/api/v1/admin/kamehouse-shell/scripts.php (GET)
 * 
 * Gets the list of scripts as csv from the server.
 * 
 * @author nbrest
 */
$kameHouseShellScriptsLoader = new KameHouseShellScriptsLoader();
$kameHouseShellScriptsLoader->getScripts();

class KameHouseShellScriptsLoader {

  /**
   * Load kamehouse shell scripts.
   */
  public function getScripts() {
    global $kameHouse;
    require_once("../../../../api/v1/commons/kamehouse.php");
    require_once("../../../../api/v1/auth/kamehouse-auth.php");
    $kameHouse->auth->authorizeApi();
    require_once("kamehouse-shell.php");
    $kameHouse->shell->getScripts();
  }  
}
?>