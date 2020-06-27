/**
 * Site under construction modal functionality.
 * 
 * Dependencies: logger.
 * 
 * @author nbrest
 */
var siteUnderConstructionModal;

function main() {
  var loadingModules = ["logger"];
  waitForModules(loadingModules, function initSiteUnderConstructionModal() {
    logger.info("Started initializing site under construction modal");
    siteUnderConstructionModal = new SiteUnderConstructionModal();
    siteUnderConstructionModal.import();
  });
}

function SiteUnderConstructionModal() {

  /**
   * Import modal content.
   */
  this.import = function importModal() {
    $('head').append('<link rel="stylesheet" type="text/css" href="/kame-house/css/snippets/site-under-construction-modal.css">');
    $('body').append('<div id="site-under-construction-modal" class="site-under-construction-modal">');
    $("#site-under-construction-modal").load("/kame-house/html-snippets/site-under-construction-modal.html", function() {
      var siteUnderConstructionModalDiv = document.getElementById("site-under-construction-modal");
      var siteUnderConstructionModalDivCloseBtn = document.getElementById("site-under-construction-modal-close");
      siteUnderConstructionModalDivCloseBtn.onclick = function() {
        siteUnderConstructionModalDiv.style.display = "none";
      }
      // When the user clicks anywhere outside of the modal, close it
      window.onclick = function (event) {
        if (event.target == siteUnderConstructionModalDiv) {
          siteUnderConstructionModalDiv.style.display = "none";
        }
      }
    });
  }

  /** Open site under construction modal. */
  this.open = function open() {
    logger.traceFunctionCall();
    var siteUnderConstructionModalDiv = document.getElementById("site-under-construction-modal");
    siteUnderConstructionModalDiv.style.display = "block";
  }
}

$(document).ready(main);