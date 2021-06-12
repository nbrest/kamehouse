/**
 * Functionality to manage the dragonball users.
 */
function DragonBallUserServiceJsp() {
  let self = this;
  var REST_SERVICE_URI = '/kame-house-testmodule/api/v1/servlet/test-module/dragonball/users';

  /**
   * Get a dragonball user and populate it to the edit table.
   */
  this.getDragonBallUser = (event) => {
    logger.traceFunctionCall();
    const urlParams = new URLSearchParams(window.location.search);
    const username = urlParams.get('username');
    httpClient.get(REST_SERVICE_URI + "?username=" + username, null,
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
    httpClient.get(REST_SERVICE_URI, null,
      (responseBody, responseCode, responseDescription) => self.displayDragonBallUsers(responseBody),
      (responseBody, responseCode, responseDescription) => self.displayErrorGettingDragonBallUsers());
  }

  /**
   * Add a dragonball user.
   */
  this.addDragonBallUser = () => {
    logger.traceFunctionCall();
    let postUrl = REST_SERVICE_URI + "?";
    let usernameValue = document.getElementById("input-username").value;
    let usernameParam = "username=" + usernameValue  + "&";
    postUrl = postUrl + usernameParam;
    let emailValue = document.getElementById("input-email").value;
    let emailParam = "email=" + emailValue + "&";
    postUrl = postUrl + emailParam;
    let ageValue = document.getElementById("input-age").value;
    let ageParam = "age=" + ageValue + "&";
    postUrl = postUrl + ageParam;
    let powerLevelValue = document.getElementById("input-powerLevel").value;
    let powerLevelParam = "powerLevel=" + powerLevelValue + "&";
    postUrl = postUrl + powerLevelParam;
    let staminaValue = document.getElementById("input-stamina").value;
    let staminaParam = "stamina=" + staminaValue;
    postUrl = postUrl + staminaParam;

    httpClient.post(postUrl, httpClient.getUrlEncodedHeaders(), null,
      (responseBody, responseCode, responseDescription) => {window.location.href = 'users-list'},
      (responseBody, responseCode, responseDescription) => {
        alert("Error adding dragonball user");
        logger.error("Error adding dragonball user " + responseBody + responseCode + responseDescription);
      });
  }

  /**
   * Update a dragonball user.
   */
  this.updateDragonBallUser = () => {
    logger.traceFunctionCall();
    let postUrl = REST_SERVICE_URI + "?";
    let idValue = document.getElementById("input-id").value;
    let idParam = "id=" + idValue + "&";
    postUrl = postUrl + idParam;
    let usernameValue = document.getElementById("input-username").value;
    let usernameParam = "username=" + usernameValue  + "&";
    postUrl = postUrl + usernameParam;
    let emailValue = document.getElementById("input-email").value;
    let emailParam = "email=" + emailValue + "&";
    postUrl = postUrl + emailParam;
    let ageValue = document.getElementById("input-age").value;
    let ageParam = "age=" + ageValue + "&";
    postUrl = postUrl + ageParam;
    let powerLevelValue = document.getElementById("input-powerLevel").value;
    let powerLevelParam = "powerLevel=" + powerLevelValue + "&";
    postUrl = postUrl + powerLevelParam;
    let staminaValue = document.getElementById("input-stamina").value;
    let staminaParam = "stamina=" + staminaValue;
    postUrl = postUrl + staminaParam;

    httpClient.put(postUrl, httpClient.getUrlEncodedHeaders(), null,
      (responseBody, responseCode, responseDescription) => {window.location.href = 'users-list'},
      (responseBody, responseCode, responseDescription) => {
        alert("Error updating dragonball user");
        logger.error("Error updating dragonball user " + responseBody + responseCode + responseDescription);
      });
  }

  /**
  * Delete dragonball user.
  */
  this.deleteDragonBallUser = (event) => {
    logger.traceFunctionCall();
    let id = event.data.id;
    httpClient.delete(REST_SERVICE_URI + "?id=" + id, httpClient.getUrlEncodedHeaders(),
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
   * Display dragonball users.
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
    logger.traceFunctionCall();
    let $dragonBallUsersTbody = $('#dragonball-users-tbody');
    let tableRow = $('<tr>').append($('<td>').text("Error getting dragonball users from the backend"));
    $dragonBallUsersTbody.append(tableRow);
  }

  /**
   * Display dragonball users.
   */
  this.displayErrorDeletingDragonBallUser = () => {
    logger.traceFunctionCall();
    let $dragonBallUsersTbody = $('#dragonball-users-tbody');
    $dragonBallUsersTbody.empty();
    let tableRow = $('<tr>').append($('<td>').text("Error deleting dragonball user from the backend"));
    $dragonBallUsersTbody.append(tableRow);
  }
}
