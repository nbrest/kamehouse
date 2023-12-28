/**
 * KameHouse error page.
 * 
 * @author nbrest
 */
class KameHouseErrorPage { 

  /**
   * Load the error page header and footer.
   */
  load() {
    this.#logInfo("Loading kamehouse error page");
    $('head').append('<link rel="stylesheet" type="text/css" href="/kame-house/error/css/error-header.css">');
    $('head').append('<link rel="stylesheet" type="text/css" href="/kame-house/error/css/error-footer.css">');
    $("#kamehouse-error-header").load("/kame-house/error/html/error-header.html", () => {
      this.#logInfo("Loaded kamehouse error header");
    });
    $("#kamehouse-error-footer").load("/kame-house/error/html/error-footer.html", () => {
      this.#logInfo("Loaded kamehouse error footer");
    });
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
}

const kameHouseErrorPage = new KameHouseErrorPage();
$(document).ready(() => {
  kameHouseErrorPage.load();
});
  