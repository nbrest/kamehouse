var dragonBallUserServiceJsp;

window.onload = () => {
  moduleUtils.waitForModules(["debuggerHttpClient"], () => {
    dragonBallUserServiceJsp = new DragonBallUserServiceJsp();
    dragonBallUserServiceJsp.getDragonBallUser();
  });
}

