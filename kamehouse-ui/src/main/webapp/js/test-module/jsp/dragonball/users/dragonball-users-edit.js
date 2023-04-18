window.onload = () => {
  kameHouse.util.module.waitForModules(["dragonBallUserServiceJsp"], () => {
    kameHouse.extension.dragonBallUserServiceJsp.getDragonBallUser();
  });
}

