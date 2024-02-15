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
    $documentRoot = realpath($_SERVER["DOCUMENT_ROOT"]);
    require_once("$documentRoot/kame-house-groot/api/v1/kamehouse/commons/kamehouse.php");
    require_once("$documentRoot/kame-house-groot/api/v1/kamehouse/test/test.php");
    require_once("$documentRoot/kame-house-groot/api/v1/kamehouse/commons/examples/examples.php");
    $kameHouseTest->runAll();
    $kameHouseExamples->runAll();
  }

}
?>
