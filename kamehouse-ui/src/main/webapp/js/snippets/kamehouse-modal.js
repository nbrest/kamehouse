/**
 * Kamehouse modal functionality. 
 * 
 * This file contains all my different kamehouse modals and their functionality.
 * 
 * Use basic kamehouse modal to show standard errors and other messages. 
 * If the same message is used in several places, add it as a constant to the modal 
 * and create a specific open method, as I did for siteUnderConstruction.
 * 
 * Dependencies: logger.
 * 
 * @author nbrest
 */
var basicKamehouseModal;
var loadingWheelModal;

function main() {
  logger.info("Started initializing kamehouse modal framework");
  importKamehouseModalCss();
  basicKamehouseModal = new BasicKamehouseModal();
  basicKamehouseModal.import();
  loadingWheelModal = new LoadingWheelModal();
  loadingWheelModal.import();
}

function importKamehouseModalCss() {
  domUtils.append($('head'), '<link rel="stylesheet" type="text/css" href="/kame-house/css/snippets/kamehouse-modal.css">');
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

  const modalUtils = new ModalUtils("basic-kamehouse-modal");

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
  function openApiError(responseBody, responseCode, responseDescription) {
    if (isEmpty(responseBody)) {
      responseBody = getEmptyResponseBodyText();
    }
    modalUtils.open(getErrorMessage(responseBody, responseCode, responseDescription));
  }

  function getEmptyResponseBodyText() {
    const message = domUtils.getSpan({}, "Error executing the request.");
    domUtils.append(message, domUtils.getBr());
    domUtils.append(message, "Please check the logs for more information");
    return message;
  }
  
  function getErrorMessage(responseBody, responseCode, responseDescription) {
    const message = domUtils.getSpan({}, "Error executing the request.");
    domUtils.append(message, domUtils.getBr());
    domUtils.append(message, domUtils.getBr());
    domUtils.append(message, "Response code: " + responseCode);
    domUtils.append(message, domUtils.getBr());
    domUtils.append(message, "Response description: " + responseDescription);
    domUtils.append(message, domUtils.getBr());
    domUtils.append(message, "Response body: ");
    domUtils.append(message, domUtils.getBr());
    const bodySpan = domUtils.getSpan({}, null);
    domUtils.setText(bodySpan, JSON.stringify(responseBody));
    domUtils.append(message, bodySpan);
    return message;
  }
}

/**
 * Loading Wheel Modal.
 */
function LoadingWheelModal() {

  this.open = open;

  const modalUtils = new ModalUtils("loading-wheel-modal");

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
    if (isEmpty(message) && !isEmpty(global.session.firstName)) {
      const chottoMatte = 'ちょっと まって';
      message = chottoMatte + ", " + global.session.firstName + "-san!";
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
    const modalDiv = await fetchUtils.loadHtmlSnippet("/kame-house/html-snippets/" + modalId + ".html");
    domUtils.append($('body'), modalDiv);
    const modalDivCloseBtn = document.getElementById(modalId + "-close");
    domUtils.setOnClick(modalDivCloseBtn, () => close());
  }

  /** Open modal */
  function open(message) {
    if (!isEmpty(message)) {
      setHtml(message);
    }
    const modal = document.getElementById(modalId);
    domUtils.setDisplay(modal, "block");
  }

  /** Open auto closeable modal */
  function openAutoCloseable(message, autoCloseMs) {
    open(message);
    autoClose(autoCloseMs);
  }

  /** Close modal */
  function close() {
    const modal = document.getElementById(modalId);
    domUtils.setDisplay(modal, "none");
  }

  /** Auto close modal after the specified miliseconds */
  async function autoClose(autoCloseMs) {
    if (isEmpty(autoCloseMs)) {
      logger.trace("autoCloseMs not set. Closing after default value of " + DEFAULT_AUTO_CLOSE_SEC + " ms");
      autoCloseMs = DEFAULT_AUTO_CLOSE_SEC;
    }
    const autoCloseId = modalId + "-autoclose";
    domUtils.removeClass($("#" + autoCloseId), "hidden-kh");
    while (autoCloseMs > 0) {
      const secondsRemaining = autoCloseMs / 1000;
      domUtils.setHtml($("#" + autoCloseId), "Closing in " + secondsRemaining + " seconds");
      autoCloseMs = autoCloseMs - 1000;
      await sleep(1000);
    }
    domUtils.addClass($("#" + autoCloseId), "hidden-kh");
    close();
  }

  /** Set the html in the modal */
  function setHtml(message) { domUtils.setHtml($("#" + modalId + "-text"), message); }

  /** Append the message to the modal */
  function appendHtml(message) { domUtils.append($("#" + modalId + "-text"), message); }

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

$(document).ready(main);