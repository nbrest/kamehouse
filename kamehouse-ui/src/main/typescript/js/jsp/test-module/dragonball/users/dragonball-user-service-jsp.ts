/**
 * Functionality to manage the dragonball users in the UI through the jsps and servconst api.
 */
class DragonBallUserServiceJsp {

  static #SERVLET_SERVICE_URI = '/kame-house-testmodule/api/v1/servlet/test-module/dragonball/users';

  /**
   * Load the extension.
   */
  load() {
    kameHouse.logger.info("Loading DragonBallUserServiceJsp", null);
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
      username: (document.getElementById("input-username") as HTMLInputElement).value,
      email: (document.getElementById("input-email") as HTMLInputElement).value,
      age: (document.getElementById("input-age") as HTMLInputElement).value,
      powerLevel: (document.getElementById("input-powerLevel") as HTMLInputElement).value,
      stamina: (document.getElementById("input-stamina") as HTMLInputElement).value
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
      id: (document.getElementById("input-id") as HTMLInputElement).value,
      username: (document.getElementById("input-username") as HTMLInputElement).value,
      email: (document.getElementById("input-email") as HTMLInputElement).value,
      age: (document.getElementById("input-age") as HTMLInputElement).value,
      powerLevel: (document.getElementById("input-powerLevel") as HTMLInputElement).value,
      stamina: (document.getElementById("input-stamina") as HTMLInputElement).value
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
        kameHouse.logger.error(message, null);
      } 
    }
    kameHouse.plugin.modal.basicModal.open(errorMessage);
    kameHouse.logger.error(errorMessage, null);
  }

  /**
   * Display the dragonball user to edit.
   */
  #displayDragonBallUserToEdit(dragonBallUser) {
    (document.getElementById("input-id") as HTMLInputElement).value = dragonBallUser.id;
    (document.getElementById("input-username") as HTMLInputElement).value = dragonBallUser.username;
    (document.getElementById("input-email") as HTMLInputElement).value = dragonBallUser.email;
    (document.getElementById("input-age") as HTMLInputElement).value = dragonBallUser.age;
    (document.getElementById("input-powerLevel") as HTMLInputElement).value = dragonBallUser.powerLevel;
    (document.getElementById("input-stamina") as HTMLInputElement).value = dragonBallUser.stamina;
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
    return kameHouse.util.dom.getButton({
      attr: {
        class: "img-btn-kh m-15-d-r-kh",
      },
      mobileClass: null,
      backgroundImg: "/kame-house/img/other/edit.png",
      html: null,
      data: null,
      click: (event, data) => kameHouse.core.windowLocation("users-edit?username=" + username)
    });
  }

  /**
   * Get delete button.
   */
  #getDeleteButton(id) {
    return kameHouse.util.dom.getButton({
      attr: {
        class: "img-btn-kh",
      },
      mobileClass: null,
      backgroundImg: "/kame-house/img/other/delete.png",
      html: null,
      data: null,
      click: (event, data) => this.deleteDragonBallUser(id)
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