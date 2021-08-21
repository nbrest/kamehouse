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
          type: "select",
          values: ["CARDIO", "NTC_CLAY_COURTS", "NTC_OUTDOOR", "ROD_LAVER_OUTDOOR", "ROD_LAVER_SHOW_COURTS"],
          displayValues: ["Cardio", "NTC Clay Courts", "NTC Outdoors", "Rod Laver Outdoors", "Rod Laver Show Courts"]
        },
        { 
          name: "site",
          type: "select",
          values: ["MELBOURNE_PARK", "ALBERT_RESERVE"],
          displayValues: ["Melbourne Park", "Albert Reserve"]
        }, 
        { 
          name: "day",
          type: "select",
          values: ["SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"],
          displayValues: ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"]
        }, 
        { 
          name: "time",
          type: "time"
        }, 
        { 
          name: "bookingDate",
          type: "date"
        }, 
        { 
          name: "bookAheadDays",
          type: "number",
          min: 0,
          max: 90
        }, 
        { 
          name: "enabled",
          type: "boolean"
        }, 
        { 
          name: "duration",
          type: "select",
          values: ["0", "15", "30", "45", "60", "90", "120", "150", "180"],
          displayValues: ["0", "15", "30", "45", "60", "90", "120", "150", "180"]
        }
      ]
    });
  });
};
