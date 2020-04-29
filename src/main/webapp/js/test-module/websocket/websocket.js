/**
 * Test module websocket functionality.
 * 
 * Dependencies: logger.
 * 
 * @author nbrest
 */
/** Main function. */
var main = function() {
  var loadingModules = ["logger"];
  waitForModules(loadingModules, initWebSocket);
};

/** Init function to execute after global dependencies are loaded. */
var initWebSocket = function() {
  logger.info("Started initializing WebSocket");
  logger.logLevel = 4;
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
  var socket = new SockJS('/kame-house/api/ws/test-module/websocket');
  stompClient = Stomp.over(socket);
  stompClient.connect({}, function (frame) {
    setConnected(true);
    logger.debug('Connected WebSocket: ' + frame);
    stompClient.subscribe('/topic/test-module/websocket-out', function (testWebSocketResponse) {
        showTestWebSocketResponse(JSON.parse(testWebSocketResponse.body));
    });
  });
}

function disconnectWebSocket() {
  logger.traceFunctionCall();
  if (stompClient !== null) {
      stompClient.disconnect();
  }
  setConnected(false);
  logger.debug("Disconnected WebSocket");
}

function sendWebSocketRequest() {
  logger.traceFunctionCall();
  stompClient.send("/app/test-module/websocket-in", {}, 
      JSON.stringify({'firstName': $("#firstName").val(), 'lastName': $("#lastName").val()}));
}

function showTestWebSocketResponse(testWebSocketResponse) {
  logger.traceFunctionCall();
  logger.trace("Received testWebSocketResponse from server: " + JSON.stringify(testWebSocketResponse));
  $("#websocket-responses").append("<tr><td>" + testWebSocketResponse.date + " : " 
      + testWebSocketResponse.message + "</td></tr>");
}

/** Call main. */
$(document).ready(main);
