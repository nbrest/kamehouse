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
    
    $user = isset($_SERVER['REMOTE_USER']) ? $_SERVER['REMOTE_USER'] : 'anonymousUser';
  
    $sessionStatus = [ 'server' => gethostname(), 'username' => $user , 'isLinuxHost' => isLinuxHost() ];
  
    setJsonResponseBody($sessionStatus);
  }

  function init() {
    require_once("../../../../api/v1/commons/global.php");
  }
?>