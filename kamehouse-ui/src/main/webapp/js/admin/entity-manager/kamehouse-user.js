window.onload = () => {
  moduleUtils.waitForModules(["debuggerHttpClient", "crudManager"], () => {    
    crudManager.init({
      entityName: "KameHouse User",
      url: "/kame-house-admin/api/v1/admin/kamehouse/users",
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
          name: "password",
          type: "input"
        }, 
        { 
          name: "email",
          type: "input"
        }, 
        { 
          name: "firstName",
          type: "input"
        }, 
        { 
          name: "lastName",
          type: "input"
        }, 
        { 
          name: "lastLogin",
          type: "input"
        }, 
        { 
          name: "authorities",
          type: "input"
        }, 
        { 
          name: "accountNonExpired",
          type: "input"
        }, 
        { 
          name: "accountNonLocked",
          type: "input"
        }, 
        { 
          name: "credentialsNonExpired",
          type: "input"
        }, 
        { 
          name: "enabled",
          type: "input"
        }
      ]
    });
  });
};
