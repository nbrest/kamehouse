<?php
/**
 * Endpoint: /kame-house-groot/api/v1/admin/my-scripts/my-scripts.php (GET)
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

    $myScriptsCSV = "";
  
    if (isLinuxHost()) {
      $username = trim(shell_exec("sudo /home/nbrest/my.scripts/kamehouse/get-username.sh"));
      $myScriptsCSV = trim(shell_exec("sudo -u " . $username . " /home/nbrest/my.scripts/lin/csv-my-scripts.sh"));
    } else {
      $myScriptsCSV = trim(shell_exec("C:/Users/nbrest/my.scripts/win/bat/git-bash.bat -c \"C:/Users/nbrest/my.scripts/win/csv-my-scripts.sh\""));
    }
  
    if (empty($myScriptsCSV)) {
      $myScriptsCSV = "couldn-find-scripts.sh";
    }
  
    $myScriptsArray = explode(",", $myScriptsCSV);
  
    setJsonResponseBody($myScriptsArray);
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