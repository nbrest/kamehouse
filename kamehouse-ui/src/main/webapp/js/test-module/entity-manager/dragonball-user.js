$(document).ready(() => {
  kameHouse.util.module.waitForModules(["crudManager"], () => {    
    kameHouse.plugin.crudManager.init({
      readOnly: true,
      entityName: "DragonBall User",
      url: "/kame-house-testmodule/api/v1/test-module/dragonball/users",
      banner: "banner-goku-ssj1",
      icon: "/kame-house/img/dbz/gohan-ssj2-icon.png",
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
});
