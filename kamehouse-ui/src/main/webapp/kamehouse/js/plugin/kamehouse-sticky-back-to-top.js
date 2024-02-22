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
    kameHouse.util.dom.append(document.body, stickyBackToTopBtn);
    this.#setupEventHandlers();
  }

  /**
   * Import the sticky button css.
   */
  #importCss() {
    kameHouse.util.dom.append(document.head, '<link rel="stylesheet" type="text/css" href="/kame-house/kamehouse/css/plugin/kamehouse-sticky-back-to-top.css">');
  }

  /**
   * Configure event handlers for the sticky back to top button.
   */
  #setupEventHandlers() {
    window.addEventListener("scroll", this.#showHideStickyBackToTopBtn);
    const stickyBackToTopBtn = document.getElementById('btn-sticky-back-to-top');  
    kameHouse.util.dom.setOnClick(stickyBackToTopBtn, (e) => {
      e.preventDefault();
      this.#backToTop();
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

  /**
   * Scroll back to the top of the page.
   */
  #backToTop() {
    const currentHeight = document.documentElement.scrollTop || document.body.scrollTop;
    if (currentHeight > 0) {
      window.scrollTo({
        top: 0,
        left: 0,
        behavior: 'smooth'
      });
    }
  }
}

kameHouse.ready(() => {
  kameHouse.addPlugin("stickyBackToTop", new KameHouseStickyBackToTop());
});