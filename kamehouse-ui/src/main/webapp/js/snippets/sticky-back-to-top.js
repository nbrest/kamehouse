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

function StickyBackToTopManager() {
  let self = this;

  this.init = () => {
    self.importCss();
    self.importHtml();
  }

  this.importHtml = async () => {
    const response = await fetch("/kame-house/html-snippets/sticky-back-to-top.html");
    const stickyBackToTopBtn = await response.text();
    $('body').append(stickyBackToTopBtn);
    self.setupEventHandlers();
  }

  this.importCss = () => {
    $('head').append('<link rel="stylesheet" type="text/css" href="/kame-house/css/snippets/sticky-back-to-top.css">');
  }

  this.setupEventHandlers = () => {
    window.addEventListener("scroll", self.showHideStickyBackToTopBtn);
    const stickyBackToTopBtn = document.getElementById('btn-sticky-back-to-top');  
    stickyBackToTopBtn.onclick = (e) => {
      e.preventDefault();
      self.backToTop();
    }
  }

  this.showHideStickyBackToTopBtn = () => {
    const stickyBackToTopBtn = document.getElementById('btn-sticky-back-to-top');  
    let verticalScroll = window.scrollY;
    if (verticalScroll > 0) {
      stickyBackToTopBtn.className = "sticky-back-to-top-link active";
    } else {
      stickyBackToTopBtn.className = "sticky-back-to-top-link hidden";
    }
  }

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