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
  moduleUtils.loadWebSocketKameHouse();
  moduleUtils.waitForModules(["logger", "webSocketKameHouse"], () => {
    logger.info("Started initializing WebSocket");
    logger.logLevel = 4;
    websocket = new WebSocketKameHouse();
    websocket.setStatusUrl('/kame-house/api/ws/test-module/websocket');
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
  logger.traceFunctionCall();
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
  logger.traceFunctionCall();
  websocket.connect((testWebSocketResponse) => showTestWebSocketResponse(JSON.parse(testWebSocketResponse.body)));
  setConnected(true);
  logger.debug("Connected WebSocket");
}

function disconnectWebSocket() {
  logger.traceFunctionCall();
  websocket.disconnect();
  setConnected(false);
  logger.debug("Disconnected WebSocket");
}

function sendWebSocketRequest() {
  logger.traceFunctionCall();
  let pollBody = JSON.stringify({
    'firstName': $("#firstName").val(),
    'lastName': $("#lastName").val()
  });
  websocket.poll(pollBody);
}

function showTestWebSocketResponse(testWebSocketResponseBody) {
  logger.traceFunctionCall();
  logger.trace("Received testWebSocketResponse from server: " + JSON.stringify(testWebSocketResponseBody));
  $("#websocket-responses").append("<tr><td>" + testWebSocketResponseBody.date + " : "
      +testWebSocketResponseBody.message + "</td></tr>");
}

/** Call main. */
$(document).ready(main);
