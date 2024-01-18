/**
 * Global js variables and functions for all pages.
 * 
 * The idea is for this to be as minimal as possible. 
 * 
 * All my main functionality should be in /kame-house project
 * 
 * @author nbrest
 */
class KameHouseGroot {

  /**
   * Load the kamehouse groot extension.
   */
  load() {
    kameHouse.extension.groot.header = new GrootHeader();
    kameHouse.extension.groot.header.renderGrootMenu();
  }

} // KameHouseGroot

/**
 * Functionality to manage the groot header.
 * 
 * @author nbrest
 */
class GrootHeader {

  /** Render groot sub menu */
  renderGrootMenu() {
    kameHouse.util.dom.load($("#groot-menu-wrapper"), "/kame-house-groot/kamehouse-groot/html/kamehouse-groot-menu.html", () => {
      this.#updateGRootMenuActiveTab();
      this.#loadSession();
    });
  }
  
  /** Load session */
  #loadSession() {
    const SESSION_STATUS_API = '/kame-house-groot/api/v1/commons/session/status.php';
    const config = kameHouse.http.getConfig();
    config.timeout = 15;
    kameHouse.http.get(config, SESSION_STATUS_API, null, null,
      (responseBody, responseCode, responseDescription, responseHeaders) => {
        kameHouse.logger.info("GRoot session: " + kameHouse.json.stringify(responseBody));
        kameHouse.extension.groot.session = responseBody;
        this.#updateSessionStatus();
        kameHouse.util.module.setModuleLoaded("kameHouseGrootSession");
        kameHouse.core.completeAuthorizeUser(responseCode, responseBody);
      },
      (responseBody, responseCode, responseDescription, responseHeaders) => {
        const message = "Error retrieving current groot session information.";
        kameHouse.logger.error(message, kameHouse.logger.getRedText(message));
        kameHouse.extension.groot.session = {};
        this.#updateSessionStatus();
        kameHouse.util.module.setModuleLoaded("kameHouseGrootSession");
        kameHouse.core.completeAuthorizeUser(responseCode, responseBody);
      }
    );
  }
  
  /**
   * Update groot session status.
   */
  #updateSessionStatus() {
    const $loginStatus = $("#groot-header-login-status");
    kameHouse.util.dom.empty($loginStatus);
    if (kameHouse.core.isEmpty(kameHouse.extension.groot.session.username) || kameHouse.extension.groot.session.username.trim() == "" ||
    kameHouse.extension.groot.session.username.trim() == "anonymousUser") {
      kameHouse.util.dom.append($loginStatus, this.#getLoginButton());
    } else {
      kameHouse.util.dom.append($loginStatus, this.#getUsernameHeader(kameHouse.extension.groot.session.username));
      kameHouse.util.dom.append($loginStatus, this.#getLogoutButton());
    }
  }

  /**
  * Set active tab in the groot sub menu.
  */
  #updateGRootMenuActiveTab() {
    const pageUrl = window.location.pathname;
    $("#groot-menu button").toArray().forEach((navItem) => {
      kameHouse.util.dom.removeClass($(navItem), "active");
      if (pageUrl == "/kame-house-groot/" || pageUrl == "/kame-house-groot/index.html") {
        if ($(navItem).attr("id") == "nav-groot-home") {
          kameHouse.util.dom.addClass($(navItem), "active");
        }
      }
      if (pageUrl.startsWith("/kame-house-groot/admin/server-manager")) {
        if ($(navItem).attr("id") == "nav-server-manager") {
          kameHouse.util.dom.addClass($(navItem), "active");
        }
      }
      if (pageUrl.startsWith("/kame-house-groot/admin/kamehouse-shell")) {
        if ($(navItem).attr("id") == "nav-kamehouse-shell") {
          kameHouse.util.dom.addClass($(navItem), "active");
        }
      }
    });
  }
  
  /**
   * Get groot login button.
   */
  #getLoginButton() {
    return kameHouse.util.dom.getImgBtn({
      src: "/kame-house/img/pc/login-gray-dark.png",
      className: "groot-header-login-status-btn",
      alt: "Login GRoot",
      onClick: () => {
        return kameHouse.util.mobile.windowLocation("/kame-house-groot/login.html", "/kame-house-mobile/settings.html");
      }
    });
  }

  /**
   * Get groot logout button.
   */
  #getLogoutButton() {
    return kameHouse.util.dom.getImgBtn({
      src: "/kame-house/img/dbz/goku-gray-dark.png",
      className: "groot-header-login-status-btn",
      alt: "Logout GRoot",
      onClick: () => {
        return kameHouse.util.mobile.windowLocation("/kame-house-groot/api/v1/auth/logout.php", "/kame-house-mobile/settings.html");
      }
    });
  }

  /**
   * Get username header.
   */
  #getUsernameHeader(username) {
    return kameHouse.util.dom.getSpan({
      class: "groot-header-login-status-text"
    }, username);
  }

} // GrootHeader

$(document).ready(() => {
  kameHouse.addExtension("groot", new KameHouseGroot());
});