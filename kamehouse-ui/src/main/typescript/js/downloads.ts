kameHouse.ready(() => {
  kameHouse.util.banner.setRandomAllBanner(null);
  kameHouse.util.module.waitForModules(["slideshow"], () => {
    kameHouse.plugin.slideshow.setDotSlide(1);
  });
});