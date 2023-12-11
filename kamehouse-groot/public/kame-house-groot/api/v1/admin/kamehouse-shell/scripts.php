<?php
/**
 * [EXTERNAL] Endpoint: /kame-house-groot/api/v1/admin/kamehouse-shell/scripts.php (GET)
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
    $documentRoot = realpath($_SERVER["DOCUMENT_ROOT"]);
    require_once("$documentRoot/kame-house-groot/api/v1/commons/kamehouse.php");
    require_once("$documentRoot/kame-house-groot/api/v1/auth/kamehouse-auth.php");
    $kameHouse->auth->authorizeApi();
    require_once("$documentRoot/kame-house-groot/api/v1/admin/kamehouse-shell/kamehouse-shell.php");
    $kameHouse->shell->getScripts();
  }  
}
?>