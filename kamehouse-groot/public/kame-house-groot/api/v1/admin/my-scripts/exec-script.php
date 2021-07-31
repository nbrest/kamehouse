<?php
/**
 * Endpoint: /kame-house-groot/api/v1/admin/my-scripts/exec-script.php
 * 
 * Url parameters: 
 *  - script: Script to execute with relative path to my.scripts repo
 *  - args: Arguments to pass to the script
 * 
 * @author nbrest
 */
  main();
?> 

<?php

  function main() {
    init();
  
    $script = isset($_GET['script']) ? $_GET['script'] : '';
    $scriptArgs = isset($_GET['args']) ? $_GET['args'] : '';

    $shellCommandOutput = executeShellScript($script, $scriptArgs);
    $htmlCommandOutput = getHtmlOutput($shellCommandOutput);
  
    $consoleOutput = [ 'htmlConsoleOutput' => $htmlCommandOutput, 'bashConsoleOutput' => $shellCommandOutput ];
  
    setJsonResponseBody($consoleOutput);
  }

  function init() {
    session_start();
    require_once("../../../../api/v1/commons/global.php");
    require_once("../../../../api/v1/auth/authorize-api.php");
    // Disable time_limit and max_execution_time (mainly for scp-torrent.sh script)
    set_time_limit(0);
    ini_set('max_execution_time', 0);
  }

  /**
   * Execute the specified script. 
   */
  function executeShellScript($script, $scriptArgs) {
    if(!isValidInputForShell($script)) {
      exitWithError(400, "script is invalid for shell execution");
    }

    if(!isValidInputForShell($scriptArgs)) {
      exitWithError(400, "scriptArgs is invalid for shell execution");
    }
  
    if (isLinuxHost()) {
      /**
       * This requires to give permission to www-data to execute a couple of scripts. Update sudoers:
       * www-data ALL=(ALL) NOPASSWD: /home/nbrest/my.scripts/common/sudoers/www-data/exec-script.sh
       * www-data ALL=(ALL) NOPASSWD: /home/nbrest/my.scripts/lin/csv-my-scripts.sh
       * www-data ALL=(ALL) NOPASSWD: /home/nbrest/my.scripts/kamehouse/get-username.sh
       * www-data ALL=(ALL) NOPASSWD: /home/nbrest/my.scripts/lin/transmission/csv-torrents.sh
       */
      $username = trim(shell_exec("sudo /home/nbrest/my.scripts/kamehouse/get-username.sh"));
      $shellCommandOutput = shell_exec("sudo -u " . $username . " /home/nbrest/my.scripts/common/sudoers/www-data/exec-script.sh -s '" . $script . "' -a '" . $scriptArgs . "'");
    } else {
      $shellCommandOutput = shell_exec("C:/Users/nbrest/my.scripts/win/bat/git-bash.bat -c \"C:/Users/nbrest/my.scripts/common/sudoers/www-data/exec-script.sh -s '" . $script . "' -a '" . $scriptArgs . "'\"");
    }

    return $shellCommandOutput;
  }

  /**
   * Convert the specified bash output to html output.
   */
  function getHtmlOutput($shellCommandOutput) {
    $htmlCommandOutput = convertBashColorsToHtml($shellCommandOutput);
    $htmlCommandOutput = explode("\n", $htmlCommandOutput);
    return $htmlCommandOutput;
  }
?>