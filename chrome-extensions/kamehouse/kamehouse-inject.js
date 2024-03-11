/**
 * KameHouse Chrome Extension.
 * 
 * @author nbrest
 */
try {
  class KameHouseChromeExtension {

    /**
     * Load the chrome extension.
     */
    load() {
      console.log(this.getTimestamp() + " Loading KameHouse Chrome Extension");
      this.setGlobalVariables();
      //this.createAutoCloseableModal();
      this.testFunctionLoop();
    }
    
    /**
     * Set global variables.
     */
    setGlobalVariables() {
      if (!window.kameHouseGlobal) {
        console.log(this.getTimestamp() + " Setting KameHouse global variables");
        window.kameHouseGlobal = 1;
      } else {
        console.log(this.getTimestamp() + " KameHouse global variables already set");
      }  
    }
    
    /**
     * Run test function.
     */
    testFunctionLoop() {
      let count = 1;
      while (count <= 3) {
        setTimeout(() => this.testFunction(), 5000 + count * 5000);
        count = count + 1;
      }
    }
    
    /**
     * Test function.
     */
    testFunction() {
      console.log(this.getTimestamp() + " : window.kameHouseGlobal " + window.kameHouseGlobal);
    }
    
    /**
     * Create an auto closeable modal.
     */
    createAutoCloseableModal() {
      console.log(this.getTimestamp() + " Creating KameHouse Chrome extension modal");
      const modal = document.createElement("div");
      modal.setAttribute("id", "kamehouse-chrome-extension-div");
      document.body.appendChild(modal);
      modal.innerHTML = this.getTimestamp() + " - KameHouse Chrome Extension is running<br><br>Auto closing in 5 seconds";
      setTimeout(() => {
        console.log(this.getTimestamp() + " Hiding KameHouse Chrome extension modal")
        modal.style.display = "none";
      }, 5000);
    }
    
    /**
     * Get current timestamp.
     */
    getTimestamp() {
      const date = new Date();
      const offsetTime = date.getTimezoneOffset() * -1 * 60 * 1000;
      const currentDateTime = date.getTime();
      return new Date(currentDateTime + offsetTime).toISOString().replace("T"," ").slice(0,19);
    }
    
    /**
     * Scroll to the top.
     */
    scrollToTop() {
      document.body.scrollTop = document.documentElement.scrollTop = 0;
    }
    
  } // KameHouseChromeExtension

  new KameHouseChromeExtension().load();
} catch(error) {
  console.log(error);
}



