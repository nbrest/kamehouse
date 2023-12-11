<?php
/**
 * [INTERNAL] Endpoint: /kame-house-groot/api/v1/admin/kamehouse-shell/kamehouse-shell.php (GET)
 * 
 * To be imported from other php files. Not to be directly called from frontend code.
 * 
 * Class definition of kamehouse shell.
 * 
 * @author nbrest
 */
global $kameHouse;
$kameHouse->setShell(new KameHouseShell());

class KameHouseShell {

  /**
   * execute a kamehouse shell script.
   */
  public function execute() {
    global $kameHouse;
    $this->configSession();
    $script = isset($_GET['script']) ? $_GET['script'] : '';
    $scriptArgs = isset($_GET['args']) ? $_GET['args'] : '';
    $executeOnDockerHost = isset($_GET['executeOnDockerHost']) ? $_GET['executeOnDockerHost'] : '';
    $executeOnDockerHost = $kameHouse->util->string->getBoolean($executeOnDockerHost);

    $shellCommandOutput = $this->executeShellScript($script, $scriptArgs, $executeOnDockerHost);
    $htmlCommandOutput = $this->getHtmlOutput($shellCommandOutput);
  
    $consoleOutput = [ 'htmlConsoleOutput' => $htmlCommandOutput, 'bashConsoleOutput' => $shellCommandOutput ];
  
    $kameHouse->core->setJsonResponseBody($consoleOutput);
  }

  /**
   * Get a list of all kamehouse shell scripts.
   */
  public function getScripts() {
    global $kameHouse;

    $kameHouseShellCSV = "";
    
    if ($kameHouse->core->isLinuxHost()) {
      $kameHouseShellCSV = trim(shell_exec("HOME=/var/www /var/www/programs/kamehouse-shell/bin/lin/csv-kamehouse-shell.sh"));
    } else {
      $kameHouseShellCSV = trim(shell_exec("%USERPROFILE%/programs/kamehouse-shell/bin/win/bat/git-bash.bat -c \"~/programs/kamehouse-shell/bin/win/csv-kamehouse-shell.sh\""));
    }
  
    if (empty($kameHouseShellCSV)) {
      $kameHouseShellCSV = "couldn-find-scripts.sh";
    }
  
    $kameHouseShellArray = explode(",", $kameHouseShellCSV);
  
    $kameHouse->core->setJsonResponseBody($kameHouseShellArray);
  }  

  /**
   * Config session for kamehouse shell script execution.
   */
  private function configSession() {
    // Disable time_limit and max_execution_time (mainly for scp-torrent.sh script)
    set_time_limit(0);
    ini_set('max_execution_time', 0);
  }

  /**
   * Execute the specified script. 
   */
  private function executeShellScript($script, $scriptArgs, $executeOnDockerHost) {
    global $kameHouse;
    if ($scriptArgs == 'null') {
      $scriptArgs = '';
    }

    if(!$kameHouse->util->string->isValidInputForShell($script)) {
      $kameHouse->logger->info("Script " . $script . " is invalid for shell execution");
      $kameHouse->core->exitWithError(400, "script is invalid for shell execution");
    }

    if(!$kameHouse->util->string->isValidInputForShell($scriptArgs)) {
      $kameHouse->logger->info("Script arguments for script " . $script . " are invalid for shell execution");
      $kameHouse->core->exitWithError(400, "scriptArgs is invalid for shell execution");
    }
    $shellCommand = $this->buildShellCommand($script, $scriptArgs, $executeOnDockerHost);
    $kameHouse->logger->info("Started executing script " . $script);
    $shellCommandOutput = shell_exec($shellCommand);
    $kameHouse->logger->info("Finished executing script " . $script);
    return $shellCommandOutput;
  }

  /**
   * Build the shell command to execute either on windows or linux.
   */
  private function buildShellCommand($script, $scriptArgs, $executeOnDockerHost) {
    global $kameHouse;
    $shellCommand = "";
    if ($kameHouse->core->isLinuxHost()) {
      /**
       * This requires to give permission to www-data to execute a couple of scripts. 
       * Update sudoers:
       *  www-data ALL=(ALL) NOPASSWD: /var/www/programs/kamehouse-shell/bin/common/sudoers/www-data/exec-script.sh
       * Make sure the script `install-kamehouse-groot.sh` was executed as well to access get-username.sh and exec-script.sh from the user www-data through groot.
       */
      $username = trim(shell_exec("HOME=/var/www /var/www/programs/kamehouse-shell/bin/kamehouse/get-username.sh"));
      $shellCommand = "sudo -u " . $username . " /var/www/programs/kamehouse-shell/bin/common/sudoers/www-data/exec-script.sh";
    } else {
      $shellCommand = "%USERPROFILE%/programs/kamehouse-shell/bin/win/bat/git-bash.bat -c \"~/programs/kamehouse-shell/bin/common/sudoers/www-data/exec-script.sh";
    }
    if ($executeOnDockerHost) {
      $shellCommand = $shellCommand . " -x";
    }
    $shellCommand = $shellCommand . " -s '" . $script . "' -a '" . $scriptArgs . "'";
    if (!$kameHouse->core->isLinuxHost()) {
      $shellCommand . "\"";
    }
    return $shellCommand;   
  }

  /**
   * Convert the specified bash output to html output.
   */
  private function getHtmlOutput($shellCommandOutput) {
    global $kameHouse;
    $htmlCommandOutput = $kameHouse->util->string->convertBashColorsToHtml($shellCommandOutput);
    $htmlCommandOutput = explode("\n", $htmlCommandOutput);
    return $htmlCommandOutput;
  }
  
} // KameHouseShell
?>