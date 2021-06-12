/**
 * Kamehouse modal functionality. 
 * 
 * This file contains all my different kamehouse modals and their functionality.
 * 
 * Use basic kamehouse modal to show standard errors and other messages. 
 * If the same message is used in several places, add it as a constant to the modal 
 * and create a specific open method, as I did for siteUnderConstruction.
 * 
 * TODO: BUG: With openAutoCloseable(), if while I'm on the countdown, I manually close the modal, 
 * and trigger another request that reopens the same modal, the behavior is not correct. The
 * original loop to close the modal is still running and will trigger it to close and show
 * multiple autoclose divs. This shouldn't be a problem though.
 * 
 * Dependencies: logger.
 * 
 * @author nbrest
 */
var basicKamehouseModal;
var loadingWheelModal;

function main() {
  moduleUtils.waitForModules(["logger"], () => {
    logger.info("Started initializing kamehouse modal framework");
    importKamehouseModalCss();
    basicKamehouseModal = new BasicKamehouseModal();
    basicKamehouseModal.import();
    loadingWheelModal = new LoadingWheelModal();
    loadingWheelModal.import();
  });
}

function importKamehouseModalCss() {
  $('head').append('<link rel="stylesheet" type="text/css" href="/kame-house/css/snippets/kamehouse-modal.css">');
}

/**
 * Basic KameHouse Modal Functionality.
 * 
 * This is the standard modal I would use for simple error and other messages.
 * 
 */
function BasicKamehouseModal() {
  let self = this;
  this.SITE_UNDER_CONSTRUCTION = "The site is still under construction and this functionality has not been implemented yet.";
  this.modalUtils = new ModalUtils("basic-kamehouse-modal");
  this.import = self.modalUtils.import;
  this.open = self.modalUtils.open;
  this.openAutoCloseable = self.modalUtils.openAutoCloseable;
  this.close = self.modalUtils.close;
  this.setText = self.modalUtils.setText;

  /** Open site under construction modal */
  this.openSiteUnderConstruction = () => self.modalUtils.open(self.SITE_UNDER_CONSTRUCTION);

  /** Open api call error message auto closeable modal */
  this.openApiError = (responseBody, responseCode, responseDescription) => {
    if (isNullOrUndefined(responseBody)) {
      responseBody = "Error executing the request.<br>Please check the logs for more information";
    }
    let errorMessage = "Error executing the request.<br>Response: [ code: " + responseCode + ", description: \"" + responseDescription + "\", body: " + JSON.stringify(responseBody) + " ]";
    self.openAutoCloseable(errorMessage, 7000);
  }
}

/**
 * Loading Wheel Modal.
 */
function LoadingWheelModal() {
  let self = this;
  this.modalUtils = new ModalUtils("loading-wheel-modal");
  this.import = self.modalUtils.import;
  this.openAutoCloseable = self.modalUtils.openAutoCloseable;
  this.close = self.modalUtils.close;
  this.setText = self.modalUtils.setText;

  this.open = (message) => {
    if (isNullOrUndefined(message) && !isNullOrUndefined(global.session.firstName)) {
      let chottoMatte = 'ちょっと まって';
      message = chottoMatte + ", " + global.session.firstName + "-san!";
    }
    self.modalUtils.open(message);
  }
}

/**
 * Common functionality shared by all modals.
 */
function ModalUtils(modalId) {
  let self = this;
  this.modalId = modalId;
  this.BASE_CLASS = "kamehouse-modal";
  this.DEFAULT_AUTO_CLOSE_SEC = 7000;

  /** Import modal content */
  this.import = () => {
    $('body').append('<div id="' + modalId + '" class="' + self.BASE_CLASS + '">');
    $("#" + modalId).load("/kame-house/html-snippets/" + modalId + ".html", () => {
      let modalDivCloseBtn = document.getElementById(modalId + "-close");
      modalDivCloseBtn.onclick = () => self.close();
      //self.setCloseOnClickOutsideModal();
    });
  }

  /** When the user clicks anywhere outside of the modal, close it */
  this.setCloseOnClickOutsideModal = () => {
    let modalDiv = document.getElementById(modalId);
    window.onclick = (event) => {
      if (event.target == modalDiv) {
        self.close();
      }
    }
  }

  /** Open modal */
  this.open = (message) => {
    if (!isNullOrUndefined(message)) {
      self.setText(message);
    }
    let modal = document.getElementById(modalId);
    modal.style.display = "block";
    //self.setCloseOnClickOutsideModal();
  }

  /** Open auto closeable modal */
  this.openAutoCloseable = (message, autoCloseMs) => {
    self.open(message);
    self.autoClose(autoCloseMs);
  }

  /** Close modal */
  this.close = () => {
    let modal = document.getElementById(modalId);
    modal.style.display = "none";
  }

  /** Auto close modal after the specified miliseconds */
  this.autoClose = async function autoClose(autoCloseMs) {
    logger.traceFunctionCall();
    if (isNullOrUndefined(autoCloseMs)) {
      logger.trace("autoCloseMs not set. Closing after default value of " + self.DEFAULT_AUTO_CLOSE_SEC + " ms");
      autoCloseMs = self.DEFAULT_AUTO_CLOSE_SEC;
    }
    let autoCloseId = modalId + "-autoclose";
    $("#" + modalId + "-text").after("<div id='" + autoCloseId + "' class='" + self.BASE_CLASS + "-autoclose'>");
    while (autoCloseMs > 0) {
      let secondsRemaining = autoCloseMs / 1000;
      $("#" + autoCloseId).text("Closing in " + secondsRemaining + " seconds");
      autoCloseMs = autoCloseMs - 1000;
      await sleep(1000);
    }
    $("#" + autoCloseId).remove();
    self.close();
  }

  /** Set the text in the modal */
  this.setText = (message) => $("#" + self.modalId + "-text").html(message);
}

$(document).ready(main);