<?php 
/**
 * Endpoint: /kame-house-groot/api/v1/commons/session/status.php
 * 
 * @author nbrest
 */
  main();
?>

<?php
  function main() {
    init();
    
    $user = isset($_SESSION['username']) ? $_SESSION['username'] : 'anonymousUser';
  
    $sessionStatus = [ 'server' => gethostname(), 'username' => $user , 'isLinuxHost' => isLinuxHost() ];
  
    setJsonResponseBody($sessionStatus);
  }

  function init() {
    session_start();
    require_once("../../../../api/v1/commons/global.php");
  }
?>