var dragonBallUserServiceJsp;

window.onload = () => {
  kameHouse.util.module.waitForModules(["kameHouseDebugger"], () => {
    dragonBallUserServiceJsp = new DragonBallUserServiceJsp();
    dragonBallUserServiceJsp.getAllDragonBallUsers();
  });
}

