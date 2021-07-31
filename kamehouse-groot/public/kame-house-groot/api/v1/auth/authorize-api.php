<?php
/**
 * 
 * [INTERNAL] - To be imported from other php files. Not to be directly called from frontend code.
 * 
 * Check if an Authorization header was sent or if there is an active session.
 * There's no roles in GRoot. Only admin users. So if the user is logged in, it has access to any page.
 * 
 * Use this endpoint in other GRoot API endpoints that require an authenticated user by calling:
 * `require_once("../../../../api/v1/auth/authorize-api.php");`
 * At the beginning of the init() method of the endpoint that needs securing.
 * 
 * @author nbrest
 */
  authorizeApi();
?> 

<?php

  function authorizeApi() {
    initAuthorizeApi();

    if (isLoggedIn()) {
      return;
    }

    if (isAuthorizationHeaderSet()) {
      $username = getUsernameFromAuthorizationHeader();
      $password = getPasswordFromAuthorizationHeader();

      if (isAuthorizedUser($username, $password)) {
        return;
      } else {
        exitWithError(401, "Invalid username and password");
      }
    }

    exitWithError(401, "Login to /kame-house-groot to access this endpoint");
  }

  function initAuthorizeApi() {
    // global.php already imported by the callers of authorize-api.php
    // require_once("../../../api/v1/commons/global.php");
    require_once("auth-functions.php");
  }
?>