/**
 * Header and Footer functions for mobile.
 * 
 * Dependencies: logger, httpClient.
 * 
 * @author nbrest
 */
const mobileHeader = new MobileHeader();

/**
 * Render mobile header and footer.
 */
function renderMobileHeader() {
  logger.info("Started initializing mobile header");
  mobileHeader.renderHeader();
}

/** Header functionality */
function MobileHeader() {

  this.renderHeader = renderHeader;

  /** Render the header */
  function renderHeader() {
    domUtils.load($("#kh-mobile-tabs-wrapper"), "/kame-house-mobile/html-snippets/header.html");
  }
}