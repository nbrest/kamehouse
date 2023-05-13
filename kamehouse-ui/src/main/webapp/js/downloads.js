$(document).ready(() => {
  kameHouse.util.banner.setRandomAllBanner();
  kameHouse.util.module.waitForModules(["slideshow"], () => {
    kameHouse.plugin.slideshow.setDotSlide(1);
  });
});