<?php
/**
 * Check if an Authorization header was sent or if is the user is logged in.
 * There's no roles in GRoot, so if the user is logged in, it has access to any page.
 * 
 * Use this endpoint in other GRoot API endpoints that require an authenticated user by calling:
 * `require_once("../../../../api/v1/auth/authorize-api.php");`
 * At the beginning of the init() method of the endpoint that needs securing.
 * 
 * @author nbrest
 */
  mainAuthorizeApi();
?> 

<?php

  function mainAuthorizeApi() {

    // Check that the user is logged in
    if (isset($_SESSION['logged-in'])) {
      return; 
    }

    if (isset($_SERVER["PHP_AUTH_USER"]) && isset($_SERVER["PHP_AUTH_PW"])) {
      $username = $_SERVER["PHP_AUTH_USER"];
      validateUsernamePasswordFormat($username);

      $password = $_SERVER["PHP_AUTH_PW"];  
      validateUsernamePasswordFormat($password);
  
      if (isValidUsernameAndPassword($username, $password)) {
        return;
      } else {
        exitWithError(401, "Invalid username and password");
      }
    }
    exitWithError(401, "Login to /kame-house-groot to access this endpoint");
  }

  function isValidUsernameAndPassword($username, $password) {
    $isValidUsernameAndPassword = false;

    $scriptArgs = $username . " " . $password;

    if (isLinuxHost()) {
      /**
       * This requires to give permission to www-data to execute. Check exec-script.php for more details.
       */
      $shellUsername = trim(shell_exec("sudo /home/nbrest/my.scripts/kamehouse/get-username.sh"));
      $shellCommandOutput = shell_exec("sudo -u " . $shellUsername . " /home/nbrest/my.scripts/common/sudoers/www-data/exec-script.sh -s 'kamehouse/groot-login.sh' -a '" . $scriptArgs . "'");
    } else {
      $shellCommandOutput = shell_exec("C:/Users/nbrest/my.scripts/win/bat/git-bash.bat -c \"C:/Users/nbrest/my.scripts/common/sudoers/www-data/exec-script.sh -s 'kamehouse/groot-login.sh' -a '" . $scriptArgs . "'\"");
    }
    $shellCommandOutput = explode("\n", $shellCommandOutput);

    foreach ($shellCommandOutput as $shellCommandOutputLine) {
      if (startsWith($shellCommandOutputLine, 'loginStatus=SUCCESS')) {
        $isValidUsernameAndPassword = true;
      }
    }
    return $isValidUsernameAndPassword;
  }

  /** 
   * Validate that the username and password don't contain unauthorized format.
   * I need to filter all these to be safe because the username and password are passed as parameters to a bash script.
   */
  function validateUsernamePasswordFormat($credential) {
    $MAX_LENGTH = 50;

    if (isEmptyStr($credential)) {
      exitWithError(401, "Invalid username and password");
    }
    if (strlen($credential) > $MAX_LENGTH) {
      exitWithError(401, "Invalid username and password");
    }
    validateUnauthorizedChar($credential, ">");
    validateUnauthorizedChar($credential, "<");
    validateUnauthorizedChar($credential, ";");
    validateUnauthorizedChar($credential, ":");
    validateUnauthorizedChar($credential, "|");
    #validateUnauthorizedChar($credential, "%");
    validateUnauthorizedChar($credential, "&");
    validateUnauthorizedChar($credential, "*");
    validateUnauthorizedChar($credential, "(");
    validateUnauthorizedChar($credential, ")");
    validateUnauthorizedChar($credential, "{");
    validateUnauthorizedChar($credential, "}");
    validateUnauthorizedChar($credential, "[");
    validateUnauthorizedChar($credential, "]");    
    #validateUnauthorizedChar($credential, "@");
    #validateUnauthorizedChar($credential, "!");
    #validateUnauthorizedChar($credential, "$");
    #validateUnauthorizedChar($credential, "?");
    validateUnauthorizedChar($credential, "^");
    validateUnauthorizedChar($credential, "\"");
    validateUnauthorizedChar($credential, "'");
    #validateUnauthorizedChar($credential, "#");
    validateUnauthorizedChar($credential, "\\");
    validateUnauthorizedChar($credential, ",");
    validateUnauthorizedChar($credential, "`");
    validateUnauthorizedChar($credential, "..");
    validateUnauthorizedChar($credential, " ");
  }

  /**
   * Validate that the specified param doesn't contain the specified invalid character sequence.
   */
  function validateUnauthorizedChar($param, $invalidCharSequence) {
    if (contains($param, $invalidCharSequence)) {
      exitWithError(401, "Invalid username and password");
    }
  }  
?>