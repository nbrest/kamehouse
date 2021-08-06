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

    httpClient.get(getUrl, null,
      (responseBody, responseCode, responseDescription) => self.displayDragonBallUserToEdit(responseBody),
      (responseBody, responseCode, responseDescription) => {
        alert("Error getting dragonball user");
        logger.error("Error getting dragonball user " + responseBody + responseCode + responseDescription);
      });
  }

  /**
   * Get all dragonball users.
   */
  this.getAllDragonBallUsers = () => {
    logger.trace(arguments.callee.name);
    httpClient.get(SERVLET_SERVICE_URI, null,
      (responseBody, responseCode, responseDescription) => self.displayDragonBallUsers(responseBody),
      (responseBody, responseCode, responseDescription) => self.displayErrorGettingDragonBallUsers());
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
    let postUrl = SERVLET_SERVICE_URI + "?" + params;

    httpClient.post(postUrl, httpClient.getUrlEncodedHeaders(), null,
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
    let putUrl = SERVLET_SERVICE_URI + "?" + params;

    httpClient.put(putUrl, httpClient.getUrlEncodedHeaders(), null,
      (responseBody, responseCode, responseDescription) => {window.location.href = 'users-list'},
      (responseBody, responseCode, responseDescription) => {
        alert("Error updating dragonball user. Check console logs for more details");
        logger.error("Error updating dragonball user " + responseBody + responseCode + responseDescription);
      });
  }

  /**
  * Delete dragonball user.
  */
  this.deleteDragonBallUser = (event) => {
    logger.trace(arguments.callee.name);
    const params = new URLSearchParams({
      id: event.data.id
    });
    let deleteUrl = SERVLET_SERVICE_URI + "?" + params;

    httpClient.delete(deleteUrl, httpClient.getUrlEncodedHeaders(), null,
      (responseBody, responseCode, responseDescription) => self.getAllDragonBallUsers(),
      (responseBody, responseCode, responseDescription) => self.getAllDragonBallUsers());
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
    let editButton = $('<input>');
    editButton.attr("type", "button");
    editButton.attr("value", "edit");
    editButton.addClass("btn btn-outline-success btn-borderless");
    editButton.click(() => {
      window.location.href="users-edit?username=" + username;
    });
    return editButton;
  }

  this.getDeleteButton = (id) => {
    let deleteButton = $('<button>');
    deleteButton.addClass("btn btn-outline-danger btn-borderless")
    deleteButton.text("delete");
    deleteButton.click({
      id: id
    }, self.deleteDragonBallUser);
    return deleteButton;
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
