window.onload = () => {
  kameHouse.util.module.waitForModules(["kameHouseDebugger", "crudManager"], () => {    
    crudManager.init({
      readOnly: true,
      entityName: "DragonBall User",
      url: "/kame-house-testmodule/api/v1/test-module/dragonball/users",
      columns: [
        { 
          name: "id",
          type: "id"
        },
        { 
          name: "username",
          type: "text"
        }, 
        { 
          name: "email",
          type: "email"
        }, 
        { 
          name: "age",
          type: "number"
        }, 
        { 
          name: "powerLevel",
          type: "number"
        }, 
        { 
          name: "stamina",
          type: "number"
        }
      ]
    });
  });
};
