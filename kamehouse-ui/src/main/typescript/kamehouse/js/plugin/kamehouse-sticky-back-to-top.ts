/**
 * Functionality to add a sticky back to top button.
 * Manager to handle the sticky button to go back to top.
 * 
 * @author nbrest
 */
class KameHouseStickyBackToTop {

  /**
   * Load kamehouse sticky back to top plugin.
   */
  load() {
    kameHouse.logger.info("Started initializing sticky back to top");
    this.#importCss();
    this.#importHtml();
  }

  /**
   * Import the sticky button html.
   */
  async #importHtml() {
    const stickyBackToTopBtn = await kameHouse.util.fetch.loadHtmlSnippet("/kame-house/kamehouse/html/plugin/kamehouse-sticky-back-to-top.html");
    kameHouse.util.dom.append(kameHouse.util.dom.getBody(), stickyBackToTopBtn);
    this.#setupEventHandlers();
  }

  /**
   * Import the sticky button css.
   */
  #importCss() {
    kameHouse.util.dom.append(kameHouse.util.dom.getHead(), '<link rel="stylesheet" type="text/css" href="/kame-house/kamehouse/css/plugin/kamehouse-sticky-back-to-top.css">');
  }

  /**
   * Configure event handlers for the sticky back to top button.
   */
  #setupEventHandlers() {
    window.addEventListener("scroll", () => {this.#showHideStickyBackToTopBtn()});
    const stickyBackToTopBtn = document.getElementById('btn-sticky-back-to-top');  
    kameHouse.util.dom.setClick(stickyBackToTopBtn, null, (event) => {
      event.preventDefault();
      kameHouse.core.backToTop();
    });
  }

  /**
   * Show or hide the sticky button depending on the scroll location.
   */
  #showHideStickyBackToTopBtn() {
    const stickyBackToTopBtn = document.getElementById('btn-sticky-back-to-top');  
    const verticalScroll = window.scrollY;
    if (verticalScroll > 0) {
      kameHouse.util.dom.classListAdd(stickyBackToTopBtn, "active");
      kameHouse.util.dom.classListRemove(stickyBackToTopBtn, "hidden");
    } else {
      kameHouse.util.dom.classListAdd(stickyBackToTopBtn, "hidden");
      kameHouse.util.dom.classListRemove(stickyBackToTopBtn, "active");
    }
  }
}

kameHouse.ready(() => {
  kameHouse.addPlugin("stickyBackToTop", new KameHouseStickyBackToTop());
});