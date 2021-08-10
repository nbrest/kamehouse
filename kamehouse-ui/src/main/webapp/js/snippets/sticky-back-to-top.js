/**
 * Functionality to add a sticky back to top button.
 */
var stickyBackToTopManager;

function loadStickyBackToTop() {
  moduleUtils.waitForModules(["logger"], () => {
    logger.info("Started initializing sticky back to top");
    stickyBackToTopManager = new StickyBackToTopManager();
    stickyBackToTopManager.init();
  });
}

/**
 * Manager to handle the sticky button to go back to top.
 */
function StickyBackToTopManager() {
  let self = this;

  this.init = () => {
    self.importCss();
    self.importHtml();
  }

  /**
   * Import the sticky button html.
   */
  this.importHtml = async () => {
    const stickyBackToTopBtn = await domUtils.loadHtmlSnippet("/kame-house/html-snippets/sticky-back-to-top.html");
    domUtils.append($('body'), stickyBackToTopBtn);
    self.setupEventHandlers();
  }

  /**
   * Import the sticky button css.
   */
  this.importCss = () => {
    domUtils.append($('head'), '<link rel="stylesheet" type="text/css" href="/kame-house/css/snippets/sticky-back-to-top.css">');
  }

  /**
   * Configure event handlers for the sticky back to top button.
   */
  this.setupEventHandlers = () => {
    window.addEventListener("scroll", self.showHideStickyBackToTopBtn);
    const stickyBackToTopBtn = document.getElementById('btn-sticky-back-to-top');  
    domUtils.setOnClick(stickyBackToTopBtn, (e) => {
      e.preventDefault();
      self.backToTop();
    });
  }

  /**
   * Show or hide the sticky button depending on the scroll location.
   */
  this.showHideStickyBackToTopBtn = () => {
    const stickyBackToTopBtn = document.getElementById('btn-sticky-back-to-top');  
    let verticalScroll = window.scrollY;
    if (verticalScroll > 0) {
      domUtils.classListAdd(stickyBackToTopBtn, "active");
      domUtils.classListRemove(stickyBackToTopBtn, "hidden");
    } else {
      domUtils.classListAdd(stickyBackToTopBtn, "hidden");
      domUtils.classListRemove(stickyBackToTopBtn, "active");
    }
  }

  /**
   * Scroll back to the top of the page.
   */
  this.backToTop = () => {
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

$(document).ready(loadStickyBackToTop);