<?php
/**
 * Endpoint: /kame-house-groot/api/v1/auth/logout.php (GET)
 * 
 * Logout the current user.
 * 
 * @author nbrest
 */
  main();
?> 

<?php

  /**
   * Destroy the current session and redirect to login page.
   */
  function main() {
    ini_set('session.gc_maxlifetime', 0);
    session_set_cookie_params(0);
    session_start();
    session_destroy();
    header('Location: /kame-house-groot/login.html?logout=true');
  }
?>