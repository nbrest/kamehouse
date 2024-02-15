<?php
/**
 * Example functions.
 * 
 * @author nbrest
 */
$kameHouseExamples = new KameHouseExamples();

class KameHouseExamples {

  /**
   * Run all examples.
   */
  public function runAll() {
    $this->isLinuxHost();
  }

  /** Example isLinuxHost usage */
  public function isLinuxHost() {
    global $kameHouse;
    $kameHouse->logger->info("isLinuxHostExample");
    echo "isLinuxHostExample():<br>";
    if ($kameHouse->core->isLinuxHost()) {
      echo "its a linux host. do linux host specific stuff";
    } else {
      echo "its NOT linux host. do windows host specific stuff";
    } 
  }

} // KameHouseExamples
?>
