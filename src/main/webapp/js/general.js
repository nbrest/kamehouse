/**
 * General functions for all pages
 */
function siteUnderCostructionAlert() {
  alert('The site is still under construction and this functionality has not been implemented yet.');
}

function importNewsletter(path) {
  if (path == undefined || path == null) {
    path = "";
  }
  $("#newsletter").load(path + "newsletter.html");
}
