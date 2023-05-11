$(document).ready(() => {
  kameHouse.util.module.waitForModules(["crudManager"], () => {    
    kameHouse.plugin.crudManager.init({
      entityName: "Vlc Player",
      url: "/kame-house-vlcrc/api/v1/vlc-rc/players",
      banner: "banner-pegasus-ryu-sei-ken",
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
});
