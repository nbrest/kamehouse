<?php
/**
 * Endpoint: /kame-house-groot/api/v1/admin/kamehouse-shell/scripts.php (GET)
 * 
 * Gets the list of scripts as csv from the server.
 * 
 * @author nbrest
 */
  main();
?>

<?php

  /**
   * Main scripts function.
   */
  function main() {
    init();

    $kameHouseShellCSV = "";
    
    if (isLinuxHost()) {
      $kameHouseShellCSV = trim(shell_exec("HOME=/var/www /var/www/programs/kamehouse-shell/bin/lin/csv-kamehouse-shell.sh"));
    } else {
      $kameHouseShellCSV = trim(shell_exec("%USERPROFILE%/programs/kamehouse-shell/bin/win/bat/git-bash.bat -c \"~/programs/kamehouse-shell/bin/win/csv-kamehouse-shell.sh\""));
    }
  
    if (empty($kameHouseShellCSV)) {
      $kameHouseShellCSV = "couldn-find-scripts.sh";
    }
  
    $kameHouseShellArray = explode(",", $kameHouseShellCSV);
  
    setJsonResponseBody($kameHouseShellArray);
  }

  /**
   * Init scripts.
   */
  function init() {
    ini_set('session.gc_maxlifetime', 0);
    session_set_cookie_params(0);
    session_start();
    require_once("../../../../api/v1/commons/kamehouse.php");
    require_once("../../../../api/v1/auth/authorize-admin-api.php");
    unlockSession();
  }
?>