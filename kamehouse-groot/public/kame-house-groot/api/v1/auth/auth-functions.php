<?php
/**
 * Endpoint: /kame-house-groot/api/v1/auth/auth-functions.php
 * 
 * [INTERNAL] - To be imported from other php files. Not to be directly called from frontend code.
 * 
 * Common functions used in the /auth APIs
 * 
 * @author nbrest
 */

 /**
 * 
 */
function unlockSession() {
  session_write_close();
}

/**
 * 
 */
function isLoggedIn() {
  if (isset($_SESSION['logged-in'])) {
    return true; 
  } else {
    return false;
  }
}

/**
 * 
 */
function isAuthorizationHeaderSet() {
  if (isset($_SERVER["PHP_AUTH_USER"]) && isset($_SERVER["PHP_AUTH_PW"])) {
    return true;
  } else {
    return false;
  }
}

/**
 * 
 */
function getUsernameFromAuthorizationHeader() {
  return $_SERVER["PHP_AUTH_USER"];
}

/**
 * 
 */
function getPasswordFromAuthorizationHeader() {
  return $_SERVER["PHP_AUTH_PW"];
}

/**
 * 
 */
function isAuthorizedUser($username, $password) {
  if(!isValidInputForShell($username)) {
    return false;
  }

  if(!isValidInputForShell($password)) {
    return false;
  }

  $isAuthorizedUser = false;
  $scriptArgs = $username . " " . $password;

  if (isLinuxHost()) {
    /**
     * This requires to give permission to www-data to execute. Check API exec-script.php for more details.
     */
    $shellUsername = trim(shell_exec("sudo /home/nbrest/my.scripts/kamehouse/get-username.sh"));
    $shellCommandOutput = shell_exec("sudo -u " . $shellUsername . " /home/nbrest/my.scripts/common/sudoers/www-data/exec-script.sh -s 'kamehouse/groot-login.sh' -a '" . $scriptArgs . "'");
  } else {
    $shellCommandOutput = shell_exec("C:/Users/nbrest/my.scripts/win/bat/git-bash.bat -c \"C:/Users/nbrest/my.scripts/common/sudoers/www-data/exec-script.sh -s 'kamehouse/groot-login.sh' -a '" . $scriptArgs . "'\"");
  }
  $shellCommandOutput = explode("\n", $shellCommandOutput);

  foreach ($shellCommandOutput as $shellCommandOutputLine) {
    if (startsWith($shellCommandOutputLine, 'loginStatus=SUCCESS')) {
      $isAuthorizedUser = true;
    }
  }
  
  return $isAuthorizedUser;
}
?> 
