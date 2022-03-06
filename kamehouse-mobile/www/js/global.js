
/**
 * Global functions for kamehouse-mobile app.
 * 
 * @author nbrest
 */
 function mainIndexMobileRoot() {
   logger.info("Started initializing mobile root app");

   const mediaServerUrl = 'http://192.168.0.109/kame-house/vlc-player';
   const twBookingServerUrl = 'https://kame.nicobrest.com/kame-house/tennisworld/booking-response.html';
   const jenkinsUrl = 'https://jenkins.nicobrest.com/';
   const target = '_self';
   const options = "location=no,hideurlbar=yes,hidenavigationbuttons=yes,toolbarcolor=#000000,closebuttoncolor=#d90000,zoom=no,clearcache=no,footer=yes,footercolor=#000000";
   
   document.addEventListener("deviceready", onDeviceReady, false);
   
   function onDeviceReady() {
      var ref = cordova.InAppBrowser.open(mediaServerUrl, target, options);
      window.open = cordova.InAppBrowser.open;
    }
   
   document.getElementById("media-button").addEventListener("click", openMediaServer);
   function openMediaServer() {
    var ref = cordova.InAppBrowser.open(mediaServerUrl, target, options);
   }
   
   document.getElementById("tw-bookings-button").addEventListener("click", openTwBookingServer);
   function openTwBookingServer() {
    var ref = cordova.InAppBrowser.open(twBookingServerUrl, target, options);
   }
   
   document.getElementById("jenkins-button").addEventListener("click", openJenkins);
   function openJenkins() {
    var ref = cordova.InAppBrowser.open(jenkinsUrl, target, options);
   }
   
   const urlParams = new URLSearchParams(window.location.search);
   const mockCordova = urlParams.get('mockCordova');
   if (mockCordova) {
     console.log("Mocking cordova object");
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
 
/** Call main. */
$(document).ready(mainIndexMobileRoot);