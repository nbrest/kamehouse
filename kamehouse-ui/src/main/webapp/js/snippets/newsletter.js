/**
 * Newsletter functions.
 */
function mainNewsletter() {
  importNewsletter();
}

/**
 * Import newsletter content.
 */
function importNewsletter() {
  kameHouse.util.dom.append($('head'), '<link rel="stylesheet" type="text/css" href="/kame-house/css/snippets/newsletter.css">');
  kameHouse.util.dom.load($("#newsletter"), "/kame-house/html-snippets/newsletter.html");
}

/**
 * Call main.
 */
$(document).ready(mainNewsletter);