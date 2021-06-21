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
    logger.logLevel = 4;
    websocket = new WebSocketKameHouse();
    websocket.setStatusUrl('/kame-house-testmodule/api/ws/test-module/websocket');
    websocket.setTopicUrl('/topic/test-module/websocket-out');
    websocket.setPollUrl("/app/test-module/websocket-in");
    $(() => {
      $("form").on('submit', (e) => e.preventDefault());
      $("#connect").click(() => connectWebSocket());
      $("#disconnect").click(() => disconnectWebSocket());
      $("#send").click(() => sendWebSocketRequest());
    });
  });
}

function setConnected(isConnected) {
  logger.trace(arguments.callee.name);
  $("#connect").prop("disabled", isConnected);
  $("#disconnect").prop("disabled", !isConnected);
  $("#send").prop("disabled", !isConnected);
  $("#websocket-responses").html("");
  if (isConnected) {
    $("#websocket-responses-wrapper").show();
  }
  else {
    $("#websocket-responses-wrapper").hide();
  }
}

function connectWebSocket() {
  logger.trace(arguments.callee.name);
  websocket.connect((testWebSocketResponse) => showTestWebSocketResponse(JSON.parse(testWebSocketResponse.body)));
  setConnected(true);
  logger.debug("Connected WebSocket");
}

function disconnectWebSocket() {
  logger.trace(arguments.callee.name);
  websocket.disconnect();
  setConnected(false);
  logger.debug("Disconnected WebSocket");
}

function sendWebSocketRequest() {
  logger.trace(arguments.callee.name);
  let pollBody = JSON.stringify({
    'firstName': $("#firstName").val(),
    'lastName': $("#lastName").val()
  });
  websocket.poll(pollBody);
}

function showTestWebSocketResponse(testWebSocketResponseBody) {
  logger.trace(arguments.callee.name);
  logger.trace("Received testWebSocketResponse from server: " + JSON.stringify(testWebSocketResponseBody));
  let date = new Date(parseInt(testWebSocketResponseBody.date));
  $("#websocket-responses").append("<tr><td>" + date.toLocaleString() + " : " + testWebSocketResponseBody.message + "</td></tr>");
}

/** Call main. */
$(document).ready(main);
