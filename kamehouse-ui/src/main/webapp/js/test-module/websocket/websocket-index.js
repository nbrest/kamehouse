/**
 * Test module websocket functionality.
 * 
 * Dependencies: logger, webSocketKameHouse
 * 
 * @author nbrest
 */
var websocket;

/** Main function. */
function mainWebSocket() {
  bannerUtils.setRandomSaintSeiyaBanner();
  moduleUtils.loadWebSocketKameHouse();
  moduleUtils.waitForModules(["webSocketKameHouse"], () => {
    logger.info("Started initializing WebSocket");
    logger.setLogLevel(4);
    websocket = new WebSocketKameHouse();
    websocket.setStatusUrl('/kame-house-testmodule/api/ws/test-module/websocket');
    websocket.setTopicUrl('/topic/test-module/websocket-out');
    websocket.setPollUrl("/app/test-module/websocket-in");
    
    $("form").on('submit', (e) => e.preventDefault());
    domUtils.setClick($("#connect"), null, () => connectWebSocket());
    domUtils.setClick($("#disconnect"), null, () => disconnectWebSocket());
    domUtils.setClick($("#send"), null, () => sendWebSocketRequest());
  });
}

/**
 * Update the view based on the websocket being connected or disconnected.
 */
function setConnected(isConnected) {
  logger.trace(arguments.callee.name);
  if (isConnected) {
    domUtils.addClass($("#connect"), "hidden-kh");
    domUtils.removeClass($("#connected"), "hidden-kh");
    domUtils.removeClass($("#disconnect"), "hidden-kh");
    domUtils.addClass($("#disconnected"), "hidden-kh");
    domUtils.removeClass($("#send-label"), "hidden-kh");
    domUtils.removeClass($("#send"), "hidden-kh");
    domUtils.removeClass($("#websocket-responses-wrapper"), "hidden-kh");
  } else {
    domUtils.removeClass($("#connect"), "hidden-kh");
    domUtils.addClass($("#connected"), "hidden-kh");
    domUtils.addClass($("#disconnect"), "hidden-kh");
    domUtils.removeClass($("#disconnected"), "hidden-kh");
    domUtils.addClass($("#send-label"), "hidden-kh");
    domUtils.addClass($("#send"), "hidden-kh");
    domUtils.addClass($("#websocket-responses-wrapper"), "hidden-kh");
  }
  domUtils.empty($("#websocket-responses"));
}

/**
 * Connect the websocket.
 */
function connectWebSocket() {
  logger.trace(arguments.callee.name);
  websocket.connect((testWebSocketResponse) => showTestWebSocketResponse(JSON.parse(testWebSocketResponse.body)));
  setConnected(true);
  logger.debug("Connected WebSocket");
}

/**
 * Disconnect the websocket.
 */
function disconnectWebSocket() {
  logger.trace(arguments.callee.name);
  websocket.disconnect();
  setConnected(false);
  logger.debug("Disconnected WebSocket");
}

/**
 * Send a message through the websocket.
 */
function sendWebSocketRequest() {
  logger.trace(arguments.callee.name);
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
  logger.trace(arguments.callee.name);
  logger.trace("Received testWebSocketResponse from server: " + JSON.stringify(testWebSocketResponseBody));
  const date = new Date(parseInt(testWebSocketResponseBody.date));
  domUtils.append($("#websocket-responses"), getWebsocketResponseTr(date.toLocaleDateString(), testWebSocketResponseBody.message));
}

function getWebsocketResponseTr(date, message) {
  return domUtils.getTrTd(date + " : " + message);
}

/** Call main. */
$(document).ready(mainWebSocket);
