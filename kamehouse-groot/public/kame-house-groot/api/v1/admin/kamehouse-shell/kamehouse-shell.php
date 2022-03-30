<?php
/**
 * Endpoint: /kame-house-groot/api/v1/admin/kamehouse-shell/kamehouse-shell.php (GET)
 * 
 * Gets the list of scripts as csv from the server.
 * 
 * @author nbrest
 */
  main();
?>

<?php

  function main() {
    init();

    $kameHouseShellCSV = "";
    
    //TODO remove nbrest from these paths and try to get the username from the env
    if (isLinuxHost()) {
      $username = trim(shell_exec("sudo /home/nbrest/programs/kamehouse-shell/bin/kamehouse/get-username.sh"));
      $kameHouseShellCSV = trim(shell_exec("sudo -u " . $username . " /home/nbrest/programs/kamehouse-shell/bin/lin/csv-kamehouse-shell.sh"));
    } else {
      $kameHouseShellCSV = trim(shell_exec("C:/Users/nbrest/programs/kamehouse-shell/bin/win/bat/git-bash.bat -c \"C:/Users/nbrest/programs/kamehouse-shell/bin/win/csv-kamehouse-shell.sh\""));
    }
  
    if (empty($kameHouseShellCSV)) {
      $kameHouseShellCSV = "couldn-find-scripts.sh";
    }
  
    $kameHouseShellArray = explode(",", $kameHouseShellCSV);
  
    setJsonResponseBody($kameHouseShellArray);
  }

  function init() {
    ini_set('session.gc_maxlifetime', 0);
    session_set_cookie_params(0);
    session_start();
    require_once("../../../../api/v1/commons/global.php");
    require_once("../../../../api/v1/auth/authorize-api.php");
    unlockSession();
  }
?>