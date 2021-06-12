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
    logger.traceFunctionCall();
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
    logger.traceFunctionCall();
    httpClient.get(SERVLET_SERVICE_URI, null,
      (responseBody, responseCode, responseDescription) => self.displayDragonBallUsers(responseBody),
      (responseBody, responseCode, responseDescription) => self.displayErrorGettingDragonBallUsers());
  }

  /**
   * Add a dragonball user.
   */
  this.addDragonBallUser = () => {
    logger.traceFunctionCall();
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
    logger.traceFunctionCall();
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
    logger.traceFunctionCall();
    const params = new URLSearchParams({
      id: event.data.id
    });
    let deleteUrl = SERVLET_SERVICE_URI + "?" + params;

    httpClient.delete(deleteUrl, httpClient.getUrlEncodedHeaders(),
      (responseBody, responseCode, responseDescription) => self.getAllDragonBallUsers(),
      (responseBody, responseCode, responseDescription) => self.getAllDragonBallUsers());
  }

  /**
   * Display the dragonball user to edit.
   */
  this.displayDragonBallUserToEdit = (dragonBallUser) => {
    logger.traceFunctionCall();
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
    logger.traceFunctionCall();
    let $dragonBallUsersTbody = $('#dragonball-users-tbody');
    $dragonBallUsersTbody.empty();
    for (let i = 0; i < dragonBallUsersList.length; i++) {
      let tableRow = $('<tr>');
      tableRow.append($('<td>').text(dragonBallUsersList[i].id));
      tableRow.append($('<td>').text(dragonBallUsersList[i].username));
      tableRow.append($('<td>').text(dragonBallUsersList[i].email));
      tableRow.append($('<td>').text(dragonBallUsersList[i].age));
      tableRow.append($('<td>').text(dragonBallUsersList[i].powerLevel));
      tableRow.append($('<td>').text(dragonBallUsersList[i].stamina));
      let editButton = '<input type="button" value="edit" class="btn btn-outline-success btn-borderless" onclick="window.location.href=' + "'users-edit?username=" + dragonBallUsersList[i].username + "'" + '">';
      let deleteButton = $('<button class="btn btn-outline-danger btn-borderless">');
      deleteButton.text("delete");
      deleteButton.click({
        id: dragonBallUsersList[i].id
      }, self.deleteDragonBallUser);
      tableRow.append($('<td>').html(editButton));
      tableRow.append($('<td>').html(deleteButton));
      $dragonBallUsersTbody.append(tableRow);
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
    logger.traceFunctionCall();
    let $dragonBallUsersTbody = $('#dragonball-users-tbody');
    $dragonBallUsersTbody.empty();
    let tableRow = $('<tr>').append($('<td>').text(message));
    $dragonBallUsersTbody.append(tableRow);
  }
}
