kameHouse.ready(() => {
  kameHouse.util.banner.setRandomAllBanner(null);
  kameHouse.util.module.waitForModules(["dragonBallUserServiceJsp"], () => {
    kameHouse.extension.dragonBallUserServiceJsp.getAllDragonBallUsers();
  });
});

