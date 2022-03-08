/**
 * Mobile app link page startup function. Generate QR code.
 */
function mainMobileLinks() {
  generateAndroidQrCode();
}

function generateAndroidQrCode() {
  $(".android-app-qrcode").qrcode({
    text:'https://kame.nicobrest.com/kame-house-mobile/kamehouse.apk',
    ecLevel:'L',
    size: 256
  });
}

$(document).ready(mainMobileLinks);