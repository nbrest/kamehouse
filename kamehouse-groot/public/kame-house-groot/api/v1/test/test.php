<?php 
/**
 * Endpoint: /kame-house-groot/api/v1/test/test.php (GET)
 * 
 * Test php functionality.
 * 
 * @author nbrest
 */
$testApi = new TestApi();
$testApi->main();

class TestApi {

  /**
   * Run all test and example functions.
   */
  public function main() {
    require_once(realpath($_SERVER["DOCUMENT_ROOT"]) . "/kame-house-groot/api/v1/kamehouse/commons/kamehouse.php");
    $kameHouseTest = new KameHouseTest();
    $kameHouseTest->runAll();
    $kameHouseExamples = new KameHouseExamples();
    $kameHouseExamples->runAll();
  }

}
?>
