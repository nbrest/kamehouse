/**
 * General functions for all pages
 */
function main() {
  importNewsletter();
}

function siteUnderCostructionAlert() {
  alert('The site is still under construction and this functionality has not been implemented yet.');
}

function importNewsletter() {
  $("#newsletter").load("/kame-house/html/newsletter.html");
}

$(document).ready(main);