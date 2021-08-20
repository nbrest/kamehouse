window.onload = () => {
  moduleUtils.waitForModules(["debuggerHttpClient", "crudManager"], () => {    
    crudManager.init({
      entityName: "DragonBall User",
      url: "/kame-house-testmodule/api/v1/test-module/dragonball/users"
    });
  });
};
