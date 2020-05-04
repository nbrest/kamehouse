/**
 * Test module websocket functionality.
 * 
 * Dependencies: logger, webSocketKameHouse
 * 
 * @author nbrest
 */
var websocket;
/** Main function. */
var main = function() {
  loadWebSocketKameHouse();
  var loadingModules = ["logger","webSocketKameHouse"];
  waitForModules(loadingModules, initWebSocketTest);
};

function loadWebSocketKameHouse() {
  $.getScript("/kame-house/js/utils/websocket-kamehouse.js", function (data, textStatus, jqxhr) {
    let loadingModules = ["logger"];
    waitForModules(loadingModules, function initWebSocket() {
      modules.webSocketKameHouse = true;
    });
  });
}

/** Init function to execute after global dependencies are loaded. */
var initWebSocketTest = function() {
  logger.info("Started initializing WebSocket");
  logger.logLevel = 4;  
  websocket = new WebSocketKameHouse();
  websocket.setStatusUrl('/kame-house/api/ws/test-module/websocket');
  websocket.setTopicUrl('/topic/test-module/websocket-out');
  websocket.setPollUrl("/app/test-module/websocket-in");
  $(function() {
    $("form").on('submit', function (e) {
      e.preventDefault();
    });
    $("#connect").click(function() {
      connectWebSocket();
    });
    $("#disconnect").click(function() {
      disconnectWebSocket();
    });
    $("#send").click(function() {
      sendWebSocketRequest();
    });
  });
};

var stompClient = null;

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
  websocket.connect(function (testWebSocketResponse) {
    showTestWebSocketResponse(JSON.parse(testWebSocketResponse.body));
  });
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
