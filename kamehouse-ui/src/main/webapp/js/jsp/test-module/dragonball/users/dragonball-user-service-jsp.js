/**
 * Functionality to manage the dragonball users in the UI through the jsps and servconst api.
 */
class DragonBallUserServiceJsp {

  static #SERVLET_SERVICE_URI = '/kame-house-testmodule/api/v1/servlet/test-module/dragonball/users';

  /**
   * Load the extension.
   */
  load() {
    kameHouse.logger.info("Loading DragonBallUserServiceJsp");
    kameHouse.util.module.waitForModules(["kameHouseModal", "kameHouseDebugger"], () => {
      kameHouse.util.module.setModuleLoaded("dragonBallUserServiceJsp");
    });
  }

  /**
   * Get a dragonball user and populate it to the edit table.
   */
  getDragonBallUser(event) {
    const urlParams = new URLSearchParams(window.location.search);
    const params ={
      "username" : urlParams.get('username')
    };
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.get(config, DragonBallUserServiceJsp.#SERVLET_SERVICE_URI, kameHouse.http.getUrlEncodedHeaders(), params, 
      (responseBody, responseCode, responseDescription, responseHeaders) => this.#displayDragonBallUserToEdit(responseBody),
      (responseBody, responseCode, responseDescription, responseHeaders) => {
        let errorMessage = 'Error getting dragonball user';
        this.#handleApiErrorResponse(DragonBallUserServiceJsp.#SERVLET_SERVICE_URI, errorMessage, responseBody, responseCode, responseDescription, responseHeaders);
      });
  }

  /**
   * Get all dragonball users.
   */
  getAllDragonBallUsers() {
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.get(config, DragonBallUserServiceJsp.#SERVLET_SERVICE_URI, null, null, 
      (responseBody, responseCode, responseDescription, responseHeaders) => this.#displayDragonBallUsers(responseBody),
      (responseBody, responseCode, responseDescription, responseHeaders) => {
        const errorMessage = 'Error getting dragonball users from the backend';
        this.#displayErrorTable(errorMessage);
        this.#handleApiErrorResponse(DragonBallUserServiceJsp.#SERVLET_SERVICE_URI, errorMessage, responseBody, responseCode, responseDescription, responseHeaders);
      });
  }

  /**
   * Add a dragonball user.
   */
  addDragonBallUser() {
    const params = {
      username: document.getElementById("input-username").value,
      email: document.getElementById("input-email").value,
      age: document.getElementById("input-age").value,
      powerLevel: document.getElementById("input-powerLevel").value,
      stamina: document.getElementById("input-stamina").value
    };
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.post(config, DragonBallUserServiceJsp.#SERVLET_SERVICE_URI, kameHouse.http.getUrlEncodedHeaders(), params,
      (responseBody, responseCode, responseDescription, responseHeaders) => {kameHouse.core.windowLocation('users-list')},
      (responseBody, responseCode, responseDescription, responseHeaders) => {
        const errorMessage = 'Error adding dragonball user';
        this.#handleApiErrorResponse(DragonBallUserServiceJsp.#SERVLET_SERVICE_URI, errorMessage, responseBody, responseCode, responseDescription, responseHeaders);
      });
  }

  /**
   * Update a dragonball user.
   */
  updateDragonBallUser() {
    const params = {
      id: document.getElementById("input-id").value,
      username: document.getElementById("input-username").value,
      email: document.getElementById("input-email").value,
      age: document.getElementById("input-age").value,
      powerLevel: document.getElementById("input-powerLevel").value,
      stamina: document.getElementById("input-stamina").value
    };
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.put(config, DragonBallUserServiceJsp.#SERVLET_SERVICE_URI, kameHouse.http.getUrlEncodedHeaders(), params,
      (responseBody, responseCode, responseDescription, responseHeaders) => {kameHouse.core.windowLocation('users-list')},
      (responseBody, responseCode, responseDescription, responseHeaders) => {
        const errorMessage = 'Error updating dragonball user';
        this.#handleApiErrorResponse(DragonBallUserServiceJsp.#SERVLET_SERVICE_URI, errorMessage, responseBody, responseCode, responseDescription, responseHeaders);
      });
  }

  /**
  * Delete dragonball user.
  */
  deleteDragonBallUser(id) {
    const params = {
      id: id
    };
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.delete(config, DragonBallUserServiceJsp.#SERVLET_SERVICE_URI, kameHouse.http.getUrlEncodedHeaders(), params,
      (responseBody, responseCode, responseDescription, responseHeaders) => this.getAllDragonBallUsers(),
      (responseBody, responseCode, responseDescription, responseHeaders) => {
        const errorMessage = 'Error deleting dragonball user';
        this.#displayErrorTable(errorMessage);
        this.#handleApiErrorResponse(DragonBallUserServiceJsp.#SERVLET_SERVICE_URI, errorMessage, responseBody, responseCode, responseDescription, responseHeaders);
      });
  }
  
  /** Display api error */
  #handleApiErrorResponse(url, errorMessage, responseBody, responseCode, responseDescription, responseHeaders) {
    if (!kameHouse.core.isEmpty(responseBody)) {
      try {
        errorMessage = errorMessage + " : " + kameHouse.json.parse(responseBody).message;
      } catch (error) {
        const message = "Error parsing api error response body. " + error;
        kameHouse.logger.error(message);
      } 
    }
    kameHouse.plugin.modal.basicModal.open(errorMessage);
    kameHouse.logger.logApiError(url, responseBody, responseCode, responseDescription, responseHeaders, errorMessage);
  }

  /**
   * Display the dragonball user to edit.
   */
  #displayDragonBallUserToEdit(dragonBallUser) {
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
  async #displayDragonBallUsers(dragonBallUsersList) {
    const dragonBallUsersTbody = document.getElementById('dragonball-users-tbody');
    kameHouse.util.dom.empty(dragonBallUsersTbody);
    kameHouse.util.dom.append(dragonBallUsersTbody, await this.#getDragonBallUserTableHeader());
    for (const dragonballUser of dragonBallUsersList) {
      kameHouse.util.dom.append(dragonBallUsersTbody, this.#getDragonBallUserTableRow(dragonballUser));
    }
  }

  /**
   * Shows the specified error message in the table.
   */
  #displayErrorTable(message) {
    const dragonBallUsersTbody = document.getElementById('dragonball-users-tbody');
    kameHouse.util.dom.empty(dragonBallUsersTbody);
    kameHouse.util.dom.append(dragonBallUsersTbody, this.#getErrorMessageTr(message));
  }
  
  /**
   * Get error message.
   */
  #getErrorMessageTr(message) {
    return kameHouse.util.dom.getTrTd(message);
  }

  /**
   * Get dragonball user table row.
   */
  #getDragonBallUserTableRow(dragonBallUser) {
    const tr = kameHouse.util.dom.getTr({}, null);
    kameHouse.util.dom.append(tr, this.#getDragonBallUserTd(dragonBallUser.id));
    kameHouse.util.dom.append(tr, this.#getDragonBallUserTd(dragonBallUser.username));
    kameHouse.util.dom.append(tr, this.#getDragonBallUserTd(dragonBallUser.email));
    kameHouse.util.dom.append(tr, this.#getDragonBallUserTd(dragonBallUser.age));
    kameHouse.util.dom.append(tr, this.#getDragonBallUserTd(dragonBallUser.powerLevel));
    kameHouse.util.dom.append(tr, this.#getDragonBallUserTd(dragonBallUser.stamina));
    kameHouse.util.dom.append(tr, this.#getActionButtonsTd(dragonBallUser.username, dragonBallUser.id));
    return tr;
  }

  /**
   * Get dragonball user table row data.
   */
  #getDragonBallUserTd(dataValue) {
    return kameHouse.util.dom.getTd({}, dataValue);
  }

  /**
   * Get action buttons table data.
   */
  #getActionButtonsTd(username, id) {
    const td = kameHouse.util.dom.getTd({}, null);
    kameHouse.util.dom.append(td, this.#getEditButton(username));
    kameHouse.util.dom.append(td, this.#getDeleteButton(id));
    return td; 
  }

  /**
   * Get edit button.
   */
  #getEditButton(username) {
    return kameHouse.util.dom.getImgBtn({
      src: "/kame-house/img/other/edit.png",
      className: "img-btn-kh m-15-d-r-kh",
      alt: "Edit",
      onClick: () => kameHouse.core.windowLocation("users-edit?username=" + username)
    });
  }

  /**
   * Get delete button.
   */
  #getDeleteButton(id) {
    return kameHouse.util.dom.getImgBtn({
      src: "/kame-house/img/other/delete.png",
      className: "img-btn-kh",
      alt: "Delete",
      onClick: () => this.deleteDragonBallUser(id)
    });
  }

  /**
   * Get dragonball user table header.
   */
  #getDragonBallUserTableHeader() {
    return kameHouse.util.fetch.loadHtmlSnippet("/kame-house/html-snippets/test-module/dragonball-users-table-header.html");
  }
}

kameHouse.ready(() => {
  kameHouse.addExtension("dragonBallUserServiceJsp", new DragonBallUserServiceJsp());
});