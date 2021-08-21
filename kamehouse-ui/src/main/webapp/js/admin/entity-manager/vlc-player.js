window.onload = () => {
  moduleUtils.waitForModules(["debuggerHttpClient", "crudManager"], () => {    
    crudManager.init({
      entityName: "Vlc Player",
      url: "/kame-house-vlcrc/api/v1/vlc-rc/players",
      columns: [
        { 
          name: "id",
          type: "hidden"
        },
        { 
          name: "hostname",
          type: "input"
        }, 
        { 
          name: "port",
          type: "input"
        }, 
        { 
          name: "username",
          type: "input"
        }, 
        { 
          name: "password",
          type: "password"
        }
      ]
    });
  });
};
