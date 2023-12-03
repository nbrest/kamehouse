/**
 * Newsletter functions.
 */
class KameHouseNewsletter {

  /**
   * Load the extension.
   */
  load() {
    kameHouse.util.dom.append($('head'), '<link rel="stylesheet" type="text/css" href="/kame-house/css/snippets/newsletter.css">');
    kameHouse.util.dom.load($("#newsletter"), "/kame-house/html-snippets/newsletter.html");
  }
}

$(document).ready(() => {
  kameHouse.addExtension("newsletter", new KameHouseNewsletter())
});