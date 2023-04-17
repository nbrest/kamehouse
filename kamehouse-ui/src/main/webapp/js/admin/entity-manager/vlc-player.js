window.onload = () => {
  kameHouse.util.module.waitForModules(["kameHouseDebugger", "crudManager"], () => {    
    crudManager.init({
      entityName: "Vlc Player",
      url: "/kame-house-vlcrc/api/v1/vlc-rc/players",
      columns: [
        { 
          name: "id",
          type: "id"
        },
        { 
          name: "hostname",
          type: "text"
        }, 
        { 
          name: "port",
          type: "number"
        }, 
        { 
          name: "username",
          type: "text"
        }, 
        { 
          name: "password",
          type: "password"
        }
      ]
    });
  });
};
