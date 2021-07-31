<?php
/**
 * Check if the user is logged in. 
 * There's no roles in GRoot, so if the user is logged in, it has access to any page.
 * 
 * Import this in every page that requires groot authorization by calling:
 * `<?php require_once("../../api/v1/auth/authorize-page.php") ?>`
 * At the beginning of that page. The rest of the page should be static html code
 * 
 * @author nbrest
 */
mainAuthorizePage();
?> 

<?php

  function mainAuthorizePage() {
    session_start();
    
    // Check that the user is logged in, otherwise redirect to login
    if (!isset($_SESSION['logged-in'])) {
    	header('Location: /kame-house-groot/login.html');
    	exit;
    }
  }
?>