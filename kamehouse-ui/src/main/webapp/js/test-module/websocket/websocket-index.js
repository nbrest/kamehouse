/**
 * Test module websocket functionality.
 * 
 * Dependencies: logger, webSocketKameHouse
 * 
 * @author nbrest
 */
var websocket;
var stompClient = null;

/** Main function. */
var main = () => {
  bannerUtils.setRandomSaintSeiyaBanner();
  moduleUtils.loadWebSocketKameHouse();
  moduleUtils.waitForModules(["logger", "webSocketKameHouse"], () => {
    logger.info("Started initializing WebSocket");
    logger.setLogLevel(4);
    websocket = new WebSocketKameHouse();
    websocket.setStatusUrl('/kame-house-testmodule/api/ws/test-module/websocket');
    websocket.setTopicUrl('/topic/test-module/websocket-out');
    websocket.setPollUrl("/app/test-module/websocket-in");
    
    $("form").on('submit', (e) => e.preventDefault());
    $("#connect").click(() => connectWebSocket());
    $("#disconnect").click(() => disconnectWebSocket());
    $("#send").click(() => sendWebSocketRequest());
  });
}

/**
 * Update the view based on the websocket being connected or disconnected.
 */
function setConnected(isConnected) {
  logger.trace(arguments.callee.name);
  if (isConnected) {
    $("#connect").addClass("hidden-kh");
    $("#connected").removeClass("hidden-kh");
    $("#disconnect").removeClass("hidden-kh");
    $("#disconnected").addClass("hidden-kh");
    $("#send").removeClass("hidden-kh");
  } else {
    $("#connect").removeClass("hidden-kh");
    $("#connected").addClass("hidden-kh");
    $("#disconnect").addClass("hidden-kh");
    $("#disconnected").removeClass("hidden-kh");
    $("#send").addClass("hidden-kh");
  }
  $("#websocket-responses").html("");
  if (isConnected) {
    $("#websocket-responses-wrapper").show();
  }
  else {
    $("#websocket-responses-wrapper").hide();
  }
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
  let pollBody = JSON.stringify({
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
  let date = new Date(parseInt(testWebSocketResponseBody.date));
  $("#websocket-responses").append(getWebsocketResponseTableRow(date.toLocaleDateString(), testWebSocketResponseBody.message));
}

/** Dynamic DOM element generation ------------------------------------------ */
function getWebsocketResponseTableRow(date, message) {
  let tableRow = $('<tr>');
  let tableRowData = $('<td>');
  tableRowData.text(date + " : " + message);
  tableRow.append(tableRowData);
  return tableRow;
}

/** Call main. */
$(document).ready(main);
