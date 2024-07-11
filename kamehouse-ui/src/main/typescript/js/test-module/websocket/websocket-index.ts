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
    kameHouse.logger.info("Started initializing TestWebSocket", null);
    kameHouse.util.banner.setRandomAllBanner(null);
    kameHouse.core.loadKameHouseWebSocket();
    kameHouse.logger.setLogLevel(4);
    document.querySelectorAll("form").forEach((form) => {
      form.addEventListener('submit', (e) => e.preventDefault())
    });
    kameHouse.util.dom.setClickById("connect", null, () => this.#connectWebSocket());
    kameHouse.util.dom.setClickById("disconnect", null, () => this.#disconnectWebSocket());
    kameHouse.util.dom.setClickById("send", null, () => this.#sendWebSocketRequest());
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
      kameHouse.util.dom.classListAddById("connect", "hidden-kh");
      kameHouse.util.dom.classListRemoveById("connected", "hidden-kh");
      kameHouse.util.dom.classListRemoveById("disconnect", "hidden-kh");
      kameHouse.util.dom.classListAddById("disconnected", "hidden-kh");
      kameHouse.util.dom.classListRemoveById("send-label", "hidden-kh");
      kameHouse.util.dom.classListRemoveById("send", "hidden-kh");
      kameHouse.util.dom.classListRemoveById("websocket-responses-wrapper", "hidden-kh");
    } else {
      kameHouse.util.dom.classListRemoveById("connect", "hidden-kh");
      kameHouse.util.dom.classListAddById("connected", "hidden-kh");
      kameHouse.util.dom.classListAddById("disconnect", "hidden-kh");
      kameHouse.util.dom.classListRemoveById("disconnected", "hidden-kh");
      kameHouse.util.dom.classListAddById("send-label", "hidden-kh");
      kameHouse.util.dom.classListAddById("send", "hidden-kh");
      kameHouse.util.dom.classListAddById("websocket-responses-wrapper", "hidden-kh");
    }
    kameHouse.util.dom.emptyById("websocket-responses");
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
        kameHouse.logger.debug("Connected WebSocket", null);
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
    kameHouse.logger.debug("Disconnected WebSocket", null);
  }

  /**
   * Send a message through the websocket.
   */
  #sendWebSocketRequest() {
    const pollBody = kameHouse.json.stringify({
      'firstName': (document.getElementById("first-name") as HTMLInputElement).value,
      'lastName': (document.getElementById("last-name") as HTMLInputElement).value
    }, null, null);
    this.#websocket.poll(pollBody);
  }

  /**
   * Update the view after getting a response from the websocket.
   */
  #showTestWebSocketResponse(testWebSocketResponseBody) {
    kameHouse.logger.trace("Received testWebSocketResponse from server: " + kameHouse.json.stringify(testWebSocketResponseBody, null, null), null);
    const date = kameHouse.util.time.getDateFromEpoch(testWebSocketResponseBody.date);
    kameHouse.util.dom.appendById("websocket-responses", this.#getWebsocketResponseTr(date, testWebSocketResponseBody.message));
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
