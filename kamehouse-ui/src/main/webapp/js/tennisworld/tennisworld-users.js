window.onload = () => {
  moduleUtils.waitForModules(["debuggerHttpClient", "crudManager"], () => {    
    crudManager.init({
      entityName: "TennisWorld User",
      url: "/kame-house-tennisworld/api/v1/tennis-world/users"
    });
  });
};
