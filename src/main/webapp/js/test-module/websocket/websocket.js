global.logLevel = 4;

var stompClient = null;

function setConnected(isConnected) {
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
    var socket = new SockJS('/kame-house/api/ws/test-module/websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected WebSocket: ' + frame);
        stompClient.subscribe('/topic/test-module/websocket-out', function (testWebSocketResponse) {
            showTestWebSocketResponse(JSON.parse(testWebSocketResponse.body));
        });
    });
}

function disconnectWebSocket() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected WebSocket");
}

function sendWebSocketRequest() {
    stompClient.send("/app/test-module/websocket-in", {}, 
        JSON.stringify({'firstName': $("#firstName").val(), 'lastName': $("#lastName").val()}));
}

function showTestWebSocketResponse(testWebSocketResponse) {
    console.log("Received testWebSocketResponse from server: " + JSON.stringify(testWebSocketResponse));
    $("#websocket-responses").append("<tr><td>" + testWebSocketResponse.date + " : " 
        + testWebSocketResponse.message + "</td></tr>");
}

$(function () {
    $("form").on('submit', function (e) {e.preventDefault();});
    $("#connect").click(function() { connectWebSocket(); });
    $("#disconnect").click(function() { disconnectWebSocket(); });
    $("#send").click(function() { sendWebSocketRequest(); }); 
});

