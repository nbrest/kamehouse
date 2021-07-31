<?php
/**
 * Logout the current user.
 * 
 * @author nbrest
 */
  main();
?> 

<?php

  function main() {
    session_start();
    session_destroy();
    header('Location: /kame-house-groot/login.html?logout');
  }
?>