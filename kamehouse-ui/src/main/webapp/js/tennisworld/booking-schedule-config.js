window.onload = () => {
  moduleUtils.waitForModules(["debuggerHttpClient", "crudManager"], () => {    
    crudManager.init({
      entityName: "Booking Schedule Config",
      url: "/kame-house-tennisworld/api/v1/tennis-world/booking-schedule-config",
      columns: [
        { 
          name: "id",
          type: "hidden"
        }, 
        {
          name: "tennisWorldUser",
          type: "object",
          columns: [
            {
              name: "id",
              type: "hidden"
            },
            {
              name: "email",
              type: "input"
            },
            {
              name: "password",
              type: "password"
            }
          ]
        }, 
        { 
          name: "sessionType",
          type: "input"
        }, 
        { 
          name: "site",
          type: "input"
        }, 
        { 
          name: "day",
          type: "input"
        }, 
        { 
          name: "time",
          type: "input"
        }, 
        { 
          name: "bookingDate",
          type: "input"
        }, 
        { 
          name: "bookAheadDays",
          type: "input"
        }, 
        { 
          name: "enabled",
          type: "input"
        }, 
        { 
          name: "duration",
          type: "input"
        }
      ]
    });
  });
};
