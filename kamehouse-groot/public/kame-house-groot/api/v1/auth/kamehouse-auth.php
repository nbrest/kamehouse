<?php
/**
 * Endpoint: /kame-house-groot/api/v1/auth/kamehouse-auth.php
 * 
 * [INTERNAL] - To be imported from other php files. Not to be directly called from frontend code.
 * 
 * Common functions used in the /auth APIs
 * 
 * @author nbrest
 */
global $kameHouse;
$kameHouse->setAuth(new KameHouseAuth());

class KameHouseAuth {

  /**
   * Authorize api. Validate that the user is logged in and admin.
   */
  public function authorizeApi() {
    global $kameHouse;
    $this->startSession();
    $this->unlockSession();

    if ($this->isAdminUser()) {
      return;
    }

    if ($this->isAuthorizationHeaderSet()) {
      $username = $this->getUsernameFromAuthorizationHeader();
      $password = $this->getPasswordFromAuthorizationHeader();

      if ($this->isAuthorizedUser($username, $password) && $this->hasAdminRole($username)) {
        return;
      } else {
        $kameHouse->logger->info("Invalid username and password");
        $kameHouse->core->exitWithError(401, "Invalid username and password");
      }
    }

    $kameHouse->core->exitWithError(401, "Login as admin to /kame-house-groot to access this endpoint");
  }

  /**
   * Check if there's an active session of an admin user, otherwise redirect to login page.
   */
  public function authorizePage() {
    $this->startSession();
    $this->unlockSession();

    if ($this->isAdminUser()) {
      return;
    }

    if (isset($_SERVER['REQUEST_URI'])) {
      header('Location: /kame-house-groot/login.html?unauthorizedPageAccess=true&referrer=' . $_SERVER['REQUEST_URI']);
      exit;
    }

    header('Location: /kame-house-groot/login.html?unauthorizedPageAccess=true');
  	exit;
  }

  /**
   * Unlock the session to enable multiple requests to be executed in parallel in the same session.
   */
  public function unlockSession() {
    session_write_close();
  }

  /**
   * Check if the user is an admin of kamehouse.
   */
  public function isAdminUser() {
    if (!$this->isLoggedIn()) {
      return false;
    }
    return $this->hasAdminRole($_SESSION['username']);
  }

  /**
   * Checks if an user has admin roles.
   */
  public function hasAdminRole($user) {
    $hasAdminRole = false;
    $roles = $this->getRoles($user);
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
  public function isAuthorizationHeaderSet() {
    global $kameHouse;
    if (isset($_SERVER["PHP_AUTH_USER"]) && isset($_SERVER["PHP_AUTH_PW"])) {
      return true;
    } else {
      $kameHouse->logger->info("Authorization headers not set");
      return false;
    }
  }

  /**
   * Get the username from the auth header.
   */
  public function getUsernameFromAuthorizationHeader() {
    return $_SERVER["PHP_AUTH_USER"];
  }

  /**
   * Get the password from the auth header.
   */
  public function getPasswordFromAuthorizationHeader() {
    return $_SERVER["PHP_AUTH_PW"];
  }

  /**
   * Checks if the specified login credentials are valid for a kamehouse user.
   */
  public function isAuthorizedUser($username, $password) {
    global $kameHouse;
    if(!$kameHouse->util->string->isValidInputForShell($username)) {
      $kameHouse->logger->info("Username '" . $username . "' has invalid characters for db access");
      return false;
    }

    if(!$kameHouse->util->string->isValidInputForShell($password)) {
      $kameHouse->logger->info("Password for username '" . $username . "' has invalid characters for for db access");
      return false;
    }

    $isAuthorizedUser = false;
    
    $dbConfig = json_decode($this->getDatabaseConfig());
    $dbConnection = new mysqli($dbConfig->server, $dbConfig->username, $dbConfig->password, $dbConfig->database);
    if ($dbConnection->connect_error) {
      $kameHouse->logger->info("Database connection failed: " . $dbConnection->connect_error);
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
  public function getRoles($username) {
    global $kameHouse;
    if(!$kameHouse->util->string->isValidInputForShell($username)) {
      $kameHouse->logger->info("Username '" . $username . "' has invalid characters for db access");
      return [];
    }

    $roles = [];
    
    $dbConfig = json_decode($this->getDatabaseConfig());
    $dbConnection = new mysqli($dbConfig->server, $dbConfig->username, $dbConfig->password, $dbConfig->database);
    if ($dbConnection->connect_error) {
      $kameHouse->logger->info("Database connection failed: " . $dbConnection->connect_error);
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

  /**
   * Start a new session.
   */
  public function initiateSession($username) {
    global $kameHouse;
    try {
      if(session_status() !== PHP_SESSION_ACTIVE) {
        $kameHouse->logger->info("Initiating session for user " . $username);
        $this->startSession();
        session_regenerate_id();
        $_SESSION['logged-in'] = true;
        $_SESSION['username'] = $username;
        $this->unlockSession();
      } else {
        $kameHouse->logger->info("Session already active for user " . $username);
        $_SESSION['logged-in'] = true;
        $_SESSION['username'] = $username;
      }
    } catch(Exception $e) {
      // session already open throws an exception, ignore it
    }
  }

  /**
   * Start session.
   */
  public function startSession() {
    ini_set('session.gc_maxlifetime', 0);
    session_set_cookie_params(0);
    session_start();
  }

  /**
   * End a current session.
   */
  public function endSession($username) {
    global $kameHouse;
    try {
      if(session_status() == PHP_SESSION_ACTIVE) {
        $_SESSION['logged-in'] = false;
        unset($_SESSION['username']);
        session_destroy();
      } else {
        //$kameHouse->logger->info("Session already ended for user " . $username);
      }
    } catch(Exception $e) {
      // session already open throws an exception, ignore it
    }
  }

  /**
   * Checks if the specified login credentials are valid executing a shell script to validate the user
   * with the .htpasswd file.
   * @deprecated. Moved to mariadb auth.
   */
  private function authorizeUserDeprecated() {
    global $kameHouse;
    if(!$kameHouse->util->string->isValidInputForShell($username)) {
      $kameHouse->logger->info("Username '" . $username . "' has invalid characters for shell");
      return false;
    }

    if(!$kameHouse->util->string->isValidInputForShell($password)) {
      $kameHouse->logger->info("Password for username '" . $username . "' has invalid characters for shell");
      return false;
    }

    $isAuthorizedUser = false;
    $scriptArgs = "-u " . $username . " -p " . $password;

    $kameHouse->logger->info("Started executing script kamehouse/kamehouse-groot-login.sh");
    if ($kameHouse->core->isLinuxHost()) {
      /**
       * This requires to give permission to www-data to execute. Check API execute.php for more details.
       */
      $shellUsername = trim(shell_exec("HOME=/var/www /var/www/programs/kamehouse-shell/bin/kamehouse/get-username.sh"));
      $shellCommandOutput = shell_exec("sudo -u " . $shellUsername . " /var/www/programs/kamehouse-shell/bin/common/sudoers/www-data/exec-script.sh -s 'kamehouse/kamehouse-groot-login.sh' -a '" . $scriptArgs . "'");
    } else {
      $shellCommandOutput = shell_exec("%USERPROFILE%/programs/kamehouse-shell/bin/win/bat/git-bash.bat -c \"~/programs/kamehouse-shell/bin/common/sudoers/www-data/exec-script.sh -s 'kamehouse/kamehouse-groot-login.sh' -a '" . $scriptArgs . "'\"");
    }
    $kameHouse->logger->info("Finished executing script kamehouse/kamehouse-groot-login.sh");
    $shellCommandOutput = explode("\n", $shellCommandOutput);

    foreach ($shellCommandOutput as $shellCommandOutputLine) {
      if ($kameHouse->util->string->startsWith($shellCommandOutputLine, 'loginStatus=SUCCESS')) {
        $isAuthorizedUser = true;
      }
    }
    
    return $isAuthorizedUser;
  }

  /**
   * Check if the user is logged in.
   */
  private function isLoggedIn() {
    if (isset($_SESSION['logged-in']) && isset($_SESSION['username'])) {
      return true;
    } else {
      return false;
    }
  }  

  /**
   * Get database configuration.
   */
  private function getDatabaseConfig() {
    return '{ 
      "server" : "localhost",
      "username" : "kameHouseUser",
      "password" : "kameHousePwd",
      "database" : "kameHouse"
    }';
  }

} // KameHouseAuth
?> 
