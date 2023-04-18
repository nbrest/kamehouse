$(document).ready(() => {
  kameHouse.util.banner.setRandomAllBanner();
  kameHouse.util.mobile.generateAndroidQrCode("android-app-qrcode");
  kameHouse.util.module.waitForModules(["slideshow"], () => {
    kameHouse.plugin.slideshow.setDotSlide(1);
  });
});