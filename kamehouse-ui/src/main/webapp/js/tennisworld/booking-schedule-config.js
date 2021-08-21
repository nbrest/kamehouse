window.onload = () => {
  moduleUtils.waitForModules(["debuggerHttpClient", "crudManager"], () => {    
    crudManager.init({
      entityName: "Booking Schedule Config",
      url: "/kame-house-tennisworld/api/v1/tennis-world/booking-schedule-config",
      columns: [
        { 
          name: "id",
          type: "id"
        }, 
        {
          name: "tennisWorldUser",
          type: "object",
          columns: [
            {
              name: "id",
              type: "id"
            },
            {
              name: "email",
              type: "email"
            },
            {
              name: "password",
              type: "hidden"
            }
          ]
        }, 
        { 
          name: "sessionType",
          type: "text"
        }, 
        { 
          name: "site",
          type: "text"
        }, 
        { 
          name: "day",
          type: "text"
        }, 
        { 
          name: "time",
          type: "text"
        }, 
        { 
          name: "bookingDate",
          type: "date"
        }, 
        { 
          name: "bookAheadDays",
          type: "number"
        }, 
        { 
          name: "enabled",
          type: "boolean"
        }, 
        { 
          name: "duration",
          type: "number"
        }
      ]
    });
  });
};
