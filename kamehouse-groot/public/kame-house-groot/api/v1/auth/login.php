<?php
/**
 * Authenticate the specified user and password.
 * 
 * @author nbrest
 */
  main();
?>

<?php

  /**
   * Authenticate the user with the received credentials. 
   * If successful redirect to home. If login unsuccessful redirect back to login page.
   */
  function main() {
    init();

    if (!isset($_POST['username'], $_POST['password'])) {
      redirectLoginError();
    }
    $username = $_POST['username'];
    validateUsernamePasswordFormat($username);

    $password = $_POST['password'];
    validateUsernamePasswordFormat($password);

    if (isValidUsernameAndPassword($username, $password)) {
      initiateSession($username);
      redirectLoginSuccess();
    } else {
      redirectLoginError();
    } 
  }

  function init() {
    require_once("../../../api/v1/commons/global.php");
  }

  function initiateSession($username) {
    session_start();
    session_regenerate_id();
    $_SESSION['logged-in'] = true;
    $_SESSION['username'] = $username;
  }

  function redirectLoginSuccess() {
    $redirectUrl = "/kame-house-groot/";
    if (isset($_POST['referrer']) && startsWith($_POST['referrer'], "/kame-house-groot/")) {
      $redirectUrl = $_POST['referrer'];
    }
    header('Location: ' . $redirectUrl);
    exit;
  }

  function redirectLoginError() {
    header('Location: /kame-house-groot/login.html?error');
    exit;
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
      redirectLoginError();
    }
    if (strlen($credential) > $MAX_LENGTH) {
      redirectLoginError();
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
      redirectLoginError();
    }
  }
?>