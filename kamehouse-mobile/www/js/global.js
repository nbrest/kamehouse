
/**
 * Global functions for kamehouse-mobile app.
 * 
 * @author nbrest
 */
const cordovaManager = new CordovaManager();

function mainGlobalMobile() {
  logger.info("Started initializing mobile global");
  cordovaManager.setCordovaMock();
} 

function CordovaManager() {

  this.setCordovaMock = setCordovaMock;
  this.openBrowser = openBrowser;

  const mediaServerUrl = 'http://192.168.0.109/kame-house/vlc-player';
  const twBookingServerUrl = 'https://kame.nicobrest.com/kame-house/tennisworld/booking-response.html';
  const vmUbuntuServerUrl = 'https://vm-ubuntu.nicobrest.com/kame-house/';
  const dockerDemoServerUrl = 'https://docker-demo.nicobrest.com/kame-house/';
  const jenkinsUrl = 'https://jenkins.nicobrest.com/';
  const target = '_self';
  const options = "location=no,hideurlbar=yes,hidenavigationbuttons=yes,toolbarcolor=#000000,closebuttoncolor=#d90000,zoom=no,clearcache=no,footer=yes,footercolor=#000000";

  /**
   * Open inAppBrowser with
   */
  function openBrowser(urlLookup) {
    switch (urlLookup) {
      case "mediaServer":
        openInAppBrowser(mediaServerUrl);
        break;
      case "twBookingServer":
        openInAppBrowser(twBookingServerUrl);
        break;
      case "vmUbuntuServer":
        openInAppBrowser(vmUbuntuServerUrl);
        break;
      case "dockerDemoServer":
        openInAppBrowser(dockerDemoServerUrl);
        break;
      case "jenkins":
        openInAppBrowser(jenkinsUrl);
        break;
      default:
        logger.error("Invalid urlLookup name passed to openBrowser: " + urlLookup);
        break;
    }
  }

  /**
   * Open the InAppBrowser with the specified url.
   */
  function openInAppBrowser(url) {
    cordova.InAppBrowser.open(url, target, options);
  }

  /**
   * Mock cordova when it's not set.
   */
   function setCordovaMock() {
    const urlParams = new URLSearchParams(window.location.search);
    const mockCordova = urlParams.get('mockCordova');
    if (mockCordova) {
      logger.info("Mocking cordova object");
      cordova = new CordovaMock();
    }
    function CordovaMock() {
      this.InAppBrowser = new InAppBrowserMock();
      
      function InAppBrowserMock() {
        this.open = open;
    
        function open(url, target, options) {
          alert("cordova.InAppBrowser.open() call with:\n\nurl:\n" + url + "\n\ntarget:\n" + target + "\n\noptions:\n" + options);
        }
      }
    }  
  }
}

/** Call main. */
$(document).ready(mainGlobalMobile);