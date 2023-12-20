<?php
/**
 * [EXTERNAL] Endpoint: /kame-house-groot/api/v1/auth/authorize-page.php
 * 
 * To be loaded by groot pages that require an admin user, like server management page.
 * 
 * Check if the user is logged in and an admin.
 * 
 * Import this in every page that requires groot authorization by calling:
 * `<?php realpath($_SERVER["DOCUMENT_ROOT"]) . "/kame-house-groot/api/v1/auth/authorize-page.php") ?>`
 * At the beginning of that page. The rest of the page should be static html code
 * 
 * @author nbrest
 */
$kameHousePageAuthorizator = new KameHousePageAuthorizator();
$kameHousePageAuthorizator->authorize();

class KameHousePageAuthorizator {

  /**
   * Check if there's an active session, otherwise redirect to login page.
   */
  public function authorize() {
    global $kameHouse;
    $documentRoot = realpath($_SERVER["DOCUMENT_ROOT"]);
    require_once("$documentRoot/kame-house-groot/api/v1/commons/kamehouse.php");
    require_once("$documentRoot/kame-house-groot/api/v1/admin/kamehouse-shell/kamehouse-shell.php");
    require_once("$documentRoot/kame-house-groot/api/v1/auth/kamehouse-auth.php");
    $kameHouse->auth->authorizePage();
  }

} // KameHousePageAuthorizator
?>