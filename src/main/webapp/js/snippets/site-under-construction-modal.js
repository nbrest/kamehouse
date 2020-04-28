function main() {
  initKameHouse(importSiteUnderConstructionModal);
}

/**
 * Import newsletter content.
 */
function importSiteUnderConstructionModal() {
  $('head').append('<link rel="stylesheet" type="text/css" href="/kame-house/css/snippets/site-under-construction-modal.css">');
  $('body').append('<div id="site-under-construction-modal" class="site-under-construction-modal">');
  $("#site-under-construction-modal").load("/kame-house/html-snippets/site-under-construction-modal.html", function(){
	  var siteUnderConstructionModal = document.getElementById("site-under-construction-modal");
	  var siteUnderConstructionModalCloseBtn = document.getElementsByClassName("site-under-construction-modal-close")[0];
	  siteUnderConstructionModalCloseBtn.onclick = function() {
	  	siteUnderConstructionModal.style.display = "none";
	  }
	  // When the user clicks anywhere outside of the modal, close it
	  window.onclick = function(event) {
	    if (event.target == siteUnderConstructionModal) {
	  	  siteUnderConstructionModal.style.display = "none";
	    }
	  }
  }); 
}

/** Open site under construction modal. */
function openSiteUnderCostructionModal() {
  logger.traceFunctionCall();
	var siteUnderConstructionModal = document.getElementById("site-under-construction-modal");
	siteUnderConstructionModal.style.display = "block";
}

$(document).ready(main);