/**
 * Loading wheel modal functionality.
 * 
 * Dependencies: logger.
 * 
 * @author nbrest
 */
var loadingWheelModal;

function main() {
  var loadingModules = ["logger"];
  waitForModules(loadingModules, function initSiteUnderConstructionModal() {
    logger.info("Started initializing loading wheel modal");
    loadingWheelModal = new LoadingWheelModal();
    loadingWheelModal.import();
  });
}

function LoadingWheelModal() {
  let self = this;
  /**
   * Import modal content.
   */
  this.import = function importModal() {
    $('head').append('<link rel="stylesheet" type="text/css" href="/kame-house/css/snippets/loading-wheel-modal.css">');
    $('body').append('<div id="loading-wheel-modal" class="loading-wheel-modal">');
    $("#loading-wheel-modal").load("/kame-house/html-snippets/loading-wheel-modal.html", function() {
      var loadingWheelModalDiv = document.getElementById("loading-wheel-modal");
      var loadingWheelModalCloseBtn = document.getElementById("loading-wheel-modal-close");
      loadingWheelModalCloseBtn.onclick = function () {
        loadingWheelModalDiv.style.display = "none";
      }
    });
  }

  /** Open modal */
  this.open = function open(message) {
    logger.traceFunctionCall();
    if (!isEmpty(message)) {
     self.setText(message);
    } else {
      if (!isEmpty(global.session.firstName)) {
        let chottoMatte = 'ちょっと まって';
        self.setText(chottoMatte  + ", " + global.session.firstName + "-san!");
      }
    }
    let modal = document.getElementById("loading-wheel-modal");
    modal.style.display = "block";
  }

  /** Close modal */
  this.close = function close() {
    logger.traceFunctionCall();
    let modal = document.getElementById("loading-wheel-modal");
    modal.style.display = "none";
  }

  /** Set the text in the modal */
  this.setText = function setText(message) {
    logger.traceFunctionCall();
    $("#loading-wheel-modal-text").text(message);
  }
}

$(document).ready(main);