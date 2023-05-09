/**
 * Functionality to manage the dragonball users in the UI through the jsps and servconst api.
 */
function DragonBallUserServiceJsp() {

  this.load = load;
  this.getDragonBallUser = getDragonBallUser;
  this.getAllDragonBallUsers = getAllDragonBallUsers;
  this.addDragonBallUser = addDragonBallUser;
  this.updateDragonBallUser = updateDragonBallUser;
  this.deleteDragonBallUser = deleteDragonBallUser;

  const SERVLET_SERVICE_URI = '/kame-house-testmodule/api/v1/servlet/test-module/dragonball/users';

  function load() {
    kameHouse.logger.info("Loading DragonBallUserServiceJsp");
    kameHouse.util.module.waitForModules(["kameHouseDebugger"], () => {
      kameHouse.util.module.setModuleLoaded("dragonBallUserServiceJsp");
    });
  }

  /**
   * Get a dragonball user and populate it to the edit table.
   */
  function getDragonBallUser(event) {
    const urlParams = new URLSearchParams(window.location.search);
    const params ={
      "username" : urlParams.get('username')
    };
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.get(config, SERVLET_SERVICE_URI, kameHouse.http.getUrlEncodedHeaders(), params, 
      (responseBody, responseCode, responseDescription, responseHeaders) => displayDragonBallUserToEdit(responseBody),
      (responseBody, responseCode, responseDescription, responseHeaders) => {
        let errorMessage = 'Error getting dragonball user';
        handleApiErrorResponse(errorMessage, responseBody, responseCode, responseDescription, responseHeaders);
      });
  }

  /**
   * Get all dragonball users.
   */
  function getAllDragonBallUsers() {
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.get(config, SERVLET_SERVICE_URI, null, null, 
      (responseBody, responseCode, responseDescription, responseHeaders) => displayDragonBallUsers(responseBody),
      (responseBody, responseCode, responseDescription, responseHeaders) => {
        const errorMessage = 'Error getting dragonball users from the backend';
        displayErrorTable(errorMessage);
        handleApiErrorResponse(errorMessage, responseBody, responseCode, responseDescription, responseHeaders);
      });
  }

  /**
   * Add a dragonball user.
   */
  function addDragonBallUser() {
    const params = {
      username: document.getElementById("input-username").value,
      email: document.getElementById("input-email").value,
      age: document.getElementById("input-age").value,
      powerLevel: document.getElementById("input-powerLevel").value,
      stamina: document.getElementById("input-stamina").value
    };
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.post(config, SERVLET_SERVICE_URI, kameHouse.http.getUrlEncodedHeaders(), params,
      (responseBody, responseCode, responseDescription, responseHeaders) => { window.location.href = 'users-list'; },
      (responseBody, responseCode, responseDescription, responseHeaders) => {
        const errorMessage = 'Error adding dragonball user';
        handleApiErrorResponse(errorMessage, responseBody, responseCode, responseDescription, responseHeaders);
      });
  }

  /**
   * Update a dragonball user.
   */
  function updateDragonBallUser() {
    const params = {
      id: document.getElementById("input-id").value,
      username: document.getElementById("input-username").value,
      email: document.getElementById("input-email").value,
      age: document.getElementById("input-age").value,
      powerLevel: document.getElementById("input-powerLevel").value,
      stamina: document.getElementById("input-stamina").value
    };
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.put(config, SERVLET_SERVICE_URI, kameHouse.http.getUrlEncodedHeaders(), params,
      (responseBody, responseCode, responseDescription, responseHeaders) => {window.location.href = 'users-list'},
      (responseBody, responseCode, responseDescription, responseHeaders) => {
        const errorMessage = 'Error updating dragonball user';
        handleApiErrorResponse(errorMessage, responseBody, responseCode, responseDescription, responseHeaders);
      });
  }

  /**
  * Delete dragonball user.
  */
  function deleteDragonBallUser(id) {
    const params = {
      id: id
    };
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.delete(config, SERVLET_SERVICE_URI, kameHouse.http.getUrlEncodedHeaders(), params,
      (responseBody, responseCode, responseDescription, responseHeaders) => getAllDragonBallUsers(),
      (responseBody, responseCode, responseDescription, responseHeaders) => {
        const errorMessage = 'Error deleting dragonball user';
        displayErrorTable(errorMessage);
        handleApiErrorResponse(errorMessage, responseBody, responseCode, responseDescription, responseHeaders);
      });
  }
  
  /** Display api error */
  function handleApiErrorResponse(errorMessage, responseBody, responseCode, responseDescription, responseHeaders) {
    if (!kameHouse.core.isEmpty(responseBody)) {
      try {
        errorMessage = errorMessage + " : " + JSON.parse(responseBody).message;
      } catch (e) {
        kameHouse.logger.error("Error parsing response body");
      } 
    }
    kameHouse.plugin.modal.basicModal.open(errorMessage);
    kameHouse.logger.logApiError(responseBody, responseCode, responseDescription, responseHeaders, errorMessage);
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

$(document).ready(() => {
  kameHouse.addExtension("dragonBallUserServiceJsp", new DragonBallUserServiceJsp());
});