/**
 * Functionality to manage the dragonball users in the UI through the jsps and servlet api.
 */
function DragonBallUserServiceJsp() {
  let self = this;
  var SERVLET_SERVICE_URI = '/kame-house-testmodule/api/v1/servlet/test-module/dragonball/users';

  /**
   * Get a dragonball user and populate it to the edit table.
   */
  this.getDragonBallUser = (event) => {
    logger.trace(arguments.callee.name);
    const urlParams = new URLSearchParams(window.location.search);
    const params = new URLSearchParams({
      username: urlParams.get('username')
    });
    let getUrl = SERVLET_SERVICE_URI + "?" + params;

    debuggerHttpClient.get(getUrl, 
      (responseBody, responseCode, responseDescription) => self.displayDragonBallUserToEdit(responseBody),
      (responseBody, responseCode, responseDescription) => {
        alert("Error getting dragonball user");
        logger.error("Error getting dragonball user " + responseBody + responseCode + responseDescription);
      }, null);
  }

  /**
   * Get all dragonball users.
   */
  this.getAllDragonBallUsers = () => {
    logger.trace(arguments.callee.name);
    debuggerHttpClient.get(SERVLET_SERVICE_URI, 
      (responseBody, responseCode, responseDescription) => self.displayDragonBallUsers(responseBody),
      (responseBody, responseCode, responseDescription) => self.displayErrorGettingDragonBallUsers(),
      null);
  }

  /**
   * Add a dragonball user.
   */
  this.addDragonBallUser = () => {
    logger.trace(arguments.callee.name);
    const params = new URLSearchParams({
      username: document.getElementById("input-username").value,
      email: document.getElementById("input-email").value,
      age: document.getElementById("input-age").value,
      powerLevel: document.getElementById("input-powerLevel").value,
      stamina: document.getElementById("input-stamina").value
    });

    debuggerHttpClient.postUrlEncoded(SERVLET_SERVICE_URI, params,
      (responseBody, responseCode, responseDescription) => {window.location.href = 'users-list'},
      (responseBody, responseCode, responseDescription) => {
        alert("Error adding dragonball user. Check console logs for more details");
        logger.error("Error adding dragonball user " + responseBody + responseCode + responseDescription);
      });
  }

  /**
   * Update a dragonball user.
   */
  this.updateDragonBallUser = () => {
    logger.trace(arguments.callee.name);
    const params = new URLSearchParams({
      id: document.getElementById("input-id").value,
      username: document.getElementById("input-username").value,
      email: document.getElementById("input-email").value,
      age: document.getElementById("input-age").value,
      powerLevel: document.getElementById("input-powerLevel").value,
      stamina: document.getElementById("input-stamina").value
    });

    debuggerHttpClient.putUrlEncoded(SERVLET_SERVICE_URI, params,
      (responseBody, responseCode, responseDescription) => {window.location.href = 'users-list'},
      (responseBody, responseCode, responseDescription) => {
        alert("Error updating dragonball user. Check console logs for more details");
        logger.error("Error updating dragonball user " + responseBody + responseCode + responseDescription);
      }, null);
  }

  /**
  * Delete dragonball user.
  */
  this.deleteDragonBallUser = (id) => {
    logger.trace(arguments.callee.name);
    const params = new URLSearchParams({
      id: id
    });

    debuggerHttpClient.deleteUrlEncoded(SERVLET_SERVICE_URI, params,
      (responseBody, responseCode, responseDescription) => self.getAllDragonBallUsers(),
      (responseBody, responseCode, responseDescription) => self.getAllDragonBallUsers(), 
      null);
  }

  /**
   * Display the dragonball user to edit.
   */
  this.displayDragonBallUserToEdit = (dragonBallUser) => {
    logger.trace(arguments.callee.name);
    document.getElementById("input-id").value = dragonBallUser.id;
    document.getElementById("input-username").value = dragonBallUser.username;
    document.getElementById("input-email").value = dragonBallUser.email;
    document.getElementById("input-age").value = dragonBallUser.age;
    document.getElementById("input-powerLevel").value = dragonBallUser.powerLevel;
    document.getElementById("input-stamina").value = dragonBallUser.stamina;
  }

  /**
   * Display dragonball users table.
   */
  this.displayDragonBallUsers = (dragonBallUsersList) => {
    logger.trace(arguments.callee.name);
    let $dragonBallUsersTbody = $('#dragonball-users-tbody');
    $dragonBallUsersTbody.empty();
    $dragonBallUsersTbody.append(self.getDragonBallUserTableHeader());
    for (let i = 0; i < dragonBallUsersList.length; i++) {
      $dragonBallUsersTbody.append(self.getDragonBallUserTableRow(dragonBallUsersList[i]));
    }
  }

  /**
   * Display dragonball users.
   */
  this.displayErrorGettingDragonBallUsers = () => {
    self.displayErrorTable("Error getting dragonball users from the backend");
  }

  /**
   * Display dragonball users.
   */
  this.displayErrorDeletingDragonBallUser = () => {
    self.displayErrorTable("Error deleting dragonball user from the backend");
  }

  /**
   * Shows the specified error message in the table.
   */
  this.displayErrorTable = (message) => {
    logger.trace(arguments.callee.name);
    let $dragonBallUsersTbody = $('#dragonball-users-tbody');
    $dragonBallUsersTbody.empty();
    $dragonBallUsersTbody.append(self.getErrorMessageTableRow(message));
  }
  
  /** Dynamic DOM element generation ------------------------------------------ */
  this.getErrorMessageTableRow = () => {
    let tableRow = $('<tr>');
    let tableRowData = $('<td>');
    tableRowData.text(message);
    tableRow.append(tableRowData);
    return tableRow;
  }

  this.getDragonBallUserTableRow = (dragonBallUser) => {
    let tableRow = $('<tr>');
    tableRow.append(self.getDragonBallUserTableRowData(dragonBallUser.id));
    tableRow.append(self.getDragonBallUserTableRowData(dragonBallUser.username));
    tableRow.append(self.getDragonBallUserTableRowData(dragonBallUser.email));
    tableRow.append(self.getDragonBallUserTableRowData(dragonBallUser.age));
    tableRow.append(self.getDragonBallUserTableRowData(dragonBallUser.powerLevel));
    tableRow.append(self.getDragonBallUserTableRowData(dragonBallUser.stamina));
    tableRow.append(self.getActionButtonsTableRowData(dragonBallUser.username, dragonBallUser.id));
    return tableRow;
  }

  this.getDragonBallUserTableRowData = (dataValue) => {
    let tableRowData = $('<td>');
    tableRowData.text(dataValue);
    return tableRowData; 
  }

  this.getActionButtonsTableRowData = (username, id) => {
    let tableRowData = $('<td>');
    tableRowData.append(self.getEditButton(username));
    tableRowData.append(self.getDeleteButton(id));
    return tableRowData; 
  }

  this.getEditButton = (username) => {
    let img = new Image();
    img.src = "/kame-house/img/other/edit-green.png";
    img.className = "img-btn-kh m-15-d-r-kh";
    img.alt = "Edit";
    img.title = "Edit";
    img.onclick = () =>  {
      window.location.href="users-edit?username=" + username;
    }
    return img;
  }

  this.getDeleteButton = (id) => {
    let img = new Image();
    img.src = "/kame-house/img/other/delete-red.png";
    img.className = "img-btn-kh";
    img.alt = "Delete";
    img.title = "Delete";
    img.onclick = () =>  {
      self.deleteDragonBallUser(id);
    }
    return img;
  }

  this.getDragonBallUserTableHeader = () => {
    let tableRow = $('<tr>');
    tableRow.attr("class", "table-db-users-header");

    let headerColumns = ["Id", "Name", "Email", "Age", "Power Level", "Stamina"];
    for (let i = 0; i < headerColumns.length; i++) {
      let headerColumnRowData = $('<td>')
      headerColumnRowData.text(headerColumns[i]);
      tableRow.append(headerColumnRowData);
    }
    
    let actionsRowData = $('<td>')
    actionsRowData.text("Actions")
    actionsRowData.attr("class", "table-db-users-actions");
    tableRow.append(actionsRowData);

    return tableRow;
  }
}
