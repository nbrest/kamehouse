<?php
/**
 * Endpoint: /kame-house-groot/api/v1/admin/my-scripts/exec-script.php (GET)
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
    $executeOnDockerHost = isset($_GET['executeOnDockerHost']) ? $_GET['executeOnDockerHost'] : '';
    $executeOnDockerHost = getBoolean($executeOnDockerHost);

    $shellCommandOutput = executeShellScript($script, $scriptArgs, $executeOnDockerHost);
    $htmlCommandOutput = getHtmlOutput($shellCommandOutput);
  
    $consoleOutput = [ 'htmlConsoleOutput' => $htmlCommandOutput, 'bashConsoleOutput' => $shellCommandOutput ];
  
    setJsonResponseBody($consoleOutput);
  }

  function init() {
    ini_set('session.gc_maxlifetime', 0);
    session_set_cookie_params(0);
    session_start();
    require_once("../../../../api/v1/commons/global.php");
    require_once("../../../../api/v1/auth/authorize-api.php");
    unlockSession();
    // Disable time_limit and max_execution_time (mainly for scp-torrent.sh script)
    set_time_limit(0);
    ini_set('max_execution_time', 0);
  }

  /**
   * Execute the specified script. 
   */
  function executeShellScript($script, $scriptArgs, $executeOnDockerHost) {
    if ($scriptArgs == 'null') {
      $scriptArgs = '';
    }

    if(!isValidInputForShell($script)) {
      exitWithError(400, "script is invalid for shell execution");
    }

    if(!isValidInputForShell($scriptArgs)) {
      exitWithError(400, "scriptArgs is invalid for shell execution");
    }
    $shellCommand = buildShellCommand($script, $scriptArgs, $executeOnDockerHost);
    $shellCommandOutput = shell_exec($shellCommand);
    return $shellCommandOutput;
  }

  /**
   * Build the shell command to execute either on windows or linux.
   */
  function buildShellCommand($script, $scriptArgs, $executeOnDockerHost) {
    $shellCommand = "";
    if (isLinuxHost()) {
      /**
       * This requires to give permission to www-data to execute a couple of scripts. Update sudoers:
       * www-data ALL=(ALL) NOPASSWD: /home/nbrest/my.scripts/common/sudoers/www-data/exec-script.sh
       * www-data ALL=(ALL) NOPASSWD: /home/nbrest/my.scripts/lin/csv-my-scripts.sh
       * www-data ALL=(ALL) NOPASSWD: /home/nbrest/my.scripts/kamehouse/get-username.sh
       * www-data ALL=(ALL) NOPASSWD: /home/nbrest/my.scripts/lin/transmission/csv-torrents.sh
       */
      $username = trim(shell_exec("sudo /home/nbrest/my.scripts/kamehouse/get-username.sh"));
      $shellCommand = "sudo -u " . $username . " /home/nbrest/my.scripts/common/sudoers/www-data/exec-script.sh";
    } else {
      $shellCommand = "C:/Users/nbrest/my.scripts/win/bat/git-bash.bat -c \"C:/Users/nbrest/my.scripts/common/sudoers/www-data/exec-script.sh";
    }
    if ($executeOnDockerHost) {
      $shellCommand = $shellCommand . " -x";
    }
    $shellCommand = $shellCommand . " -s '" . $script . "' -a '" . $scriptArgs . "'";
    if (!isLinuxHost()) {
      $shellCommand . "\"";
    }
    return $shellCommand;   
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