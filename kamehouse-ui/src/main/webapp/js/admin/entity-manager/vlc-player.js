window.onload = () => {
  moduleUtils.waitForModules(["debuggerHttpClient", "crudManager"], () => {    
    crudManager.init({
      entityName: "Vlc Player",
      url: "/kame-house-vlcrc/api/v1/vlc-rc/players"
    });
  });
};
