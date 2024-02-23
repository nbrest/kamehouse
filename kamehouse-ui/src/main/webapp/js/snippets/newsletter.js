/**
 * Newsletter functions.
 * 
 * @author nbrest
 */
class KameHouseNewsletter {

  /**
   * Load the extension.
   */
  load() {
    kameHouse.util.dom.append(kameHouse.util.dom.getHead(), '<link rel="stylesheet" type="text/css" href="/kame-house/css/snippets/newsletter.css">');
    kameHouse.util.dom.load(document.getElementById("newsletter"), "/kame-house/html-snippets/newsletter.html");
  }
}

kameHouse.ready(() => {
  kameHouse.addExtension("newsletter", new KameHouseNewsletter())
});