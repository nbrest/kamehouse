<?php
/**
 * Endpoint: /kame-house-groot/api/v1/commons/examples/examples.php
 * 
 * [INTERNAL] - To be imported from other php files. Not to be directly called from frontend code.
 * 
 * @author nbrest
 */

/** Example isLinuxHost usage */
function isLinuxHostExample() {
  logToErrorFile("isLinuxHostExample");
  echo "isLinuxHostExample():<br>";
  if (isLinuxHost()) {
    echo "its a linux host. do linux host specific stuff";
  } else {
    echo "its NOT linux host. do windows host specific stuff";
  } 
}
?>
