<?php
/**
 * Endpoint: /kame-house-groot/api/v1/commons/session/kamehouse-session.php (GET)
 * 
 * [INTERNAL] - To be imported from other php files. Not to be directly called from frontend code.
 * 
 * Class definition of kamehouse session.
 * 
 * @author nbrest
 */
class KameHouseSession {

  /**
   * Get session status.
   */
  public function getStatus() {
    global $kameHouse;
    $this->init();

    $dockerContainerEnv = $kameHouse->util->docker->getDockerContainerEnv();
    $isLinuxDockerHost = $kameHouse->util->docker->getDockerContainerEnvBooleanProperty($dockerContainerEnv, "IS_LINUX_DOCKER_HOST");
    $isDockerContainer = $kameHouse->util->docker->getDockerContainerEnvBooleanProperty($dockerContainerEnv, "IS_DOCKER_CONTAINER");
    $dockerControlHost = $kameHouse->util->docker->getDockerContainerEnvBooleanProperty($dockerContainerEnv, "DOCKER_CONTROL_HOST");

    if (isset($_SERVER['PHP_AUTH_USER'], $_SERVER['PHP_AUTH_PW'])) {
      $username = $_SERVER['PHP_AUTH_USER'];
      $password = $_SERVER['PHP_AUTH_PW'];
      if ($kameHouse->auth->isAuthorizedUser($username, $password)) {
        $kameHouse->auth->initiateSession($username);
      } else {
        $kameHouse->auth->endSession($username);
        //$kameHouse->logger->info("Invalid credentials in basic auth header");
      }
    }
    $user = isset($_SESSION['username']) ? $_SESSION['username'] : 'anonymousUser';
    $roles = $kameHouse->auth->getRoles($user);

    $sessionStatus = [ 
      'server' => gethostname(),
      'username' => $user,
      'isLinuxHost' => $kameHouse->core->isLinuxHost(),
      'isLinuxDockerHost' => $isLinuxDockerHost,
      'isDockerContainer' => $isDockerContainer,
      'dockerControlHost' => $dockerControlHost,
      'roles' => $roles,
    ];
  
    $kameHouse->core->setJsonResponseBody($sessionStatus);
  }

  /**
   * Init session status.
   */
  private function init() {
    ini_set('session.gc_maxlifetime', 0);
    session_set_cookie_params(0);
    session_start();
    require_once("../../../../api/v1/commons/kamehouse.php");
    require_once("../../../../api/v1/auth/kamehouse-auth.php");
  }  
} // KameHouseSession
?>