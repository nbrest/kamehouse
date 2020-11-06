/**
 * Admin Test APIs functions.
 * 
 * Dependencies: logger, apiCallTable.
 * 
 * @author nbrest
 */
var apiTester;

var main = () => {
  bannerUtils.setRandomPrinceOfTennisBanner();
  importTestApisCss();
  moduleUtils.waitForModules(["logger", "apiCallTable"], () => {
    logger.info("Started initializing test apis");
    apiTester = new ApiTester();
  });
};

/** Import test apis css */
function importTestApisCss() {
  $('head').append('<link rel="stylesheet" type="text/css" href="/kame-house/css/admin/test-apis.css">');
}

function ApiTester() {

  /** Reload VLC with the file passed as a parameter. */
  this.loadFileInVlc = (url, file) => {
    logger.debug("Selected file: " + file);
    let requestParam = "file=" + file;
    apiCallTable.postUrlEncoded(url, requestParam);
  }

  this.get = (url) => {
    apiCallTable.get(url);
    scrollToTop();
  }

  this.postUrlEncoded = (url, requestParam) => {
    apiCallTable.postUrlEncoded(url, requestParam);
    scrollToTop();
  }
}

/**
 * Call main.
 */
$(document).ready(main);