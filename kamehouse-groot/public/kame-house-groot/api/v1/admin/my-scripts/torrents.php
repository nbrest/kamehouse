<?php
/**
 * Endpoint: /kame-house-groot/api/v1/admin/my-scripts/my-scripts.php
 * 
 * @author nbrest
 */
  main();
?>

<?php

  function main() {
    init();

    /** Gets the list of scripts as csv from the server */
    $myScriptsCSV = "";
  
    if (isLinuxHost()) {
      /** Get linux scripts */
      $username = trim(shell_exec("sudo /home/nbrest/my.scripts/kamehouse/get-username.sh"));
      $myScriptsCSV = trim(shell_exec("sudo -u " . $username . " /home/nbrest/my.scripts/lin/transmission/csv-torrents.sh"));
    } else {
      /** Get windows scripts */
      $myScriptsCSV = "This should only execute on linux";
    }
  
    $myScriptsArray = explode(",", $myScriptsCSV);
  
    setJsonResponseBody($myScriptsArray);
  }

  function init() {
    require_once("../../../../api/v1/commons/global.php");
  }
?>