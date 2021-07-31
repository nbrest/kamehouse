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
    session_start();
    session_destroy();
    header('Location: /kame-house-groot/login.html?logout');
  }
?>