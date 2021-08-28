const config = {
  readOnly: true,
  entityName: "Booking Response",
  url: "/kame-house-tennisworld/api/v1/tennis-world/booking-responses",
  defaultSorting: {
    columnNumber: 11, //creationDate
    sortType: "timestamp",
    direction: "desc"
  },
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
          values: ["CARDIO", "NTC_CLAY_COURTS", "NTC_OUTDOOR", "ROD_LAVER_OUTDOOR", "ROD_LAVER_SHOW_COURTS"],
          displayValues: ["Cardio", "NTC Clay Courts", "NTC Outdoors", "Rod Laver Outdoors", "Rod Laver Show Courts"],
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
        }
      ]    
    }, 
  ],
};
var customListManager;

window.onload = () => {
  customListManager = new CustomListManager();
  moduleUtils.waitForModules(["debuggerHttpClient", "crudManager"], () => {    
    crudManager.init(config);
  });
}

/**
 * Handler for all the custom filters.
 */
function CustomListManager() {
  
  this.filterByStatus = filterByStatus;

  /**
   * Filter rows by status.
   */
  function filterByStatus() {    
    const filterString = document.getElementById('status-dropdown').value;
    tableUtils.filterTableRowsByColumn(filterString, 'crud-manager-tbody', 1);
  }
}
