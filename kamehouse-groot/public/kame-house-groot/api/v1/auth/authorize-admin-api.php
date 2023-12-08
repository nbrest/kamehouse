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
  authorizeApi();
?> 

<?php

  /**
   * Authorize api.
   */
  function authorizeApi() {
    initAuthorizeApi();

    if (isAdminUser()) {
      return;
    }

    if (isAuthorizationHeaderSet()) {
      $username = getUsernameFromAuthorizationHeader();
      $password = getPasswordFromAuthorizationHeader();

      if (isAuthorizedUser($username, $password) && hasAdminRole($username)) {
        return;
      } else {
        logToErrorFile("Invalid username and password");
        exitWithError(401, "Invalid username and password");
      }
    }

    exitWithError(401, "Login as admin to /kame-house-groot to access this endpoint");
  }

  /**
   * Init authorize api.
   */
  function initAuthorizeApi() {
    // global.php already imported by the callers of authorize-admin-api.php
    // require_once("../../../api/v1/commons/kamehouse.php");
    require_once("auth-functions.php");
  }
?>