const config = {
  readOnly: true,
  entityName: "Booking Response",
  entityNameJapanese: "予約応答",
  url: "/kame-house-tennisworld/api/v1/tennis-world/booking-responses",
  banner: "banner-rikkaidai",
  icon: "/kame-house/img/prince-of-tennis/yukimura-icon.png",
  infoImage: {
    img: "/kame-house/img/banners/prince-of-tennis/banner-yukimura.jpg",
    title: "Game Set Match",
    desc: "Your bookings are completed. Get ready to fight in the tennis courts",
    isReverse: true
  },
  defaultSorting: {
    columnNumber: 11, //creationDate
    sortType: "timestamp",
    direction: "desc"
  },
  readAll: {
    maxRows: 500,
    sortColumn: "id",
    sortAscending: false,
  },
  reverseDataOrder: false,
  customListSection: "/kame-house/html-snippets/tennisworld/booking-responses-custom-list-div.html",
  columns: [
    { 
      name: "id",
      type: "id"
    }, 
    { 
      name: "status",
      type: "text"
    }, 
    { 
      name: "message",
      type: "text"
    }, 
    { 
      name: "request",
      type: "object",
      columns: [
        { 
          name: "id",
          type: "id"
        }, 
        { 
          name: "username",
          type: "text"
        }, 
        { 
          name: "date",
          type: "date"
        }, 
        { 
          name: "time",
          type: "time"
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
          ],
          sortType: "text"
        },
        { 
          name: "site",
          type: "select",
          values: ["MELBOURNE_PARK", "ALBERT_RESERVE"],
          displayValues: ["Melbourne Park", "Albert Reserve"]
        },  
        { 
          name: "duration",
          type: "select",
          values: ["0", "15", "30", "45", "60", "90", "120", "150", "180"],
          displayValues: ["0", "15", "30", "45", "60", "90", "120", "150", "180"],
          sortType: "number"
        },
        { 
          name: "dryRun",
          type: "boolean"
        },
        { 
          name: "creationDate",
          type: "timestamp"
        }, 
        { 
          name: "scheduled",
          type: "boolean"
        },
        { 
          name: "courtNumber",
          type: "number"
        }
      ]    
    }, 
  ],
};

kameHouse.ready(() => {
  kameHouse.util.module.waitForModules(["crudManager"], () => {    
    kameHouse.plugin.crudManager.init(config);
  });
});