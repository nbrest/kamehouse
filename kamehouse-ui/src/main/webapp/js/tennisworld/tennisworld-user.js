$(document).ready(() => {
  kameHouse.util.module.waitForModules(["kameHouseDebugger", "crudManager"], () => {    
    kameHouse.plugin.crudManager.init({
      entityName: "TennisWorld User",
      url: "/kame-house-tennisworld/api/v1/tennis-world/users",
      banner: "banner-fuji",
      columns: [
        { 
          name: "id",
          type: "id"
        },
        { 
          name: "email",
          type: "email"
        }, 
        { 
          name: "password",
          type: "password"
        }
      ]
    });
  });
});
