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
    $uiVersion = $this->getKameHouseUiVersion();
    $dockerContainerEnv = $kameHouse->util->docker->getDockerContainerEnv();
    $isLinuxDockerHost = $kameHouse->util->docker->getDockerContainerEnvBooleanProperty($dockerContainerEnv, "IS_LINUX_DOCKER_HOST");
    $isDockerContainer = $kameHouse->util->docker->getDockerContainerEnvBooleanProperty($dockerContainerEnv, "IS_DOCKER_CONTAINER");
    $dockerControlHost = $kameHouse->util->docker->getDockerContainerEnvBooleanProperty($dockerContainerEnv, "DOCKER_CONTROL_HOST");

    $sessionStatus = [ 
      'server' => gethostname(),
      'username' => $user,
      'buildVersion' => $uiVersion['buildVersion'],
      'buildDate' => $uiVersion['buildDate'],
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

  /**
   * Get kamehouse ui version.
   */
  private function getKameHouseUiVersion() {
    global $kameHouse;
    $uiVersion = [
      'buildVersion' => '99.99.9-r2d2c3po',
      'buildDate' => '9999-99-99 99:99:99'
    ];
    $buildVersionArray = explode("\n", file_get_contents(realpath($_SERVER["DOCUMENT_ROOT"]) . '/kame-house/ui-build-version.txt', true));
    foreach($buildVersionArray as $buildVersionEntry) {
      if($kameHouse->util->string->startsWith($buildVersionEntry, "buildVersion=")) {
        $uiVersion['buildVersion'] = explode("=", $buildVersionEntry)[1];
      }
    }
    $buildDateArray = explode("\n", file_get_contents(realpath($_SERVER["DOCUMENT_ROOT"]) . '/kame-house/ui-build-date.txt', true));
    foreach($buildDateArray as $buildDateEntry) {
      if($kameHouse->util->string->startsWith($buildDateEntry, "buildDate=")) {
        $uiVersion['buildDate'] = explode("=", $buildDateEntry)[1];
      }
    }
    return $uiVersion;
  }
  
} // KameHouseSession
?>