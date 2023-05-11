$(document).ready(() => {
  kameHouse.util.module.waitForModules(["crudManager"], () => {    
    kameHouse.plugin.crudManager.init({
      entityName: "KameHouse User",
      url: "/kame-house-admin/api/v1/admin/kamehouse/users",
      banner: "banner-gohan-ssj2-3",
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
          name: "password",
          type: "password"
        }, 
        { 
          name: "email",
          type: "email"
        }, 
        { 
          name: "firstName",
          type: "text"
        }, 
        { 
          name: "lastName",
          type: "text"
        }, 
        { 
          name: "lastLogin",
          type: "date"
        }, 
        { 
          name: "authorities",
          type: "array",
          arrayType: "object"
        },
        { 
          name: "accountNonExpired",
          type: "boolean"
        }, 
        { 
          name: "accountNonLocked",
          type: "boolean"
        }, 
        { 
          name: "credentialsNonExpired",
          type: "boolean"
        }, 
        { 
          name: "enabled",
          type: "boolean"
        }
      ]
    });
  });
});
