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
    kameHouse.util.module.loadKameHouseWebSocket();
    kameHouse.logger.setLogLevel(4);
    $("form").on('submit', (e) => e.preventDefault());
    kameHouse.util.dom.setClick($("#connect"), null, () => this.#connectWebSocket());
    kameHouse.util.dom.setClick($("#disconnect"), null, () => this.#disconnectWebSocket());
    kameHouse.util.dom.setClick($("#send"), null, () => this.#sendWebSocketRequest());
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
      kameHouse.util.dom.addClass($("#connect"), "hidden-kh");
      kameHouse.util.dom.removeClass($("#connected"), "hidden-kh");
      kameHouse.util.dom.removeClass($("#disconnect"), "hidden-kh");
      kameHouse.util.dom.addClass($("#disconnected"), "hidden-kh");
      kameHouse.util.dom.removeClass($("#send-label"), "hidden-kh");
      kameHouse.util.dom.removeClass($("#send"), "hidden-kh");
      kameHouse.util.dom.removeClass($("#websocket-responses-wrapper"), "hidden-kh");
    } else {
      kameHouse.util.dom.removeClass($("#connect"), "hidden-kh");
      kameHouse.util.dom.addClass($("#connected"), "hidden-kh");
      kameHouse.util.dom.addClass($("#disconnect"), "hidden-kh");
      kameHouse.util.dom.removeClass($("#disconnected"), "hidden-kh");
      kameHouse.util.dom.addClass($("#send-label"), "hidden-kh");
      kameHouse.util.dom.addClass($("#send"), "hidden-kh");
      kameHouse.util.dom.addClass($("#websocket-responses-wrapper"), "hidden-kh");
    }
    kameHouse.util.dom.empty($("#websocket-responses"));
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
      'firstName': $("#firstName").val(),
      'lastName': $("#lastName").val()
    });
    this.#websocket.poll(pollBody);
  }

  /**
   * Update the view after getting a response from the websocket.
   */
  #showTestWebSocketResponse(testWebSocketResponseBody) {
    kameHouse.logger.trace("Received testWebSocketResponse from server: " + kameHouse.json.stringify(testWebSocketResponseBody));
    const date = kameHouse.util.time.getDateFromEpoch(testWebSocketResponseBody.date);
    kameHouse.util.dom.append($("#websocket-responses"), this.#getWebsocketResponseTr(date, testWebSocketResponseBody.message));
  }

  /**
   * Get websocket response table row.
   */
  #getWebsocketResponseTr(date, message) {
    return kameHouse.util.dom.getTrTd(date.toLocaleString() + " : " + message);
  }
}

$(document).ready(() => {
  kameHouse.addExtension("testWebSocket", new TestWebSocket());
});
