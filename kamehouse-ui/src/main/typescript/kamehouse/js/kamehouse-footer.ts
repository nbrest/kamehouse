/**
 * Footer functions.
 * 
 * @author nbrest
 */
class KameHouseFooter {

  #buildVersion = null;
  #buildDate = null;

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
    kameHouse.logger.info("Loading kamehouse ui build info", null);
    await this.#loadUiBuildVersion();
    await this.#loadUiBuildDate();
    kameHouse.logger.info("Updating footer with kamehouse ui build info", null);
    if (!kameHouse.core.isEmpty(this.#buildVersion)) {
      kameHouse.util.dom.setHtmlById("footer-build-version", this.#buildVersion);
    }
    if (!kameHouse.core.isEmpty(this.#buildDate)) {
      kameHouse.util.dom.setHtmlById("footer-build-date", this.#buildDate);
    }
  }

  /**
   * Load ui build version and override the session value if present.
   */
  async #loadUiBuildVersion() {
    const content = await kameHouse.util.fetch.loadFile('/kame-house/ui-build-version.txt');
    if (kameHouse.core.isEmpty(content)) {
      kameHouse.logger.error("Unable to load ui-build-version.txt", null);
      return;
    }
    const lineArray = content.split("=");
    let buildVersion = lineArray[1];
    if (!kameHouse.core.isEmpty(buildVersion)) {
      buildVersion = buildVersion.replace(/\n+$/, "");
      this.#buildVersion = buildVersion;
      kameHouse.logger.info("Loaded buildVersion from ui-build-version.txt: " + buildVersion, null);
    }
  }
  
  /**
   * Load ui build date and override the session value if present.
   */
  async #loadUiBuildDate() {
    const content = await kameHouse.util.fetch.loadFile('/kame-house/ui-build-date.txt');
    if (kameHouse.core.isEmpty(content)) {
      kameHouse.logger.error("Unable to load ui-build-date.txt", null);
      return;
    }
    const lineArray = content.split("=");
    let buildDate = lineArray[1];
    if (!kameHouse.core.isEmpty(buildDate)) {
      buildDate = buildDate.replace(/\n+$/, "");
      this.#buildDate = buildDate;
      kameHouse.logger.info("Loaded buildDate from ui-build-date.txt: " + buildDate, null);
    }
  }
}
