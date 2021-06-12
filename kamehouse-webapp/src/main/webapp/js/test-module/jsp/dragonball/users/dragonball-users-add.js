var dragonBallUserServiceJsp;

window.onload = () => {
  moduleUtils.waitForModules(["logger", "httpClient"], () => {
    dragonBallUserServiceJsp = new DragonBallUserServiceJsp();
  });
}

