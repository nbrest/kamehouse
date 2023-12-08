<?php
/**
 * Endpoint: /kame-house-groot/api/v1/auth/authorize-admin-page.php
 * 
 * [INTERNAL] - To be imported from other php files. Not to be directly called from frontend code.
 * 
 * Check if the user is logged in and an admin.
 * 
 * Import this in every page that requires groot authorization by calling:
 * `<?php require_once("../../api/v1/auth/authorize-admin-page.php") ?>`
 * At the beginning of that page. The rest of the page should be static html code
 * 
 * @author nbrest
 */
  authorizePage();
?> 

<?php

  /**
   * Check if there's an active session, otherwise redirect to login page.
   */
  function authorizePage() {
    initAuthorizePage();

    if (isAdminUser()) {
      unlockSession();
      return;
    }
    unlockSession();

    if (isset($_SERVER['REQUEST_URI'])) {
      header('Location: /kame-house-groot/login.html?unauthorizedPageAccess=true&referrer=' . $_SERVER['REQUEST_URI']);
      exit;
    }

    header('Location: /kame-house-groot/login.html?unauthorizedPageAccess=true');
  	exit;
  }

  /**
   * Init authorize page.
   */
  function initAuthorizePage() {
    ini_set('session.gc_maxlifetime', 0);
    session_set_cookie_params(0);
    session_start();
    require_once("../../api/v1/commons/kamehouse.php");
    require_once("auth-functions.php");
  }
?>