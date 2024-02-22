/**
 * KameHouse error page.
 * 
 * @author nbrest
 */
class KameHouseErrorPage { 

  jq = null;

  /**
   * Build kamehouse error page.
   */
  constructor() {
    this.#setJquery();
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
      headerMenu.classList.add("responsive");
    } else {
      headerMenu.classList.remove("responsive");
    }
  }  

  /**
   * Set jQuery.
   */
  #setJquery() {
    try {
      this.jq = $;
    } catch (error) {
      console.log("Error setting jquery on kamehouse error page");
      this.jq = {};
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
   * Load error header.
   */
  #loadHeader() {
    this.#append('head', '<link rel="stylesheet" type="text/css" href="/kame-house/error/css/error-header.css">');
    this.#loadHtmlSnippet("#kamehouse-error-header", "/kame-house/error/html/error-header.html", () => {
      this.#logInfo("Loaded kamehouse error header");
    });
  }

  /**
   * Load error footer.
   */
  #loadFooter() {
    this.#append('head', '<link rel="stylesheet" type="text/css" href="/kame-house/error/css/error-footer.css">');
    this.#loadHtmlSnippet("#kamehouse-error-footer", "/kame-house/error/html/error-footer.html", () => {
      this.#logInfo("Loaded kamehouse error footer");
    });
  }

  /**
   * Load kamehouse.js if available. For 502 and 503 error pages it probably won't be available. For other errors it should.
   */
  #loadKameHouseJs() {
    this.#getScript("/kame-house/kamehouse/js/kamehouse.js", 
    () => {
      this.#logInfo("Loaded kamehouse.js. Overriding header and footer");
      this.#append('head', '<link rel="stylesheet" type="text/css" href="/kame-house/kamehouse/css/kamehouse.css">');
      this.#remove("kamehouse-error-header");
      this.#remove("kamehouse-error-footer");
    },
    () => {
      this.#logInfo("Error loading kamehouse.js. Keeping error page header and footer");
      this.#removeClass('#error-header-login-status-btn', 'rotate-4');
      this.#remove("error-header-login-status-text");
    });
  }

  /**
   * Load js script.
   */
  #getScript(scriptPath, successCallback, errorCallback) { 
    this.jq.getScript(scriptPath)
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
   * Remove element from dom.
   */
  #remove(elementId) {
    const element = document.getElementById(elementId);
    if (element) {
      element.remove();
    }
  }

  /**
   * Remove a class from an element.
   */
  #removeClass(element, className) {
    this.jq(element).removeClass(className);
  } 

  /**
   * Append the appendObject to appendTo.
   */
  #append(appendTo, appendObject) {
    this.jq(appendTo).append(appendObject);
  }

  /**
   * Append the appendObject to appendTo.
   */
  #loadHtmlSnippet(loadToId, htmlSnippetPath, callback) {
    this.jq(loadToId).load(htmlSnippetPath, callback);
  }   
}

const kameHouseErrorPage = new KameHouseErrorPage();
kameHouseErrorPage.jq(document).ready(() => {
  kameHouseErrorPage.load();
});
  