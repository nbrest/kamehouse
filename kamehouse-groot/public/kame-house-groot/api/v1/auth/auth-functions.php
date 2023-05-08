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
 * Unlock the session to enable multiple requests to be executed in parallel in the same session.
 */
function unlockSession() {
  session_write_close();
}

/**
 * Check if the user is logged in.
 */
function isLoggedIn() {
  if (isset($_SESSION['logged-in']) && isset($_SESSION['username'])) {
    return true;
  } else {
    return false;
  }
}

/**
 * Check if the user is an admin of kamehouse.
 */
function isAdminUser() {
  if (!isLoggedIn()) {
    return false;
  }
  return hasAdminRole($_SESSION['username']);
}

/**
 * Checks if an user has admin roles.
 */
function hasAdminRole($user) {
  $hasAdminRole = false;
  $roles = getRoles($user);
  if (!isset($roles) || count($roles) <= 0) {
    return false;
  }
  $arrLength = count($roles);
  for($i = 0; $i < $arrLength; $i++) {
    if ($roles[$i] == "ROLE_KAMISAMA") {
      $hasAdminRole = true; 
    }
  }
  return $hasAdminRole;
}

/**
 * Checks if the authorization header is set in the request.
 */
function isAuthorizationHeaderSet() {
  if (isset($_SERVER["PHP_AUTH_USER"]) && isset($_SERVER["PHP_AUTH_PW"])) {
    return true;
  } else {
    logToErrorFile("Authorization headers not set");
    return false;
  }
}

/**
 * Get the username from the auth header.
 */
function getUsernameFromAuthorizationHeader() {
  return $_SERVER["PHP_AUTH_USER"];
}

/**
 * Get the password from the auth header.
 */
function getPasswordFromAuthorizationHeader() {
  return $_SERVER["PHP_AUTH_PW"];
}

/**
 * Checks if the specified login credentials are valid for a kamehouse user.
 */
function isAuthorizedUser($username, $password) {
  if(!isValidInputForShell($username)) {
    logToErrorFile("Username '" . $username . "' has invalid characters for db access");
    return false;
  }

  if(!isValidInputForShell($password)) {
    logToErrorFile("Password for username '" . $username . "' has invalid characters for for db access");
    return false;
  }

  $isAuthorizedUser = false;
  
  $dbConfig = json_decode(getDatabaseConfig());
  $dbConnection = new mysqli($dbConfig->server, $dbConfig->username, $dbConfig->password, $dbConfig->database);
  if ($dbConnection->connect_error) {
    logToErrorFile("Database connection failed: " . $dbConnection->connect_error);
    return false;
  }

  $sql = "SELECT password FROM kamehouse_user where username = '$username'";
  $result = $dbConnection->query($sql);
  if ($result->num_rows == 1) {
    while($row = $result->fetch_assoc()) {
      if (password_verify($password, $row["password"])) {
        $isAuthorizedUser = true;
      }
    }
  }
  $dbConnection->close();

  return $isAuthorizedUser;
}

/**
 * Get the kamehouse roles for the specified user.
 */
function getRoles($username) {
  if(!isValidInputForShell($username)) {
    logToErrorFile("Username '" . $username . "' has invalid characters for db access");
    return [];
  }

  $roles = [];
  
  $dbConfig = json_decode(getDatabaseConfig());
  $dbConnection = new mysqli($dbConfig->server, $dbConfig->username, $dbConfig->password, $dbConfig->database);
  if ($dbConnection->connect_error) {
    logToErrorFile("Database connection failed: " . $dbConnection->connect_error);
    return false;
  }

  $sql = "SELECT name FROM kamehouse_role where kamehouse_user_id = (select id from kamehouse_user where username = '$username')";
  $result = $dbConnection->query($sql);
  if ($result->num_rows > 0) {
    while($row = $result->fetch_assoc()) {
      array_push($roles, $row["name"]);
    }
  }
  $dbConnection->close();

  return $roles;
}

function getDatabaseConfig() {
  return '{ 
    "server" : "localhost",
    "username" : "kameHouseUser",
    "password" : "kameHousePwd",
    "database" : "kameHouse"
  }';
}

/**
 * Start a new session.
 */
function initiateSession($username) {
  try {
    if(session_status() !== PHP_SESSION_ACTIVE) {
      logToErrorFile("Initiating session for user " . $username);
      ini_set('session.gc_maxlifetime', 0);
      session_set_cookie_params(0);
      session_start();
      session_regenerate_id();
      $_SESSION['logged-in'] = true;
      $_SESSION['username'] = $username;
      unlockSession();
    } else {
      logToErrorFile("Session already active for user " . $username);
      $_SESSION['logged-in'] = true;
      $_SESSION['username'] = $username;
    }
  } catch(Exception $e) {
    // session already open throws an exception, ignore it
  }
}

/**
 * End a current session.
 */
function endSession($username) {
  try {
    if(session_status() == PHP_SESSION_ACTIVE) {
      $_SESSION['logged-in'] = false;
      unset($_SESSION['username']);
      session_destroy();
    } else {
      //logToErrorFile("Session already ended for user " . $username);
    }
  } catch(Exception $e) {
    // session already open throws an exception, ignore it
  }
}

/**
 * Checks if the specified login credentials are valid executing a shell script to validate the user
 * with the .htpasswd file.
 * @deprecated. Moved to mysql auth.
 */
function authorizeUserDeprecated() {
  if(!isValidInputForShell($username)) {
    logToErrorFile("Username '" . $username . "' has invalid characters for shell");
    return false;
  }

  if(!isValidInputForShell($password)) {
    logToErrorFile("Password for username '" . $username . "' has invalid characters for shell");
    return false;
  }

  $isAuthorizedUser = false;
  $scriptArgs = "-u " . $username . " -p " . $password;

  logToErrorFile("Started executing script kamehouse/kamehouse-groot-login.sh");
  if (isLinuxHost()) {
    /**
     * This requires to give permission to www-data to execute. Check API execute.php for more details.
     */
    $shellUsername = trim(shell_exec("HOME=/var/www /var/www/programs/kamehouse-shell/bin/kamehouse/get-username.sh"));
    $shellCommandOutput = shell_exec("sudo -u " . $shellUsername . " /var/www/programs/kamehouse-shell/bin/common/sudoers/www-data/exec-script.sh -s 'kamehouse/kamehouse-groot-login.sh' -a '" . $scriptArgs . "'");
  } else {
    $shellCommandOutput = shell_exec("%USERPROFILE%/programs/kamehouse-shell/bin/win/bat/git-bash.bat -c \"~/programs/kamehouse-shell/bin/common/sudoers/www-data/exec-script.sh -s 'kamehouse/kamehouse-groot-login.sh' -a '" . $scriptArgs . "'\"");
  }
  logToErrorFile("Finished executing script kamehouse/kamehouse-groot-login.sh");
  $shellCommandOutput = explode("\n", $shellCommandOutput);

  foreach ($shellCommandOutput as $shellCommandOutputLine) {
    if (startsWith($shellCommandOutputLine, 'loginStatus=SUCCESS')) {
      $isAuthorizedUser = true;
    }
  }
  
  return $isAuthorizedUser;
}
?> 
