window.onload = () => {
  moduleUtils.waitForModules(["debuggerHttpClient", "crudManager"], () => {    
    crudManager.init({
      entityName: "KameHouse User",
      url: "/kame-house-admin/api/v1/admin/kamehouse/users"
    });
  });
};
