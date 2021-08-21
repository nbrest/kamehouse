window.onload = () => {
  moduleUtils.waitForModules(["debuggerHttpClient", "crudManager"], () => {    
    crudManager.init({
      entityName: "DragonBall User",
      url: "/kame-house-testmodule/api/v1/test-module/dragonball/users",
      columns: [
        { 
          name: "id",
          type: "hidden"
        },
        { 
          name: "username",
          type: "input"
        }, 
        { 
          name: "email",
          type: "input"
        }, 
        { 
          name: "age",
          type: "input"
        }, 
        { 
          name: "powerLevel",
          type: "input"
        }, 
        { 
          name: "stamina",
          type: "input"
        }
      ]
    });
  });
};
