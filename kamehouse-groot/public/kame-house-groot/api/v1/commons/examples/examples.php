<?php
/**
 * Endpoint: /kame-house-groot/api/v1/commons/examples/examples.php
 * 
 * [INTERNAL] - To be imported from other php files. Not to be directly called from frontend code.
 * 
 * @author nbrest
 */
$kameHouseExample = new KameHouseExample();
$kameHouseExample->runAll();

class KameHouseExample {

  /**
   * Run all examples.
   */
  public function runAll() {
    $this->isLinuxHost();
  }

  /** Example isLinuxHost usage */
  public function isLinuxHost() {
    global $kameHouse;
    $kameHouse->logger->logToErrorFile("isLinuxHostExample");
    echo "isLinuxHostExample():<br>";
    if ($kameHouse->core->isLinuxHost()) {
      echo "its a linux host. do linux host specific stuff";
    } else {
      echo "its NOT linux host. do windows host specific stuff";
    } 
  }
}
?>
