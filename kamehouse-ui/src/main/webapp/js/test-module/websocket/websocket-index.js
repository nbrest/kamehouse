/**
 * Test module websocket functionality.
 * 
 * Dependencies: logger, kameHouseWebSocket
 * 
 * @author nbrest
 */
class TestWebSocket {
  
  #websocket = null;

  /**
   * Load the extension.
   */
  load() {
    kameHouse.logger.info("Started initializing TestWebSocket");
    kameHouse.util.banner.setRandomAllBanner();
    kameHouse.core.loadKameHouseWebSocket();
    kameHouse.logger.setLogLevel(4);
    document.querySelectorAll("form").forEach((form) => {
      form.addEventListener('submit', (e) => e.preventDefault())
    });
    kameHouse.util.dom.setClick(document.getElementById("connect"), null, () => this.#connectWebSocket());
    kameHouse.util.dom.setClick(document.getElementById("disconnect"), null, () => this.#disconnectWebSocket());
    kameHouse.util.dom.setClick(document.getElementById("send"), null, () => this.#sendWebSocketRequest());
    kameHouse.util.module.waitForModules(["kameHouseWebSocket"], () => {
      this.#websocket = new KameHouseWebSocket();
      this.#websocket.statusUrl('/kame-house-testmodule/api/ws/test-module/websocket');
      this.#websocket.topicUrl('/topic/test-module/websocket-out');
      this.#websocket.pollUrl("/app/test-module/websocket-in");
    });
  }

  /**
   * Update the view based on the websocket being connected or disconnected.
   */
  #setConnected(isConnected) {
    if (isConnected) {
      kameHouse.util.dom.addClass(document.getElementById("connect"), "hidden-kh");
      kameHouse.util.dom.removeClass(document.getElementById("connected"), "hidden-kh");
      kameHouse.util.dom.removeClass(document.getElementById("disconnect"), "hidden-kh");
      kameHouse.util.dom.addClass(document.getElementById("disconnected"), "hidden-kh");
      kameHouse.util.dom.removeClass(document.getElementById("send-label"), "hidden-kh");
      kameHouse.util.dom.removeClass(document.getElementById("send"), "hidden-kh");
      kameHouse.util.dom.removeClass(document.getElementById("websocket-responses-wrapper"), "hidden-kh");
    } else {
      kameHouse.util.dom.removeClass(document.getElementById("connect"), "hidden-kh");
      kameHouse.util.dom.addClass(document.getElementById("connected"), "hidden-kh");
      kameHouse.util.dom.addClass(document.getElementById("disconnect"), "hidden-kh");
      kameHouse.util.dom.removeClass(document.getElementById("disconnected"), "hidden-kh");
      kameHouse.util.dom.addClass(document.getElementById("send-label"), "hidden-kh");
      kameHouse.util.dom.addClass(document.getElementById("send"), "hidden-kh");
      kameHouse.util.dom.addClass(document.getElementById("websocket-responses-wrapper"), "hidden-kh");
    }
    kameHouse.util.dom.empty(document.getElementById("websocket-responses"));
  }

  /**
   * Connect the websocket.
   */
  #connectWebSocket() {
    kameHouse.plugin.modal.loadingWheelModal.open("Connecting websocket...");
    this.#websocket.connect((testWebSocketResponse) => this.#showTestWebSocketResponse(kameHouse.json.parse(testWebSocketResponse.body)));
    setTimeout(() => {
      if (this.#websocket.isConnected()) {
        kameHouse.plugin.modal.loadingWheelModal.close();
        this.#setConnected(true);
        kameHouse.logger.debug("Connected WebSocket");
      } else {
        kameHouse.plugin.modal.loadingWheelModal.close();
        this.#setConnected(false);
        const message = "Error connecting websocket";
        kameHouse.logger.error(message, kameHouse.logger.getRedText(message));
        kameHouse.plugin.modal.basicModal.open("Error connecting websocket");
      }
    }, 4000);
  }

  /**
   * Disconnect the websocket.
   */
  #disconnectWebSocket() {
    this.#websocket.disconnect();
    this.#setConnected(false);
    kameHouse.logger.debug("Disconnected WebSocket");
  }

  /**
   * Send a message through the websocket.
   */
  #sendWebSocketRequest() {
    const pollBody = kameHouse.json.stringify({
      'firstName': document.getElementById("first-name").value,
      'lastName': document.getElementById("last-name").value
    });
    this.#websocket.poll(pollBody);
  }

  /**
   * Update the view after getting a response from the websocket.
   */
  #showTestWebSocketResponse(testWebSocketResponseBody) {
    kameHouse.logger.trace("Received testWebSocketResponse from server: " + kameHouse.json.stringify(testWebSocketResponseBody));
    const date = kameHouse.util.time.getDateFromEpoch(testWebSocketResponseBody.date);
    kameHouse.util.dom.append(document.getElementById("websocket-responses"), this.#getWebsocketResponseTr(date, testWebSocketResponseBody.message));
  }

  /**
   * Get websocket response table row.
   */
  #getWebsocketResponseTr(date, message) {
    return kameHouse.util.dom.getTrTd(date.toLocaleString() + " : " + message);
  }
}

kameHouse.ready(() => {
  kameHouse.addExtension("testWebSocket", new TestWebSocket());
});
