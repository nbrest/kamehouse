<?php
/**
 * Endpoint: /kame-house-groot/api/v1/auth/authorize-page.php
 * 
 * [EXTERNAL] - To be loaded by groot pages that require an admin user, like server management page.
 * 
 * Check if the user is logged in and an admin.
 * 
 * Import this in every page that requires groot authorization by calling:
 * `<?php require_once("../../api/v1/auth/authorize-page.php") ?>`
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
    // I think this only works on pages that are under /admin/aaa/1.php. Pages in /admin/1.php would fail to load kamehouse.php. But currently all admin pages are a level under /admin/
    require_once("../../api/v1/commons/kamehouse.php");
    require_once("../../api/v1/auth/kamehouse-auth.php");
    $kameHouse->auth->authorizePage();
  }

} // KameHousePageAuthorizator
?>