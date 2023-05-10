$(document).ready(() => {
  kameHouse.util.banner.setRandomAllBanner();
  kameHouse.util.module.waitForModules(["dragonBallUserServiceJsp"], () => {
    kameHouse.extension.dragonBallUserServiceJsp.getDragonBallUser();
  });
});

