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
        this.#updateFooterWithBuildInfo();
        this.#removeFooterModuleOnMobile();
        kameHouse.util.module.waitForModules(["kameHouseSession"], () => {
          this.#updateFooterWithSessionInfo();
          kameHouse.util.module.setModuleLoaded("kameHouseFooter");
          kameHouse.logger.info("Finished loading footer", null);
        });
      });
    });
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
   * Update the server name in the footer with kamehouse session info. 
   */
  #updateFooterWithSessionInfo() {
    if (kameHouse.core.isGRootPage()) {
      return;
    }
    kameHouse.logger.info("Updating footer with kamehouse session info", null);
    if (!kameHouse.core.isEmpty(kameHouse.session.server)) {
      kameHouse.util.dom.setHtmlById("footer-server-name", kameHouse.session.server);
    }
  }

  /** 
   * Update the kamehouse ui build info in the footer.
   */
  async #updateFooterWithBuildInfo() {
    kameHouse.logger.info("Updating footer with kamehouse ui build info", null);
    const content = await kameHouse.util.fetch.loadFile('/kame-house/build-info.json');
    if (kameHouse.core.isEmpty(content)) {
      kameHouse.logger.error("Unable to load build-info.json", null);
      return;
    }
    const buildInfo = kameHouse.json.parse(content);
    kameHouse.logger.info("Loaded footer ui buildInfo: " + content, null);
    if (!kameHouse.core.isEmpty(buildInfo.buildVersion)) {
      kameHouse.util.dom.setHtmlById("footer-build-version", buildInfo.buildVersion);
    }
    if (!kameHouse.core.isEmpty(buildInfo.buildDate)) {
      kameHouse.util.dom.setHtmlById("footer-build-date", buildInfo.buildDate);
    }
  }

  /**
   * Removing footer module on mobile app.
   */
  #removeFooterModuleOnMobile() {
    kameHouse.util.mobile.exec(
        () => {},
        () => {
          kameHouse.logger.debug("Removing module from footer in mobile app", null);
          kameHouse.util.dom.removeById("footer-module");
        }
      );
  }
}
