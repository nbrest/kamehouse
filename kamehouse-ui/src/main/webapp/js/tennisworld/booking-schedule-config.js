window.onload = () => {
  moduleUtils.waitForModules(["debuggerHttpClient", "crudManager"], () => {    
    crudManager.init({
      entityName: "Booking Schedule Config",
      url: "/kame-house-tennisworld/api/v1/tennis-world/booking-schedule-config"
    });
  });
};
