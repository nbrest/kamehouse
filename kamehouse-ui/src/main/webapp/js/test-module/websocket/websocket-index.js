/**
 * Test module websocket functionality.
 * 
 * Dependencies: logger, kameHouseWebSocket
 * 
 * @author nbrest
 */
function TestWebSocket() {
  this.load = load;
  let websocket;

  function load() {
    kameHouse.logger.info("Started initializing TestWebSocket");
    kameHouse.util.banner.setRandomSaintSeiyaBanner();
    kameHouse.util.module.loadKameHouseWebSocket();
    kameHouse.util.module.waitForModules(["kameHouseWebSocket"], () => {
      kameHouse.logger.setLogLevel(4);
      websocket = new KameHouseWebSocket();
      websocket.setStatusUrl('/kame-house-testmodule/api/ws/test-module/websocket');
      websocket.setTopicUrl('/topic/test-module/websocket-out');
      websocket.setPollUrl("/app/test-module/websocket-in");
      
      $("form").on('submit', (e) => e.preventDefault());
      kameHouse.util.dom.setClick($("#connect"), null, () => connectWebSocket());
      kameHouse.util.dom.setClick($("#disconnect"), null, () => disconnectWebSocket());
      kameHouse.util.dom.setClick($("#send"), null, () => sendWebSocketRequest());
    });
  }

  /**
   * Update the view based on the websocket being connected or disconnected.
   */
  function setConnected(isConnected) {
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
  function connectWebSocket() {
    kameHouse.plugin.modal.loadingWheelModal.open("Connecting websocket...");
    websocket.connect((testWebSocketResponse) => showTestWebSocketResponse(JSON.parse(testWebSocketResponse.body)));
    setTimeout(() => {
      if (websocket.isConnected()) {
        kameHouse.plugin.modal.loadingWheelModal.close();
        setConnected(true);
        kameHouse.logger.debug("Connected WebSocket");
      } else {
        kameHouse.plugin.modal.loadingWheelModal.close();
        setConnected(false);
        kameHouse.logger.error("Error connecting websocket");
        kameHouse.plugin.modal.basicModal.open("Error connecting websocket");
      }
    }, 4000);
  }

  /**
   * Disconnect the websocket.
   */
  function disconnectWebSocket() {
    websocket.disconnect();
    setConnected(false);
    kameHouse.logger.debug("Disconnected WebSocket");
  }

  /**
   * Send a message through the websocket.
   */
  function sendWebSocketRequest() {
    const pollBody = JSON.stringify({
      'firstName': $("#firstName").val(),
      'lastName': $("#lastName").val()
    });
    websocket.poll(pollBody);
  }

  /**
   * Update the view after getting a response from the websocket.
   */
  function showTestWebSocketResponse(testWebSocketResponseBody) {
    kameHouse.logger.trace("Received testWebSocketResponse from server: " + JSON.stringify(testWebSocketResponseBody));
    const date = kameHouse.util.time.getDateFromEpoch(testWebSocketResponseBody.date);
    kameHouse.util.dom.append($("#websocket-responses"), getWebsocketResponseTr(date, testWebSocketResponseBody.message));
  }

  function getWebsocketResponseTr(date, message) {
    return kameHouse.util.dom.getTrTd(date.toLocaleString() + " : " + message);
  }
}

$(document).ready(() => {
  kameHouse.addExtension("testWebSocket", new TestWebSocket());
});
