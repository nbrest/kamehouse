/**
 * Header and Footer functions for mobile.
 * 
 * Dependencies: logger, httpClient.
 * 
 * @author nbrest
 */
const header = new Header();
const footer = new Footer();

/**
 * Render header and footer.
 */
function renderHeaderAndFooter() {
  logger.info("Started initializing header and footer");
  header.renderHeader();
  footer.renderFooter();
}

/** Footer functionality */
function Footer() {

  this.renderFooter = renderFooter;

  /** Renders the footer */
  function renderFooter() { 
    domUtils.append($('head'), '<link rel="stylesheet" type="text/css" href="/kame-house/css/header-footer/footer.css">');
    domUtils.append($("body"), getFooterContainerDiv());
    domUtils.load($("#footerContainer"), "/html-snippets/footer.html");
  }

  function getFooterContainerDiv() {
    return domUtils.getDiv({
      id: "footerContainer"
    });
  }
}

/** Header functionality */
function Header() {

  this.renderHeader = renderHeader;

  /** Render the header */
  function renderHeader() {
    domUtils.append($('head'), '<link rel="stylesheet" type="text/css" href="/kame-house/css/header-footer/header.css">');
    domUtils.prepend($("body"), getHeaderContainerDiv());
    domUtils.load($("#headerContainer"), "/html-snippets/header.html");
  }

  /**
   * Get header container.
   */
  function getHeaderContainerDiv() {
    return domUtils.getDiv({
      id: "headerContainer"
    });
  }
}