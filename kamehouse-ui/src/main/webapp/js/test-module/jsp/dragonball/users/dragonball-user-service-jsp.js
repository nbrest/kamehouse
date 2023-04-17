/**
 * Functionality to manage the dragonball users in the UI through the jsps and servconst api.
 */
function DragonBallUserServiceJsp() {

  this.getDragonBallUser = getDragonBallUser;
  this.getAllDragonBallUsers = getAllDragonBallUsers;
  this.addDragonBallUser = addDragonBallUser;
  this.updateDragonBallUser = updateDragonBallUser;
  this.deleteDragonBallUser = deleteDragonBallUser;

  const SERVLET_SERVICE_URI = '/kame-house-testmodule/api/v1/servlet/test-module/dragonball/users';

  /**
   * Get a dragonball user and populate it to the edit table.
   */
  function getDragonBallUser(event) {
    const urlParams = new URLSearchParams(window.location.search);
    const params = new URLSearchParams({
      username: urlParams.get('username')
    });
    const getUrl = SERVLET_SERVICE_URI + "?" + params;

    kameHouse.plugin.debugger.http.get(getUrl, 
      (responseBody, responseCode, responseDescription) => displayDragonBallUserToEdit(responseBody),
      (responseBody, responseCode, responseDescription) => {
        let errorMessage = 'Error getting dragonball user';
        handleApiErrorResponse(errorMessage, responseBody, responseCode, responseDescription);
      }, null);
  }

  /**
   * Get all dragonball users.
   */
  function getAllDragonBallUsers() {
    kameHouse.plugin.debugger.http.get(SERVLET_SERVICE_URI, 
      (responseBody, responseCode, responseDescription) => displayDragonBallUsers(responseBody),
      (responseBody, responseCode, responseDescription) => {
        const errorMessage = 'Error getting dragonball users from the backend';
        displayErrorTable(errorMessage);
        handleApiErrorResponse(errorMessage, responseBody, responseCode, responseDescription);
      },
      null);
  }

  /**
   * Add a dragonball user.
   */
  function addDragonBallUser() {
    const params = new URLSearchParams({
      username: document.getElementById("input-username").value,
      email: document.getElementById("input-email").value,
      age: document.getElementById("input-age").value,
      powerLevel: document.getElementById("input-powerLevel").value,
      stamina: document.getElementById("input-stamina").value
    });

    kameHouse.plugin.debugger.http.postUrlEncoded(SERVLET_SERVICE_URI, params,
      (responseBody, responseCode, responseDescription) => { window.location.href = 'users-list'; },
      (responseBody, responseCode, responseDescription) => {
        const errorMessage = 'Error adding dragonball user';
        handleApiErrorResponse(errorMessage, responseBody, responseCode, responseDescription);
      });
  }

  /**
   * Update a dragonball user.
   */
  function updateDragonBallUser() {
    const params = new URLSearchParams({
      id: document.getElementById("input-id").value,
      username: document.getElementById("input-username").value,
      email: document.getElementById("input-email").value,
      age: document.getElementById("input-age").value,
      powerLevel: document.getElementById("input-powerLevel").value,
      stamina: document.getElementById("input-stamina").value
    });

    kameHouse.plugin.debugger.http.putUrlEncoded(SERVLET_SERVICE_URI, params,
      (responseBody, responseCode, responseDescription) => {window.location.href = 'users-list'},
      (responseBody, responseCode, responseDescription) => {
        const errorMessage = 'Error updating dragonball user';
        handleApiErrorResponse(errorMessage, responseBody, responseCode, responseDescription);
      }, null);
  }

  /**
  * Delete dragonball user.
  */
  function deleteDragonBallUser(id) {
    const params = new URLSearchParams({
      id: id
    });

    kameHouse.plugin.debugger.http.deleteUrlEncoded(SERVLET_SERVICE_URI, params,
      (responseBody, responseCode, responseDescription) => getAllDragonBallUsers(),
      (responseBody, responseCode, responseDescription) => {
        const errorMessage = 'Error deleting dragonball user';
        displayErrorTable(errorMessage);
        handleApiErrorResponse(errorMessage, responseBody, responseCode, responseDescription);
      }, 
      null);
  }
  
  /** Display api error */
  function handleApiErrorResponse(errorMessage, responseBody, responseCode, responseDescription) {
    if (!kameHouse.core.isEmpty(responseBody)) {
      try {
        errorMessage = errorMessage + " : " + JSON.parse(responseBody).message;
      } catch (e) {
        kameHouse.logger.error("Error parsing response body");
      } 
    }
    kameHouse.plugin.modal.basicModal.open(errorMessage);
    kameHouse.logger.logApiError(responseBody, responseCode, responseDescription, errorMessage);
  }

  /**
   * Display the dragonball user to edit.
   */
  function displayDragonBallUserToEdit(dragonBallUser) {
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
  async function displayDragonBallUsers(dragonBallUsersList) {
    const $dragonBallUsersTbody = $('#dragonball-users-tbody');
    kameHouse.util.dom.empty($dragonBallUsersTbody);
    kameHouse.util.dom.append($dragonBallUsersTbody, await getDragonBallUserTableHeader());
    for (const dragonballUser of dragonBallUsersList) {
      kameHouse.util.dom.append($dragonBallUsersTbody, getDragonBallUserTableRow(dragonballUser));
    }
  }

  /**
   * Shows the specified error message in the table.
   */
  function displayErrorTable(message) {
    const $dragonBallUsersTbody = $('#dragonball-users-tbody');
    kameHouse.util.dom.empty($dragonBallUsersTbody);
    kameHouse.util.dom.append($dragonBallUsersTbody, getErrorMessageTr(message));
  }
  
  function getErrorMessageTr(message) {
    return kameHouse.util.dom.getTrTd(message);
  }

  function getDragonBallUserTableRow(dragonBallUser) {
    const tr = kameHouse.util.dom.getTr({}, null);
    kameHouse.util.dom.append(tr, getDragonBallUserTd(dragonBallUser.id));
    kameHouse.util.dom.append(tr, getDragonBallUserTd(dragonBallUser.username));
    kameHouse.util.dom.append(tr, getDragonBallUserTd(dragonBallUser.email));
    kameHouse.util.dom.append(tr, getDragonBallUserTd(dragonBallUser.age));
    kameHouse.util.dom.append(tr, getDragonBallUserTd(dragonBallUser.powerLevel));
    kameHouse.util.dom.append(tr, getDragonBallUserTd(dragonBallUser.stamina));
    kameHouse.util.dom.append(tr, getActionButtonsTd(dragonBallUser.username, dragonBallUser.id));
    return tr;
  }

  function getDragonBallUserTd(dataValue) {
    return kameHouse.util.dom.getTd({}, dataValue);
  }

  function getActionButtonsTd(username, id) {
    const td = kameHouse.util.dom.getTd({}, null);
    kameHouse.util.dom.append(td, getEditButton(username));
    kameHouse.util.dom.append(td, getDeleteButton(id));
    return td; 
  }

  function getEditButton(username) {
    return kameHouse.util.dom.getImgBtn({
      src: "/kame-house/img/other/edit-green.png",
      className: "img-btn-kh m-15-d-r-kh",
      alt: "Edit",
      onClick: () => window.location.href="users-edit?username=" + username
    });
  }

  function getDeleteButton(id) {
    return kameHouse.util.dom.getImgBtn({
      src: "/kame-house/img/other/delete-red.png",
      className: "img-btn-kh",
      alt: "Delete",
      onClick: () => deleteDragonBallUser(id)
    });
  }

  function getDragonBallUserTableHeader() {
    return kameHouse.util.fetch.loadHtmlSnippet("/kame-house/html-snippets/test-module/dragonball-users-table-header.html");
  }
}
