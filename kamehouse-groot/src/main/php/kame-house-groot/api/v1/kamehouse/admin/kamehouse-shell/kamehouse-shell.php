<?php
/**
 * Class definition of kamehouse shell.
 * 
 * @author nbrest
 */
class KameHouseShell {

  /**
   * Get the exec-script to execute shell scripts on windows.
   */
  public function getShellScriptsBasePath() {
    return "/programs/kamehouse-shell/bin/";
  }

  /**
   * execute a kamehouse shell script and return a kamehouse command result.
   */
  public function execute() {
    global $kameHouse;
    $this->configSession();
    $script = isset($_GET['script']) ? $_GET['script'] : '';
    $scriptArgs = isset($_GET['args']) ? $_GET['args'] : '';
    $executeOnDockerHost = isset($_GET['executeOnDockerHost']) ? $_GET['executeOnDockerHost'] : '';
    $executeOnDockerHost = $kameHouse->util->string->getBoolean($executeOnDockerHost);

    $shellConsoleOutput = $this->executeShellScript($script, $scriptArgs, $executeOnDockerHost);
    $htmlConsoleOutput = $this->getHtmlOutput($shellConsoleOutput);
  
    $kameHouseCommandResult = [ 'htmlConsoleOutput' => $htmlConsoleOutput, 'bashConsoleOutput' => $shellConsoleOutput ];
  
    $kameHouse->core->setJsonResponseBody($kameHouseCommandResult);
  }

  /**
   * Get a list of all kamehouse shell scripts using the specified shell scripts to get the csv.
   */
  public function getScripts($csvScript) {
    global $kameHouse;

    $kameHouseShellCSV = "";
    
    if ($kameHouse->core->isLinuxHost()) {
      $kameHouse->core->loadKameHouseUserToEnv();
      $username = getenv("KAMEHOUSE_USER");
      $suScript = $this->getSuScript();
      $kameHouseShellCSV = trim(shell_exec("sudo /home/" . $username . $suScript . " -s " . $csvScript));
    } else {
      $shellScriptsBasePath = $this->getShellScriptsBasePath();
      $kameHouseShellCSV = trim(shell_exec("%USERPROFILE%/programs/kamehouse-shell/bin/win/bat/git-bash.bat -c \"~" . $shellScriptsBasePath . $csvScript . "\""));
    }
  
    if (empty($kameHouseShellCSV)) {
      $kameHouseShellCSV = "couldn-find-scripts.sh";
    }
  
    $kameHouseShellArray = explode(",", $kameHouseShellCSV);
  
    $kameHouse->core->setJsonResponseBody($kameHouseShellArray);
  }  

  /**
   * Config the config for kamehouse groot.
   */
  public function getGrootConfig() {
    global $kameHouse;
    $kameHouse->core->loadKameHouseUserToEnv();
    $username = getenv("KAMEHOUSE_USER");
    $grootConfigData = trim(shell_exec("sudo /home/" . $username . "/programs/kamehouse-shell/bin/common/sudoers/www-data/su.sh -s common/sudoers/www-data/groot-get-config.sh"));
    $grootConfigArray = explode("\n", $grootConfigData);
    $grootConfig = '';
    foreach ($grootConfigArray as $grootConfigEntry){
      preg_match("/([^#]+)\=(.*)/", $grootConfigEntry, $matches);
      if (isset($matches[2])) {
        if ($kameHouse->util->string->startsWith($grootConfigEntry, "MARIADB_PASS_KAMEHOUSE=")) {
          $grootConfig = $grootConfig . $grootConfigEntry . "\n";
        }
      }
    } 
    return $grootConfig;
  }  

  /**
   * Get the su script to execute shell scripts on linux.
   */
  private function getSuScript() {
    return $this->getShellScriptsBasePath() . "common/sudoers/www-data/su.sh";
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
      $kameHouse->logger->info("Script arguments [" . $scriptArgs . "] for script " . $script . " are invalid for shell execution");
      $kameHouse->core->exitWithError(400, "scriptArgs is invalid for shell execution");
    }
    $shellCommand = $this->buildShellCommand($script, $scriptArgs, $executeOnDockerHost);
    $kameHouse->logger->info("Started executing script " . $script);
    $kameHouse->logger->info("Running shell command " . $shellCommand);
    shell_exec($shellCommand);
    $userHome = $this->getUserHome();
    $scriptLog = $userHome . "/logs/" . substr(basename($script), 0, -2) . "log";
    $kameHouse->logger->info("scriptLog " . $scriptLog);
    $shellOutout = file_get_contents($scriptLog);

    $kameHouse->logger->info("Finished executing script " . $script);
    return $shellOutout;
  }

  /**
   * Return the kamehouse user home.
   */
  private function getUserHome() {
    global $kameHouse;
    if ($kameHouse->core->isLinuxHost()) {
      /**
       * Run `install-kamehouse-groot.sh` and `set-kamehouse-sudoers-permissions.sh` to successfully execute kamehouse shell scripts through groot.
       */
      $kameHouse->core->loadKameHouseUserToEnv();
      $username = getenv("KAMEHOUSE_USER");
      return "/home/" . $username;
    } else {
      return "C:/Users/" . get_current_user();
    }
  }

  /**
   * Build the shell command to execute either on windows or linux.
   */
  private function buildShellCommand($script, $scriptArgs, $executeOnDockerHost) {
    global $kameHouse;
    $shellCommand = "";
    $userHome = $this->getUserHome();
    if ($kameHouse->core->isLinuxHost()) {
      $suScript = $this->getSuScript();
      $shellCommand = "sudo " . $userHome . $suScript;
    } else {
      $shellScriptsBasePath = $this->getShellScriptsBasePath();
      $shellCommand = "%USERPROFILE%/programs/kamehouse-shell/bin/win/bat/git-bash.bat -c \"~" . $shellScriptsBasePath . "common/sudoers/www-data/exec-script.sh";
    }
    if ($executeOnDockerHost) {
      $shellCommand = $shellCommand . " -x";
    }
    if ($kameHouse->core->isLinuxHost()) {
      $shellCommand = $shellCommand . " -s \'" . $script . "\' -a \'" . $scriptArgs . "\'";
    } else {
      $shellCommand = $shellCommand . " -s '" . $script . "' -a '" . $scriptArgs . "' \"";
    }
    return $shellCommand;
  }

  /**
   * Convert the specified bash output to html output.
   */
  private function getHtmlOutput($shellKameHouseCommandResult) {
    global $kameHouse;
    $htmlKameHouseCommandResult = $kameHouse->util->string->convertBashColorsToHtml($shellKameHouseCommandResult);
    $htmlKameHouseCommandResult = explode("\n", $htmlKameHouseCommandResult);
    return $htmlKameHouseCommandResult;
  }
  
} // KameHouseShell
?>