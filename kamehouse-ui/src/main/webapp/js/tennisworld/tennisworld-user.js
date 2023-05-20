$(document).ready(() => {
  kameHouse.util.module.waitForModules(["crudManager"], () => {    
    kameHouse.plugin.crudManager.init({
      entityName: "TennisWorld User",
      url: "/kame-house-tennisworld/api/v1/tennis-world/users",
      banner: "banner-fuji",
      icon: "/kame-house/img/prince-of-tennis/fuji-icon.png",
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
