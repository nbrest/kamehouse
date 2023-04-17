/**
 * Functionality to add a sticky back to top button.
 */
var stickyBackToTopManager;

function loadStickyBackToTop() {
  kameHouse.logger.info("Started initializing sticky back to top");
  stickyBackToTopManager = new StickyBackToTopManager();
  stickyBackToTopManager.init();
}

/**
 * Manager to handle the sticky button to go back to top.
 */
function StickyBackToTopManager() {

  this.init = init;

  function init() {
    importCss();
    importHtml();
  }

  /**
   * Import the sticky button html.
   */
  async function importHtml() {
    const stickyBackToTopBtn = await kameHouse.util.fetch.loadHtmlSnippet("/kame-house/html-snippets/sticky-back-to-top.html");
    kameHouse.util.dom.append($('body'), stickyBackToTopBtn);
    setupEventHandlers();
  }

  /**
   * Import the sticky button css.
   */
  function importCss() {
    kameHouse.util.dom.append($('head'), '<link rel="stylesheet" type="text/css" href="/kame-house/css/snippets/sticky-back-to-top.css">');
  }

  /**
   * Configure event handlers for the sticky back to top button.
   */
  function setupEventHandlers() {
    window.addEventListener("scroll", showHideStickyBackToTopBtn);
    const stickyBackToTopBtn = document.getElementById('btn-sticky-back-to-top');  
    kameHouse.util.dom.setOnClick(stickyBackToTopBtn, (e) => {
      e.preventDefault();
      backToTop();
    });
  }

  /**
   * Show or hide the sticky button depending on the scroll location.
   */
  function showHideStickyBackToTopBtn() {
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
  function backToTop() {
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