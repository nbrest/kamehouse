var dragonBallUserServiceJsp;

window.onload = () => {
  moduleUtils.waitForModules(["logger", "debuggerHttpClient"], () => {
    dragonBallUserServiceJsp = new DragonBallUserServiceJsp();
    dragonBallUserServiceJsp.getAllDragonBallUsers();
  });
}

