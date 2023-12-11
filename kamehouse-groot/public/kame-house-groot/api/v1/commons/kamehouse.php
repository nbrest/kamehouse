<?php
/**
 * [INTERNAL] Endpoint: /kame-house-groot/api/v1/commons/kamehouse.php
 * 
 * KameHouse php framework root object.
 * 
 * To be imported from other php files. Not to be directly called from frontend code.
 * 
 * @author nbrest
 */
global $kameHouse;
$kameHouse = new KameHouse();

class KameHouse {

  public $auth;
  public $core;
  public $logger;
  public $session;
  public $shell;
  public $util;

  function __construct() {
    $this->core = new KameHouseCore();
    $this->logger = new KameHouseLogger();
    $this->util = new KameHouseUtils();
  }

  /**
   * Set kamehouse auth.
   */
  public function setAuth($auth) {
    $this->auth = $auth;
  }

  /**
   * Set kamehouse session.
   */
  public function setSession($session) {
    $this->session = $session;
  }

  /**
   * Set kamehouse shell.
   */
  public function setShell($shell) {
    $this->shell = $shell;
  }

} // KameHouse

/**
 * Core kamehouse functions.
 * 
 * @author nbrest
 */
class KameHouseCore {

  /** Returns true if the host os running php is a linux server */
  public function isLinuxHost() {
    $osType = php_uname();
    $linuxStrPos = stripos($osType, 'Linux');
    if ($linuxStrPos === false) {
      return false;
    } else {
      return true;
    }
  }

  /**
   * Return the specified error message and status code and stop the execution.
   */
  public function exitWithError($statusCode, $errorMessage) {

    $responseBody = [ 
      'code' => $statusCode,
      'message' => $errorMessage 
    ];

    http_response_code($statusCode);

    header('Content-Type: application/json');
    echo json_encode($responseBody);
    exit();
  }

  /**
   * Write the response body as a json.
   */
  public function setJsonResponseBody($responseBody) {
    header('Content-Type: application/json');
    echo json_encode($responseBody);
  }

} // KameHouseCore

/**
 * Wrapper for utility classes.
 * 
 * @author nbrest
 */
class KameHouseUtils {

  public $docker;
  public $string;

  function __construct() {
    $this->docker = new DockerUtils();
    $this->string = new StringUtils();
  }

} // KameHouseUtils

/**
 * Docker utils.
 * 
 * @author nbrest
 */
class DockerUtils {

  /**
   * Get an array with the docker container environment properties if running inside docker, or an empty array otherwise.
   */
  public function getDockerContainerEnv() {
    global $kameHouse;
    $dockerContainerEnv = null;
    if ($kameHouse->core->isLinuxHost()) {
      $username = trim(shell_exec("HOME=/var/www /var/www/programs/kamehouse-shell/bin/kamehouse/get-username.sh"));
      $dockerContainerEnvFile = "/home/" . $username . "/.kamehouse/.kamehouse-docker-container-env";
      $script = "if [ -f \"" . $dockerContainerEnvFile . "\" ]; then cat " . $dockerContainerEnvFile . "; fi";
      $dockerContainerEnv = trim(shell_exec($script));
      $dockerContainerEnv = explode("\n", $dockerContainerEnv);
      if(!$kameHouse->util->string->startsWith($dockerContainerEnv[0], "#")) {
        array_splice($dockerContainerEnv, 0, 1);
      }
    }
    if ($dockerContainerEnv === null) {
      $dockerContainerEnv = [];
    }  
    return $dockerContainerEnv;
  }

  /**
   * Get the boolean value of a property from the docker container environment.
   */
  public function getDockerContainerEnvBooleanProperty($dockerContainerEnv, $propertyName) {
    global $kameHouse;
    return $kameHouse->util->string->getBoolean($this->getDockerContainerEnvProperty($dockerContainerEnv, $propertyName));
  }

  /**
   * Get a property from the docker container environment.
   */
  private function getDockerContainerEnvProperty($dockerContainerEnv, $propertyName) {
    foreach ($dockerContainerEnv as $property) {
      $property = explode("=", $property);
      if ($property[0] === $propertyName) {
        return $property[1];
      }
    }
    return "";
  }

} // DockerUtils

/**
 * String manipulation utils.
 * 
 * @author nbrest.
 */
class StringUtils {

  /** 
   * Replaces bash colors in the input string for the equivalent css styled color.
   * When updating color mappings here, also update them on kamehouse.js
   */
  public function convertBashColorsToHtml($bashOutput) {
    $colorMappings = array(
      '[0;30m' => '<span style="color:black">',  
      '[1;30m' => '<span style="color:black">',
      '[0;31m' => '<span style="color:red">', 
      '[1;31m' => '<span style="color:red">', 
      '[0;32m' => '<span style="color:green">', 
      '[00;32m' => '<span style="color:green">',   
      '[1;32m' => '<span style="color:green">',
      '[0;1;32m' => '', // remove these in-the-middle-of green span symbols on build-kamehouse
      '[0;33m' => '<span style="color:yellow">',
      '[0;1;33m' => '', // remove these in-the-middle-of yellow span symbols on build-kamehouse
      '[1;33m' => '<span style="color:yellow">',
      '[0;34m' => '<span style="color:#3996ff">',
      '[1;34m' => '<span style="color:#3996ff">',
      '[0;35m' => '<span style="color:purple">',
      '[1;35m' => '<span style="color:purple">',
      '[0;36m' => '<span style="color:cyan">',
      '[1;36m' => '<span style="color:cyan">',
      '[36m' => '<span style="color:cyan">',
      '[0;37m' => '<span style="color:white">',
      '[1;37m' => '<span style="color:white">',
      '[0;39m' => '<span style="color:gray">',
      '[1;39m' => '<span style="color:gray">',
      '[1;32;49m' => '<span style="color:lightgreen">',  
      '[0m' => '</span>',
      '[00m' => '</span>',
      '[1m' => '</span>',
      '[0;1m' => '</span>',
      '[m'   => '</span>'
    );
    $htmlOutput = str_replace(array_keys($colorMappings), $colorMappings, $bashOutput);
    // Remove the special character added in my bash color mappings
    $htmlOutput = str_replace("", "", $htmlOutput);
    $htmlOutput = str_replace("</span>ain]", "[main]", $htmlOutput);

    return $htmlOutput;
  }

  /**
   * Check for empty string.
   */
  public function isEmptyStr($val) {
    if (!isset($val)) {
      return true;
    }
    if ($val == '') {
      return true;
    }
    return false;
  }

  /**
   * Check if a string ends with the specified substring.
   */
  public function endsWith($str, $ending) {
    $length = strlen($ending);
    return $length > 0 ? substr($str, -$length) === $ending : true;
  }

  /**
   * Check if a string starts with the specified substring.
   */
  public function startsWith($str, $start) {
    $length = strlen($start);
    return substr($str, 0, $length) === $start;
  }

  /**
   * Checks if a string contains the specified substring.
   */
  public function contains($str, $substr) {
    return strpos($str, $substr) !== false;
  }

  /** 
   * Check that the input is valid to pass as an argument to a shell script.
   */
  public function isValidInputForShell($param) {
    $MAX_LENGTH = 100;

    if ($this->isEmptyStr($param)) {
      return true;
    }

    $isValidInputForShell = true;

    if (strlen($param) > $MAX_LENGTH) {
      $isValidInputForShell = false;
    }

    // Skipped: " "
    $forbiddenChars = array(">", "<", ";", ":", "|", "&", "*", "(", ")", "{", "}", "[", "]", "^", "\"", "'", "#", "\\", ",", "`", "..", "%", "@", "!", "$", "?");
    foreach ($forbiddenChars as $forbiddenChar) {
      if($this->hasForbiddenCharSequenceForShell($param, $forbiddenChar)) {
        $isValidInputForShell = false;
      }
    }

    return $isValidInputForShell;
  }

  /**
   * Returns true if the specified string is either 'true or 'TRUE';
   */
  public function getBoolean($string) {
    return ($string === 'true' || $string === 'TRUE');
  }
    
  /**
   * Validate if the specified param contains the specified invalid character sequence.
   */
  private function hasForbiddenCharSequenceForShell($param, $invalidCharSequence) {
    global $kameHouse;
    if ($this->contains($param, $invalidCharSequence)) {
      $kameHouse->logger->info("Input contains forbidden characters");
      return true;
    } else {
      return false;
    }
  }

} // StringUtils

/**
 * Logging functionality.
 * 
 * @author nbrest
 */
class KameHouseLogger {

  /**
   * Log info message to the apache error log file.
   */
  public function info($message) {
    error_log($message, 0);
  }

} // KameHouseLogger
?>

