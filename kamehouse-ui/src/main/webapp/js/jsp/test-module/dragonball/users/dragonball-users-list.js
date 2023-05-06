$(document).ready(() => {
  kameHouse.util.module.waitForModules(["dragonBallUserServiceJsp"], () => {
    kameHouse.extension.dragonBallUserServiceJsp.getAllDragonBallUsers();
  });
});

