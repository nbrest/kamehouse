kameHouse.ready(() => {
  kameHouse.util.module.waitForModules(["crudManager"], () => {    
    kameHouse.plugin.crudManager.init({
      entityName: "Booking Schedule Config",
      entityNameJapanese: "予約スケジュールの構成",
      url: "/kame-house-tennisworld/api/v1/tennis-world/booking-schedule-configs",
      banner: "banner-ryoma-ss",
      icon: "/kame-house/img/prince-of-tennis/inui-data-tennis-icon.png",
      infoImage: {
        img: "/kame-house/img/prince-of-tennis/inui-data-tennis.jpg",
        title: "Schedule automatic bookings",
        desc: "Use your data tennis skills to automate your bookings",
        isReverse: false
      },
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
          values: [
          	"ADULT_MATCH_PLAY_SINGLES",
          	"ADULT_MATCH_PLAY_DOUBLES",
          	"ADULT_SOCIAL_PLAY",
          	"CARDIO",
          	"CARDIO_ACTIV8",
          	"NTC_CLAY_COURTS",
          	"NTC_INDOOR",
          	"NTC_OUTDOOR",
          	"ROD_LAVER_OUTDOOR_EASTERN",
          	"ROD_LAVER_OUTDOOR_WESTERN",
          	"ROD_LAVER_SHOW_COURTS"
          ],
          displayValues: [
          	"Adult Match Play Singles",
          	"Adult Match Play Doubles",
          	"Adult Social Play",
          	"Cardio Tennis",
          	"Cardio Activ8",
          	"NTC Clay Courts",
          	"NTC Indoor",
          	"NTC Outdoor",
          	"Rod Laver Outdoor Eastern",
          	"Rod Laver Outdoor Western",
          	"Rod Laver Show Courts"
          ]
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
        },
        { 
          name: "courtNumber",
          type: "number",
          min: 0,
          max: 40
        }
      ]
    });
  });
});
