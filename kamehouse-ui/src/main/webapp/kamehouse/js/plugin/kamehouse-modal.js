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
function KameHouseModal() {
  this.load = load;

  function load() {
    kameHouse.logger.info("Started initializing kamehouse modal framework");
    importKamehouseModalCss();
    kameHouse.plugin.modal.basicModal = new BasicKamehouseModal();
    kameHouse.plugin.modal.basicModal.import();
    kameHouse.plugin.modal.loadingWheelModal = new LoadingWheelModal();
    kameHouse.plugin.modal.loadingWheelModal.import();
  }

  function importKamehouseModalCss() {
    kameHouse.util.dom.append($('head'), '<link rel="stylesheet" type="text/css" href="/kame-house/kamehouse/css/plugin/kamehouse-modal.css">');
  }
}

/**
 * Basic KameHouse Modal Functionality.
 * 
 * This is the standard modal I would use for simple error and other messages.
 * 
 */
function BasicKamehouseModal() {
  
  this.openSiteUnderConstruction = openSiteUnderConstruction;
  this.openApiError = openApiError;

  const modalUtils = new ModalUtils("kamehouse-modal-basic");

  this.import = modalUtils.importSnippet;
  this.open = modalUtils.open;
  this.openAutoCloseable = modalUtils.openAutoCloseable;
  this.close = modalUtils.close;
  this.setHtml = modalUtils.setHtml;
  this.appendHtml = modalUtils.appendHtml;
  this.isErrorMessage = modalUtils.isErrorMessage;
  this.setErrorMessage = modalUtils.setErrorMessage;
  this.reset = modalUtils.reset;

  const SITE_UNDER_CONSTRUCTION = "The site is still under construction and this functionality has not been implemented yet.";

  /** Open site under construction modal */
  function openSiteUnderConstruction() { modalUtils.open(SITE_UNDER_CONSTRUCTION); }

  /** Open api call error message auto closeable modal */
  function openApiError(responseBody, responseCode, responseDescription, responseHeaders) {
    if (kameHouse.core.isEmpty(responseBody)) {
      responseBody = getEmptyResponseBodyText();
    }
    modalUtils.open(getErrorMessage(responseBody, responseCode, responseDescription, responseHeaders));
  }

  function getEmptyResponseBodyText() {
    const message = kameHouse.util.dom.getSpan({}, "Error executing the request.");
    kameHouse.util.dom.append(message, kameHouse.util.dom.getBr());
    kameHouse.util.dom.append(message, "Please check the logs for more information");
    return message;
  }
  
  function getErrorMessage(responseBody, responseCode, responseDescription, responseHeaders) {
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
    kameHouse.util.dom.setText(bodySpan, JSON.stringify(responseBody));
    kameHouse.util.dom.append(message, bodySpan);
    return message;
  }
}

/**
 * Loading Wheel Modal.
 */
function LoadingWheelModal() {

  this.open = open;

  const modalUtils = new ModalUtils("kamehouse-modal-loading-wheel");

  this.import = modalUtils.importSnippet;
  this.openAutoCloseable = modalUtils.openAutoCloseable;
  this.close = modalUtils.close;
  this.setHtml = modalUtils.setHtml;
  this.appendHtml = modalUtils.appendHtml;
  this.isErrorMessage = modalUtils.isErrorMessage;
  this.setErrorMessage = modalUtils.setErrorMessage;
  this.reset = modalUtils.reset;

  /**
   * Open modal.
   */
  function open(message) {
    if (kameHouse.core.isEmpty(message) && !kameHouse.core.isEmpty(kameHouse.session.firstName)) {
      const chottoMatte = 'ちょっと まって';
      message = chottoMatte + ", " + kameHouse.session.firstName + "-san!";
    }
    modalUtils.open(message);
  }
}

/**
 * Common functionality shared by all modals.
 */
function ModalUtils(modalId) {

  this.importSnippet = importSnippet;
  this.open = open;
  this.openAutoCloseable = openAutoCloseable;
  this.close = close;
  this.autoClose = autoClose;
  this.setHtml = setHtml;
  this.appendHtml = appendHtml;
  this.isErrorMessage = isErrorMessage;
  this.setErrorMessage = setErrorMessage;
  this.reset = reset;

  let isErrorMessageValue = false;

  const DEFAULT_AUTO_CLOSE_SEC = 7000;

  /** Import modal content */
  async function importSnippet() {
    const modalDiv = await kameHouse.util.fetch.loadHtmlSnippet("/kame-house/kamehouse/html/plugin/" + modalId + ".html");
    kameHouse.util.dom.append($('body'), modalDiv);
    const modalDivCloseBtn = document.getElementById(modalId + "-close");
    kameHouse.util.dom.setOnClick(modalDivCloseBtn, () => close());
  }

  /** Open modal */
  function open(message) {
    if (!kameHouse.core.isEmpty(message)) {
      setHtml(message);
    }
    const modal = document.getElementById(modalId);
    kameHouse.util.dom.setDisplay(modal, "block");
  }

  /** Open auto closeable modal */
  function openAutoCloseable(message, autoCloseMs) {
    open(message);
    autoClose(autoCloseMs);
  }

  /** Close modal */
  function close() {
    const modal = document.getElementById(modalId);
    kameHouse.util.dom.setDisplay(modal, "none");
  }

  /** Auto close modal after the specified miliseconds */
  async function autoClose(autoCloseMs) {
    if (kameHouse.core.isEmpty(autoCloseMs)) {
      kameHouse.logger.trace("autoCloseMs not set. Closing after default value of " + DEFAULT_AUTO_CLOSE_SEC + " ms");
      autoCloseMs = DEFAULT_AUTO_CLOSE_SEC;
    }
    const autoCloseId = modalId + "-autoclose";
    kameHouse.util.dom.removeClass($("#" + autoCloseId), "hidden-kh");
    while (autoCloseMs > 0) {
      const secondsRemaining = autoCloseMs / 1000;
      kameHouse.util.dom.setHtml($("#" + autoCloseId), "Closing in " + secondsRemaining + " seconds");
      autoCloseMs = autoCloseMs - 1000;
      await kameHouse.core.sleep(1000);
    }
    kameHouse.util.dom.addClass($("#" + autoCloseId), "hidden-kh");
    close();
  }

  /** Set the html in the modal */
  function setHtml(message) { kameHouse.util.dom.setHtml($("#" + modalId + "-text"), message); }

  /** Append the message to the modal */
  function appendHtml(message) { kameHouse.util.dom.append($("#" + modalId + "-text"), message); }

  function isErrorMessage() {
    return isErrorMessageValue;
  } 

  function setErrorMessage(val) {
    isErrorMessageValue = val;
  }

  function reset() {
    setHtml("");
    setErrorMessage(false);
  }
}

$(document).ready(() => {
  kameHouse.addPlugin("modal", new KameHouseModal());
});