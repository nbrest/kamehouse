<?php
/**
 * Endpoint: /kame-house-groot/api/v1/auth/authorize-admin-api.php
 * 
 * [INTERNAL] - To be imported from other php files. Not to be directly called from frontend code.
 * 
 * Check if an Authorization header was sent or if there is an active session.
 * There's no roles in GRoot. Only admin users. So if the user is logged in, it has access to any page.
 * 
 * Use this endpoint in other GRoot API endpoints that require an authenticated user by calling:
 * `require_once("../../../../api/v1/auth/authorize-admin-api.php");`
 * At the beginning of the init() method of the endpoint that needs securing.
 * 
 * @author nbrest
 */
$kameHouseApiAuthorizator = new KameHouseApiAuthorizator();
$kameHouseApiAuthorizator->authorize();

class KameHouseApiAuthorizator {

  /**
   * Authorize api.
   */
  public function authorize() {
    global $kameHouse;
    $this->init();

    if ($kameHouse->auth->isAdminUser()) {
      return;
    }

    if ($kameHouse->auth->isAuthorizationHeaderSet()) {
      $username = $kameHouse->auth->getUsernameFromAuthorizationHeader();
      $password = $kameHouse->auth->getPasswordFromAuthorizationHeader();

      if ($kameHouse->auth->isAuthorizedUser($username, $password) && $kameHouse->auth->hasAdminRole($username)) {
        return;
      } else {
        $kameHouse->logger->info("Invalid username and password");
        $kameHouse->core->exitWithError(401, "Invalid username and password");
      }
    }

    $kameHouse->core->exitWithError(401, "Login as admin to /kame-house-groot to access this endpoint");
  }

  /**
   * Init authorize api.
   */
  private function init() {
    require_once("../../../../api/v1/commons/kamehouse.php");
    require_once("kamehouse-auth.php");
  }

} // KameHouseApiAuthorizator 
?>