/**
 * Functionality to manage the dragonball users.
 */
function DragonBallUserServiceJsp() {
  let self = this;
  var REST_SERVICE_URI = '/kame-house-testmodule/api/v1/servlet/test-module/dragonball/users';

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
      deleteButton.text("Delete");
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
