/**
 * General functions for all pages.
 * 
 * @author nbrest
 */
function main() {
  importNewsletter();
}

/**
 * Site under construction message.
 */
function siteUnderCostructionAlert() {
  alert('The site is still under construction and this functionality has not been implemented yet.');
}

/**
 * Import newsletter content.
 */
function importNewsletter() {
  $("#newsletter").load("/kame-house/html/newsletter.html");
}

/**
 * Call main.
 */
$(document).ready(main);