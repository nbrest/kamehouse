<?php 
/**
 * Endpoint: /kame-house-groot/api/v1/commons/session/status.php (GET)
 * 
 * Get the current session status.
 * 
 * @author nbrest
 */
  main();
?>

<?php
  function main() {
    init();

    $dockerContainerEnv = getDockerContainerEnv();
    $isLinuxDockerHost = getDockerContainerEnvBooleanProperty($dockerContainerEnv, "IS_LINUX_DOCKER_HOST");
    $isDockerContainer = getDockerContainerEnvBooleanProperty($dockerContainerEnv, "IS_DOCKER_CONTAINER");
    $dockerControlHost = getDockerContainerEnvBooleanProperty($dockerContainerEnv, "DOCKER_CONTROL_HOST");

    $user = isset($_SESSION['username']) ? $_SESSION['username'] : 'anonymousUser';
    $sessionStatus = [ 'server' => gethostname(),
     'username' => $user,
     'isLinuxHost' => isLinuxHost(),
     'isLinuxDockerHost' => $isLinuxDockerHost,
     'isDockerContainer' => $isDockerContainer,
     'dockerControlHost' => $dockerControlHost,
    ];
  
    setJsonResponseBody($sessionStatus);
  }

  function init() {
    ini_set('session.gc_maxlifetime', 0);
    session_set_cookie_params(0);
    session_start();
    require_once("../../../../api/v1/commons/global.php");
  }
?>