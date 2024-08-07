/**
 * Kamehouse modal functionality. 
 * 
 * This file contains all my different kamehouse modals and their functionality.
 * 
 * Use basic kamehouse modal to show standard errors and other messages. 
 * If the same message is used in several places, add it as a constant to the modal 
 * and create a specific open method, as I did for siteUnderConstruction.
 * 
 * Dependencies: kameHouse.logger
 * 
 * @author nbrest
 */
class KameHouseModalLoader {

  basicModal = new BasicKamehouseModal();
  loadingWheelModal = new LoadingWheelModal();

  /**
   * Load the kamehouse modal plugin.
   */
  async load() {
    kameHouse.logger.info("Started initializing kamehouse modal framework", null);
    this.#importKamehouseModalCss();
    await this.basicModal.import();
    await this.loadingWheelModal.import();
    kameHouse.util.module.setModuleLoaded("kameHouseModal");
  }

  /**
   * Import css.
   */
  #importKamehouseModalCss() {
    kameHouse.util.dom.append(kameHouse.util.dom.getHead(), '<link rel="stylesheet" type="text/css" href="/kame-house/kamehouse/css/plugin/kamehouse-modal.css">');
  }
}

/**
 * Common functionality shared by all kamehouse modals.
 * 
 * @author nbrest
 */
class AbstractKameHouseModal {
  
  #DEFAULT_AUTO_CLOSE_SEC = 7000;

  #isErrorMessage = false;
  #isOpen = false;
  #modalId = null;

  constructor(modalId) {
    this.#modalId = modalId;
  }

  /** Import modal content */
  async import() {
    const modalDiv = await kameHouse.util.fetch.loadHtmlSnippet("/kame-house/kamehouse/html/plugin/" + this.#modalId + ".html");
    kameHouse.util.dom.append(kameHouse.util.dom.getBody(), modalDiv);
    const modalDivCloseBtn = document.getElementById(this.#modalId + "-close");
    kameHouse.util.dom.setClick(modalDivCloseBtn, null, () => this.close());
    kameHouse.logger.info("Imported " + this.#modalId, null);
  }

  /** 
   * Open modal.
   * 
   * Returns true if it opens the modal on this call. False if the modal is already open and skips this call.
   **/
  open(message) {
    if (this.#isOpen) {
      kameHouse.logger.error("There's a " + this.#modalId + " already open. Skipping this open call", null);
      return;
    }
    this.#isOpen = true;
    kameHouse.logger.debug("Opening modal " + this.#modalId, null);
    if (!kameHouse.core.isEmpty(message)) {
      this.setHtml(message);
    }
    const modal = document.getElementById(this.#modalId);
    kameHouse.util.dom.setDisplay(modal, "block");
  }

  /** Open auto closeable modal */
  openAutoCloseable(message, autoCloseMs) {
    this.open(message);
    this.autoClose(autoCloseMs);
  }

  /** Close modal */
  close() {
    this.#isOpen = false;
    kameHouse.logger.debug("Closing modal " + this.#modalId, null);
    const modal = document.getElementById(this.#modalId);
    kameHouse.util.dom.setDisplay(modal, "none");
  }

  /** Auto close modal after the specified miliseconds */
  async autoClose(autoCloseMs) {
    if (kameHouse.core.isEmpty(autoCloseMs)) {
      kameHouse.logger.trace("autoCloseMs not set. Closing after default value of " + this.#DEFAULT_AUTO_CLOSE_SEC + " ms", null);
      autoCloseMs = this.#DEFAULT_AUTO_CLOSE_SEC;
    }
    const autoCloseId = this.#modalId + "-autoclose";
    kameHouse.util.dom.classListRemoveById(autoCloseId, "hidden-kh");
    while (autoCloseMs > 0) {
      const secondsRemaining = autoCloseMs / 1000;
      kameHouse.util.dom.setHtmlById(autoCloseId, "Closing in " + secondsRemaining + " seconds");
      autoCloseMs = autoCloseMs - 1000;
      if (!this.#isOpen) {
        kameHouse.logger.debug(this.#modalId + " is already closed. Leaving autoClose function", null);
        return;
      }
      await kameHouse.core.sleep(1000);
    }
    kameHouse.util.dom.classListAddById(autoCloseId, "hidden-kh");
    this.close();
  }

  /** Set the html in the modal */
  setHtml(message) { kameHouse.util.dom.setHtmlById(this.#modalId + "-text", message); }

  /** Append the message to the modal */
  appendHtml(message) { kameHouse.util.dom.appendById(this.#modalId + "-text", message); }

  /**
   * Check if it's an error message.
   */
  isErrorMessage() {
    return this.#isErrorMessage;
  } 

  /**
   * Set error message.
   */
  setIsErrorMessage(val) {
    this.#isErrorMessage = val;
  }

  /**
   * Reset modal contents.
   */
  reset() {
    this.setHtml("");
    this.setIsErrorMessage(false);
  }
}

/**
 * Basic KameHouse Modal Functionality.
 * 
 * This is the standard modal I would use for simple error and other messages.
 * 
 * @author nbrest
 */
class BasicKamehouseModal extends AbstractKameHouseModal {

  constructor() {
    super("kamehouse-modal-basic");
  }

  /** Open site under construction modal */
  openSiteUnderConstruction() { 
    const html = kameHouse.util.dom.getDiv(null, null);
    kameHouse.util.dom.append(html, kameHouse.util.dom.getImg({
      src: "/kame-house/img/other/warning-sign.png",
      className: "under-construction-img",
      alt: "Under construction"
    }));
    kameHouse.util.dom.append(html, kameHouse.util.dom.getDiv({
      class: "under-construction-not-implemented-text"
    }, "KameHouse is still under construction and this functionality has not been implemented yet"));
    kameHouse.util.dom.append(html, kameHouse.util.dom.getDiv({
      class: "under-construction-low-priority-text"
    }, "Let's face it, this is low priority and will probably never get done"));
    super.open(html); 
  }

  /** Open api call error message auto closeable modal */
  openApiError(responseBody, responseCode, responseDescription, responseHeaders) {
    if (kameHouse.core.isEmpty(responseBody)) {
      responseBody = this.#getEmptyResponseBodyText();
    }
    super.open(this.#getErrorMessage(responseBody, responseCode, responseDescription, responseHeaders));
  }

  /**
   * Get empty response body text.
   */
  #getEmptyResponseBodyText() {
    const message = kameHouse.util.dom.getSpan({}, "Error executing the request.");
    kameHouse.util.dom.append(message, kameHouse.util.dom.getBr());
    kameHouse.util.dom.append(message, "Please check the logs for more information");
    return message;
  }
  
  /**
   * Get error message.
   */
  #getErrorMessage(responseBody, responseCode, responseDescription, responseHeaders) {
    const message = kameHouse.util.dom.getSpan({}, "Error executing the request.");
    kameHouse.util.dom.append(message, kameHouse.util.dom.getBr());
    kameHouse.util.dom.append(message, kameHouse.util.dom.getBr());
    kameHouse.util.dom.append(message, "Response code: " + responseCode);
    kameHouse.util.dom.append(message, kameHouse.util.dom.getBr());
    kameHouse.util.dom.append(message, "Response description: " + responseDescription);
    kameHouse.util.dom.append(message, kameHouse.util.dom.getBr());
    kameHouse.util.dom.append(message, "Response body: ");
    kameHouse.util.dom.append(message, kameHouse.util.dom.getBr());
    const bodySpan = kameHouse.util.dom.getSpan({}, null);
    kameHouse.util.dom.setText(bodySpan, kameHouse.json.stringify(responseBody, null, null));
    kameHouse.util.dom.append(message, bodySpan);
    return message;
  }
}

/**
 * Loading Wheel Modal.
 * 
 * @author nbrest
 */
class LoadingWheelModal extends AbstractKameHouseModal {

  constructor() {
    super("kamehouse-modal-loading-wheel");
  }

  /**
   * Open modal.
   * 
   * @override
   */
  open(message) {
    if (kameHouse.core.isEmpty(message) && !kameHouse.core.isEmpty(kameHouse.session.firstName)) {
      const chottoMatte = 'ちょっと まって';
      message = chottoMatte + ", " + kameHouse.session.firstName + "-san!";
    }
    super.open(message);
  }
}

kameHouse.ready(() => {
  kameHouse.addPlugin("modal", new KameHouseModalLoader());
});