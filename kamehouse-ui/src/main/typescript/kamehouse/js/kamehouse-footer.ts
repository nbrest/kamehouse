/**
 * Footer functions.
 * 
 * @author nbrest
 */
class KameHouseFooter {

  /** Renders the footer */
  load() { 
    kameHouse.util.dom.append(kameHouse.util.dom.getHead(), '<link rel="stylesheet" type="text/css" href="/kame-house/kamehouse/css/kamehouse-footer.css">');
    kameHouse.ready(() => {
      // load the footer after the other elements are loaded, if not it randomly puts the footer in the middle
      kameHouse.logger.info("Loading footer", null);
      kameHouse.util.dom.append(kameHouse.util.dom.getBody(), this.#getFooterContainerDiv());
      kameHouse.util.dom.loadById("kamehouse-footer-container", "/kame-house/kamehouse/html/kamehouse-footer.html", () => {
        kameHouse.core.configDynamicHtml();
        kameHouse.util.module.waitForModules(["kameHouseSession"], () => {
          this.#updateSessionStatus();
          kameHouse.util.module.setModuleLoaded("kameHouseFooter");
          kameHouse.logger.info("Finished loading footer", null);
        });
      });
    });
  }

  /** Update the server name, and build info in the footer */
  #updateFooterWithSessionInfo() {
    if (kameHouse.core.isGRootPage()) {
      return;
    }
    kameHouse.logger.info("Updating footer with kamehouse session status", null);
    if (!kameHouse.core.isEmpty(kameHouse.session.server)) {
      kameHouse.util.dom.setHtmlById("footer-server-name", kameHouse.session.server);
    }
    if (!kameHouse.core.isEmpty(kameHouse.session.buildVersion)) {
      kameHouse.util.dom.setHtmlById("footer-build-version", kameHouse.session.buildVersion);
    }
    if (!kameHouse.core.isEmpty(kameHouse.session.buildDate)) {
      kameHouse.util.dom.setHtmlById("footer-build-date", kameHouse.session.buildDate);
    }
  }

  /**
   * Get footer container div.
   */
  #getFooterContainerDiv() {
    return kameHouse.util.dom.getDiv({
      id: "kamehouse-footer-container"
    }, null);
  }

  /**
   * Wait for the footer to be loaded and then update the session status.
   */
  #updateSessionStatus() {
    this.#updateFooterWithSessionInfo();
    kameHouse.util.banner.updateServerName();
  }
}
