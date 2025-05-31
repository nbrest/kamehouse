<?php
/**
 * Kamehouse groot session.
 * 
 * @author nbrest
 */
class KameHouseSession {

  /**
   * Get session status.
   */
  public function getStatus() {
    global $kameHouse;
    $this->initSession();
    $this->configureSessionState();

    $user = isset($_SESSION['username']) ? $_SESSION['username'] : 'anonymousUser';
    $roles = $kameHouse->auth->getRoles($user);
    $dockerContainerEnv = $kameHouse->util->docker->getDockerContainerEnv();
    $isLinuxDockerHost = $kameHouse->util->docker->getDockerContainerEnvBooleanProperty($dockerContainerEnv, "IS_LINUX_DOCKER_HOST");
    $isDockerContainer = $kameHouse->util->docker->getDockerContainerEnvBooleanProperty($dockerContainerEnv, "IS_DOCKER_CONTAINER");
    $dockerControlHost = $kameHouse->util->docker->getDockerContainerEnvBooleanProperty($dockerContainerEnv, "DOCKER_CONTROL_HOST");

    $sessionStatus = [ 
      'server' => gethostname(),
      'username' => $user,
      'isLinuxHost' => $kameHouse->core->isLinuxHost(),
      'isLinuxDockerHost' => $isLinuxDockerHost,
      'isDockerContainer' => $isDockerContainer,
      'dockerControlHost' => $dockerControlHost,
      'roles' => $roles
    ];
  
    $kameHouse->core->setJsonResponseBody($sessionStatus);
  }

  /**
   * Init session.
   */
  private function initSession() {
    ini_set('session.gc_maxlifetime', 0);
    session_set_cookie_params(0);
    session_start();
  } 

  /**
   * Configure session state.
   */
  private function configureSessionState() {
    global $kameHouse;
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
  }
  
} // KameHouseSession
?>