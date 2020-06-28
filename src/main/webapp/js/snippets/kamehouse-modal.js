/**
 * Kamehouse modal functionality. 
 * Use this modal to show errors and other messages. If the same message is used in several places, add it as a constant to the modal and create a specific open method, as I did for siteUnderConstruction.
 * 
 * Dependencies: logger.
 * 
 * @author nbrest
 */
var kamehouseModal;

function main() {
  var loadingModules = ["logger"];
  waitForModules(loadingModules, function initKamehouseModal() {
    logger.info("Started initializing kamehouse modal");
    kamehouseModal = new KamehouseModal();
    kamehouseModal.import();
  });
}

function KamehouseModal() {

  self = this;
  const siteUnderConstruction = "The site is still under construction and this functionality has not been implemented yet.";

  /**
   * Import modal content.
   */
  this.import = function importModal() {
    $('head').append('<link rel="stylesheet" type="text/css" href="/kame-house/css/snippets/kamehouse-modal.css">');
    $('body').append('<div id="kamehouse-modal" class="kamehouse-modal">');
    $("#kamehouse-modal").load("/kame-house/html-snippets/kamehouse-modal.html", function () {
      var kamehouseModalDiv = document.getElementById("kamehouse-modal");
      var kamehouseModalDivCloseBtn = document.getElementById("kamehouse-modal-close");
      kamehouseModalDivCloseBtn.onclick = function () {
        self.close();
      }
      // When the user clicks anywhere outside of the modal, close it
      window.onclick = function (event) {
        if (event.target == kamehouseModalDiv) {
          self.close();
        }
      }
      modules.kamehouseModal = true;
    });
  }

  /** Open modal */
  this.open = function open(message) {
    logger.traceFunctionCall();
    if (!isEmpty(message)) {
      self.setText(message);
    }
    let modal = document.getElementById("kamehouse-modal");
    modal.style.display = "block";
  }

  /** Close modal */
  this.close = function close() {
    logger.traceFunctionCall();
    let modal = document.getElementById("kamehouse-modal");
    modal.style.display = "none";
  }

  /** Set the text in the modal */
  this.setText = function setText(message) {
    logger.traceFunctionCall();
    $("#kamehouse-modal-text").text(message);
  }

  /** Open site under construction modal */
  this.openSiteUnderConstruction = function openSiteUnderConstruction() {
    self.open(siteUnderConstruction);
  }
}

$(document).ready(main);