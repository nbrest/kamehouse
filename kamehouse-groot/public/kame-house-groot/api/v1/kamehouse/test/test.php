<?php 
/**
 * Test php functionality.
 * 
 * @author nbrest
 */
class KameHouseTest {

  /**
   * Run all test functions.
   */
  public function runAll() {
    global $kameHouse;  
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
    
    // print phpinfo. ***** DON'T LEAVE THIS UNCOMMENTED *****
    //phpinfo();
  }
  
  /** 
   * I can define functions here as well and call them in the API endpoint 
   */
  private function testFunction() {
    echo "<br><br>testFunction: mada mada dane:<br><br>";
  }

} // KameHouseTest
?>
