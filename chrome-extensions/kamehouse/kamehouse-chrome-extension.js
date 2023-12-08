/**
 * KameHouse Chrome Extension loader.
 * 
 * @author nbrest
 */
try {
  class KameHouseChromeExtensionLoader {

    load() {
      console.log(this.#getTimestamp() + " KameHouse Chrome Extension background process");
      this.#addListeners();
    }
  
    /**
     * Add listeners that trigger the code injection.
     */
    #addListeners() {
      console.log(this.#getTimestamp() + " Setting KameHouse Chrome Extension listeners");
      // Execute KameHouse Chrome Extension when I click the extension button
      chrome.action.onClicked.addListener((tab) => {
        console.log(this.#getTimestamp() + " Executing kamehouse on action.onClicked");
        chrome.scripting.executeScript({
          target: {tabId: tab.id},
          files: ['kamehouse-inject.js']	
        });
        chrome.scripting.insertCSS({
          target: { tabId: tab.id },
          files: ["kamehouse-inject.css"]
        });
      });
      
      // Execute KameHouse Chrome Extension when I open a tab/page
      chrome.tabs.onCreated.addListener((tab) => {
        console.log(this.#getTimestamp() + " Executing kamehouse on tabs.onCreated");
        chrome.scripting.executeScript({
          target: {tabId: tab.id},
          files: ['kamehouse-inject.js']	
        });
        chrome.scripting.insertCSS({
          target: { tabId: tab.id },
          files: ["kamehouse-inject.css"]
        });
      });
      
      // Execute KameHouse Chrome Extension when I refresh a tab/page
      chrome.tabs.onUpdated.addListener((tabId, changeInfo, tab) => {
        if (changeInfo.status === "complete") {
          console.log(this.#getTimestamp() + " Executing kamehouse on tab.onUpdated");
          chrome.scripting.executeScript({
            target: {tabId: tabId},
            files: ['kamehouse-inject.js']	
          });
          chrome.scripting.insertCSS({
            target: { tabId: tabId },
            files: ["kamehouse-inject.css"]
          });
        }
      });
    }
  
    /**
     * Get current timestamp.
     */
    #getTimestamp() {
      const date = new Date();
      const offsetTime = date.getTimezoneOffset() * -1 * 60 * 1000;
      const currentDateTime = date.getTime();
      return new Date(currentDateTime + offsetTime).toISOString().replace("T"," ").slice(0,19);
    }  
  
  } // KameHouseChromeExtensionLoader
  
  new KameHouseChromeExtensionLoader().load();  
} catch(error) {
  console.log(error);
}
