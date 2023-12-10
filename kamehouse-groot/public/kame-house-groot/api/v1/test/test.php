<?php 
/**
 * Endpoint: /kame-house-groot/api/v1/test/test.php (GET)
 * 
 * Test php functionality
 * 
 * @author nbrest
 */
$kameHouseTest = new KameHouseTest();
$kameHouseTest->runAll();

class KameHouseTest {

  /**
   * Run all test functions.
   */
  public function runAll() {
    global $kameHouse;
    $this->init();
  
    $kameHouse->logger->info("Accessing test page");
    // print server info. ***** DON'T LEAVE THIS UNCOMMENTED *****
    // Gives a lot of info of the server
    echo "<h1>print server info</h1>";
    //print_r($_SERVER);
    //print_r ($_SESSION);

    // print all headers
    echo "<h1>print all headers</h1>";
    foreach (getallheaders() as $name => $value) {
      echo "$name: $value<br>";
    }
  
    // Test function defined in the same test.php file
    $this->testFunction();
  
    $this->runExamples();
    
    // print phpinfo. ***** DON'T LEAVE THIS UNCOMMENTED *****
    //phpinfo();
  }

  /**
   * Init test.
   */
  private function init() {
    ini_set('session.gc_maxlifetime', 0);
    session_set_cookie_params(0);
    session_start();
    require_once("../../../api/v1/commons/kamehouse.php");
  }
  
  /** 
   * I can define functions here as well and call them in the API endpoint 
   */
  private function testFunction() {
    echo "<br><br>testFunction: mada mada dane:<br><br>";
  }

  /**
   * Run kamehouse examples.
   */
  private function runExamples() {
    require_once("../../../api/v1/commons/examples/examples.php");
  }

} // KameHouseTest
?>
