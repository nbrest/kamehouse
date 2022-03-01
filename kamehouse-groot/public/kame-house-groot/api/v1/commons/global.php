<?php
/**
 * Endpoint: /kame-house-groot/api/v1/commons/global.php
 * 
 * [INTERNAL] - To be imported from other php files. Not to be directly called from frontend code.
 * 
 * Other endpoints can import this functionality with:
 *  `require_once("../../../../api/v1/commons/global.php");`
 * 
 * @author nbrest
 */

/** Replaces bash colors in the input string for the equivalent css styled color */
function convertBashColorsToHtml($bashOutput) {
  $colorMappings = array(
    '[0;30m' => '<span style="color:black">',  
    '[1;30m' => '<span style="color:black">',
    '[0;31m' => '<span style="color:red">', 
    '[1;31m' => '<span style="color:red">', 
    '[0;32m' => '<span style="color:green">', 
    '[00;32m' => '<span style="color:green">',   
    '[1;32m' => '<span style="color:green">',   
    '[0;33m' => '<span style="color:yellow">',
    '[1;33m' => '<span style="color:yellow">',
    '[0;34m' => '<span style="color:blue">',
    '[1;34m' => '<span style="color:blue">',
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

  return $htmlOutput;
}

/** Returns true if the host os running php is a linux server */
function isLinuxHost() {
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
function exitWithError($statusCode, $errorMessage) {

  $responseBody = [ 'message' => $errorMessage ];

  http_response_code($statusCode);

  header('Content-Type: application/json');
  echo json_encode($responseBody);
  exit();
}

/**
 * Check for empty string.
 */
function isEmptyStr($val) {
  if (!isset($val)) {
    return true;
  }
  if ($val == '') {
    return true;
  }
  return false;
}

/**
 * Write the response body as a json.
 */
function setJsonResponseBody($responseBody) {
  header('Content-Type: application/json');
  echo json_encode($responseBody);
}

/**
 * Check if a string ends with the specified substring.
 */
function endsWith($str, $ending) {
  $length = strlen($ending);
  return $length > 0 ? substr($str, -$length) === $ending : true;
}

/**
 * Check if a string starts with the specified substring.
 */
function startsWith($str, $start) {
  $length = strlen($start);
  return substr($str, 0, $length) === $start;
}

/**
 * Checks if a string contains the specified substring.
 */
function contains($str, $substr) {
  return strpos($str, $substr) !== false;
}

/** 
 * Check that the input is valid to pass as an argument to a shell script.
 */
function isValidInputForShell($param) {
  $MAX_LENGTH = 100;

  if (isEmptyStr($param)) {
    return true;
  }

  $isValidInputForShell = true;

  if (strlen($param) > $MAX_LENGTH) {
    $isValidInputForShell = false;
  }

  // Skipped: " "
  $forbiddenChars = array(">", "<", ";", ":", "|", "&", "*", "(", ")", "{", "}", "[", "]", "^", "\"", "'", "#", "\\", ",", "`", "..", "%", "@", "!", "$", "?");
  foreach ($forbiddenChars as $forbiddenChar) {
    if(hasForbiddenCharSequenceForShell($param, $forbiddenChar)) {
      $isValidInputForShell = false;
    }
  }

  return $isValidInputForShell;
}

/**
 * Validate if the specified param contains the specified invalid character sequence.
 */
function hasForbiddenCharSequenceForShell($param, $invalidCharSequence) {
  if (contains($param, $invalidCharSequence)) {
    return true;
  } else {
    return false;
  }
}

/**
 * Returns true if the specified string is either 'true or 'TRUE';
 */
function getBoolean($string) {
  return ($string === 'true' || $string === 'TRUE');
}

/**
 * Get an array with the docker container environment properties if running inside docker, or an empty array otherwise.
 */
function getDockerContainerEnv() {
  $dockerContainerEnv = null;
  if (isLinuxHost()) {
    $dockerContainerEnv = trim(shell_exec("cat /home/nbrest/.kamehouse/.kamehouse-docker-container-env"));
    $dockerContainerEnv = explode("\n", $dockerContainerEnv);
    if(!startsWith($dockerContainerEnv[0], "#")) {
      array_splice($dockerContainerEnv, 0, 1);
    }
  }
  if ($dockerContainerEnv === null) {
    $dockerContainerEnv = [];
  }  
  return $dockerContainerEnv;
}

/**
 * Get a property from the docker container environment.
 */
function getDockerContainerEnvProperty($dockerContainerEnv, $propertyName) {
  foreach ($dockerContainerEnv as $property) {
    $property = explode("=", $property);
    if ($property[0] === $propertyName) {
      return $property[1];
    }
  }
  return "";
}

/**
 * Get the boolean value of a property from the docker container environment.
 */
function getDockerContainerEnvBooleanProperty($dockerContainerEnv, $propertyName) {
  return getBoolean(getDockerContainerEnvProperty($dockerContainerEnv, $propertyName));
}
?>
