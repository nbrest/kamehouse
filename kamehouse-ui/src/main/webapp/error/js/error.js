/**
 * KameHouse error page.
 * 
 * @author nbrest
 */
class KameHouseErrorPage { 

  #jq = null;
  #skipKameHouseJs = false;

  /**
   * Build kamehouse error page.
   */
  constructor() {
    this.#setJquery();
  }

  /**
   * Execute the ready function after the document is ready.
   */
  ready(readyFunction) {
    return this.#jq(document).ready(() => {readyFunction()});
  }

  /**
   * Load the error page header and footer.
   */
  load() {
    this.#logInfo("Loading kamehouse error page");
    this.#loadHeader();
    this.#loadFooter();
    this.#loadKameHouseJs();
  }

  /** 
   * Toggle between adding and removing the "responsive" class to topnav when the user clicks on the icon. 
   */
  toggleHeaderNav() {
    const headerMenu = document.getElementById("header-menu");
    if (headerMenu.className === "header-nav") {
      this.#classListAdd(headerMenu, "responsive")
    } else {
      this.#classListRemove(headerMenu, "responsive");
    }
  }  

  /**
   * Set jQuery.
   */
  #setJquery() {
    try {
      this.#jq = $;
    } catch (error) {
      console.log("Error setting jquery on kamehouse error page");
      this.#jq = {};
    }
  }

  /**
   * Log an info message.
   */
  #logInfo(message) {
    console.log(this.#getTimestamp() + " - [INFO] - " + message);
  }

  /**
   * Get the current timestamp.
   */
  #getTimestamp() {
    const  date = new Date();
    const offsetTime = date.getTimezoneOffset() * -1 * 60 * 1000;
    const currentDateTime = date.getTime();
    return new Date(currentDateTime + offsetTime).toISOString().replace("T", " ").slice(0, 19);
  }

  /**
   * Load js script.
   */
  #getScript(scriptPath, successCallback, errorCallback) { 
    this.#jq.getScript(scriptPath)
    .done((script, textStatus) => {
      this.#logInfo("Loaded successfully script: " + scriptPath);
      successCallback();
    })
    .fail((jqxhr, settings, exception) => {
      this.#logInfo("Error loading script: " + scriptPath);
      errorCallback();
    });
  }

  /**
   * Load error header.
   */
  #loadHeader() {
    this.#append(this.#getHead(), '<link rel="stylesheet" type="text/css" href="/kame-house/error/css/error-header.css">');
    this.#loadHtmlSnippetById("kamehouse-error-header", "/kame-house/error/html/error-header.html", () => {
      this.#logInfo("Loaded kamehouse error header");
      if (this.#skipKameHouseJs) {
        this.#classListRemoveById('error-header-login-status-btn', 'rotate-4');
        this.#removeById("error-header-login-status-text");
      }
    });
  }

  /**
   * Load error footer.
   */
  #loadFooter() {
    this.#append(this.#getHead(), '<link rel="stylesheet" type="text/css" href="/kame-house/error/css/error-footer.css">');
    this.#loadHtmlSnippetById("kamehouse-error-footer", "/kame-house/error/html/error-footer.html", () => {
      this.#logInfo("Loaded kamehouse error footer");
    });
  }

  /**
   * Load kamehouse.js if available. For 502 and 503 error pages it probably won't be available. For other errors it should.
   */
  #loadKameHouseJs() {
    const urlParams = new URLSearchParams(window.location.search);
    const skipKameHouseJs = urlParams.get('skipKameHouseJs');
    if (skipKameHouseJs == "true") {
      this.#logInfo("Url parameter set to skip kamehouse.js");
      this.#skipKameHouseJs = true;
      return;
    }
    this.#getScript("/kame-house/kamehouse/js/kamehouse.js", 
    () => {
      this.#logInfo("Loaded kamehouse.js. Overriding header and footer");
      this.#append(this.#getHead(), '<link rel="stylesheet" type="text/css" href="/kame-house/kamehouse/css/kamehouse.css">');
      this.#removeById("kamehouse-error-header");
      this.#removeById("kamehouse-error-footer");
    },
    () => {
      this.#logInfo("Error loading kamehouse.js. Keeping error page header and footer");
      this.#classListRemoveById('error-header-login-status-btn', 'rotate-4');
      this.#removeById("error-header-login-status-text");
    });
  }

  /**
   * get head element.
   */
  #getHead() {
    return document.getElementsByTagName("head")[0];
  }

  /**
   * Load html snippet into element.
   */
  #loadHtmlSnippetById(elementId, htmlSnippetPath, callback) {
    const element = document.getElementById(elementId);
    if (element) {
      this.#jq(element).load(htmlSnippetPath, callback);
    }
  }   

  /** Add a class to the element */
  #classListAdd(element, className) {
    if (element) {
      element.classList.add(className);
    }
  }

  /** Remove a class from the element */
  #classListRemove(element, className) {
    if (element) {
      element.classList.remove(className);
    }
  }  

  /** Remove a class from the element */
  #classListRemoveById(elementId, className) {
    const element = document.getElementById(elementId);
    this.#classListRemove(element, className);
  }    

  /**
   * Append the apendElement to appendToElement.
   */
  #append(appendToElement, apendElement) {
    if (appendToElement) {
      this.#jq(appendToElement).append(apendElement);
    }
  }
  
  /**
   * Remove element from dom.
   */
  #removeById(elementId) {
    const element = document.getElementById(elementId);
    if (element) {
      element.remove();
    }
  }
}

const kameHouseErrorPage = new KameHouseErrorPage();
kameHouseErrorPage.ready(() => {kameHouseErrorPage.load();});
