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
   * Returns true if the script is forbidden for shell execution.
   */
  public function isForbiddenScript($script, $scriptArgs) {
    global $kameHouse;

    if ($kameHouse->util->string->contains($script, "kamehouse-cmd.sh") 
        && $kameHouse->util->string->contains($scriptArgs, "decrypt")) {
      return true;
    }

    return $kameHouse->util->string->contains($script, "www-data/su.sh") 
      || $kameHouse->util->string->contains($script, "www-data-shell.sh")
      || $kameHouse->util->string->contains($script, "get-kamehouse-secret.sh")
      || $kameHouse->util->string->contains($script, "edit-kamehouse-secrets.sh")
      || $kameHouse->util->string->contains($script, "docker-ssh-")
      || $kameHouse->util->string->contains($script, "ssh.sh");
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
    $isDaemon = isset($_GET['isDaemon']) ? $_GET['isDaemon'] : '';
    $isDaemon = $kameHouse->util->string->getBoolean($isDaemon);

    $standardOutput = $this->executeShellScript($script, $scriptArgs, $executeOnDockerHost, $isDaemon);
    $standardOutputHtml = $this->getStandardOutputHtml($standardOutput);
  
    $status = "completed";
    $exitCode = 0;
    if ($isDaemon) {
      $status = "running";
      $exitCode = -1;
    }

    $kameHouseCommandResult = [ 
      'command' => $script,
      'exitCode' => $exitCode,
      'pid' => -1,
      'status' => $status,
      'standardOutput' => $standardOutput,
      'standardOutputHtml' => $standardOutputHtml, 
      'standardError' => [],
      'standardErrorHtml' => [] 
    ];
  
    $kameHouse->core->setJsonResponseBody($kameHouseCommandResult);
  }

  /**
   * Get a list of all kamehouse shell scripts using the specified shell scripts to get the csv.
   */
  public function getScripts($csvScript) {
    global $kameHouse;

    $kameHouseShellCSV = "";
    
    if ($kameHouse->core->isLinuxHost()) {
      $username = $kameHouse->core->getKameHouseUser();
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
   * Get the specified kamehouse secret from the encrypted store.
   */
  public function getKameHouseSecret($secretKey) {
    global $kameHouse;
    if ($kameHouse->core->isLinuxHost()) {
      $username = $kameHouse->core->getKameHouseUser();
      return rtrim(shell_exec("sudo /home/" . $username . "/programs/kamehouse-shell/bin/common/sudoers/www-data/su.sh kamehouse/get-kamehouse-secret.sh -s " . $secretKey));
    } else {
      $shellScriptsBasePath = $this->getShellScriptsBasePath();
      $shellCommand = "%USERPROFILE%/programs/kamehouse-shell/bin/win/bat/git-bash-silent.bat -c \"~" . $shellScriptsBasePath . "kamehouse/get-kamehouse-secret.sh -s " . $secretKey;
      return rtrim(shell_exec($shellCommand));
    }
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
  private function executeShellScript($script, $scriptArgs, $executeOnDockerHost, $isDaemon) {
    global $kameHouse;
    if ($scriptArgs == 'null') {
      $scriptArgs = '';
    }

    if($this->isForbiddenScript($script, $scriptArgs)) {
      $kameHouse->logger->info("Script " . $script . " is forbidden for shell execution");
      $kameHouse->core->exitWithError(403, "script is forbidden for shell execution");
    }

    if(!$kameHouse->util->string->isValidInputForShell($script)) {
      $kameHouse->logger->info("Script " . $script . " is invalid for shell execution");
      $kameHouse->core->exitWithError(400, "script is invalid for shell execution");
    }

    if(!$kameHouse->util->string->isValidInputForShell($scriptArgs)) {
      $kameHouse->logger->info("Script arguments [" . $scriptArgs . "] for script " . $script . " are invalid for shell execution");
      $kameHouse->core->exitWithError(400, "scriptArgs is invalid for shell execution");
    }
    $shellCommand = $this->buildShellCommand($script, $scriptArgs, $executeOnDockerHost, $isDaemon);
    $kameHouse->logger->info("Started executing script " . $script);
    $kameHouse->logger->info("Running shell command " . $shellCommand);
    $standardOutput = "";
    if ($isDaemon) {
        // triggers the script in the background and return the api response after a couple of seconds
      if ($kameHouse->core->isLinuxHost()) {
        $timer = popen($shellCommand . " > /dev/null 2>&1 &", "r"); 
        sleep(5);
        pclose($timer);
      } else {
        $timer = popen("start /B ". $shellCommand, "r"); 
        sleep(5);
        pclose($timer);
      }
    } else {
      $standardOutput = shell_exec($shellCommand);
    }
    $userHome = $this->getUserHome();
    $scriptLog = $userHome . "/logs/" . substr(basename($script), 0, -2) . "log";
    if (file_exists($scriptLog)) {
      // if the script log exits, replace the script output with the log file, which is more reliable than shell_exec output
      $kameHouse->logger->info("Using script log to get the output of " . $script);
      $standardOutput = file_get_contents($scriptLog);
    } else {
      $kameHouse->logger->info("Couldnt find log file '" . $scriptLog . "' for script '" . $script . "'. Using shell_exec output");
    }
    $kameHouse->logger->info("Finished executing script " . $script);
    return explode("\n", $standardOutput);
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
      $username = $kameHouse->core->getKameHouseUser();
      return "/home/" . $username;
    } else {
      return "C:/Users/" . get_current_user();
    }
  }

  /**
   * Build the shell command to execute either on windows or linux.
   */
  private function buildShellCommand($script, $scriptArgs, $executeOnDockerHost, $isDaemon) {
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
      $shellCommand = $shellCommand . " --execute-on-docker-host";
    }
    if ($isDaemon) {
      $shellCommand = $shellCommand . " --daemon"; 
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
  private function getStandardOutputHtml($standardOutput) {
    global $kameHouse;
    if (empty($standardOutput)) {
      return [];
    }
    $standardOutputHtml = [];
    foreach ($standardOutput as $line) {
      array_push($standardOutputHtml, $kameHouse->util->string->convertBashColorsToHtml($line));
    }
    return $standardOutputHtml;
  }
  
} // KameHouseShell
?>