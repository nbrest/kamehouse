/**
 * Custom WebSocket functionality to send and receive messages over Stomp. 
 * 
 * Dependencies: logger
 * 
 * External Dependencies: SockJS, Stomp.
 * 
 * @author nbrest
 */
function WebSocketKameHouse() {

  this.setStatusUrl = setStatusUrl;
  this.setTopicUrl = setTopicUrl;
  this.setPollUrl = setPollUrl;
  this.isConnected = isConnected;
  this.connect = connect;
  this.disconnect = disconnect;
  this.poll = poll;
  this.enableStompDebugMode = enableStompDebugMode;
  this.disableStompDebugMode = disableStompDebugMode;

  const stompClients = [];
  
  let stompClient = null;
  let stompClientDebugFunction = null;
  let statusUrl = null;
  let topicUrl = null;
  let pollUrl = null;
  
  function setStatusUrl(statusUrlParam) { statusUrl = statusUrlParam; }

  function setTopicUrl(topicUrlParam) { topicUrl = topicUrlParam; }

  function setPollUrl(pollUrlParam) { pollUrl = pollUrlParam; }
 
  /** Checks if the websocket is connected. */
  function isConnected(stompClientParam) {
    try {
      if (isEmpty(stompClientParam)) {
        // Check the current stompClient by default.
        stompClientParam = stompClient;
      }
      if (!isEmpty(stompClientParam)) {
        return stompClientParam.connected;
      } else {
        return false;
      }
    } catch (error) {
      logger.error("Error in websocket-kamehouse while executing isConnected(): " + error);
    }
  }

  /** Connect the websocket. */
  function connect(topicResponseCallback) {
    logger.debug(arguments.callee.name);
    if (isConnected()) {
      logger.warn("WebSocket is already connected!");
    }
    if (isEmpty(statusUrl) || isEmpty(topicUrl) || isEmpty(pollUrl)) {
      logger.error("statusUrl or topicUrl are not set. Can't connect.");
      return;
    }
    if (!isFunction(topicResponseCallback)) {
      logger.error("The parameter passed is not a valid function. Can't connect.");
      return;
    }
    try {
      disconnectPreviousConnections();
      const socket = new SockJS(statusUrl);
      stompClient = Stomp.over(socket);
      stompClientDebugFunction = stompClient.debug;
      disableStompDebugMode();
      stompClient.connect({}, (frame) => {
        try {
          logger.debug('Connected WebSocket: ' + frame);
          stompClient.subscribe(topicUrl, (topicResponse) => { 
            try {
              topicResponseCallback(topicResponse);
            } catch (error) {
              logger.error("Error during stompClient.subscribe() callback: " + error);
            }
          });
        } catch(error) {
          logger.error("Error during stompClient.connect() callback: " + error);
        }
      });
      stompClients.push(stompClient);
    } catch (error) {
      logger.error("Error connecting websocket: " + error);
    }
  }

  /** Disconnect the websocket. */
  function disconnect(stompClientParam) {
    logger.debug(arguments.callee.name);
    if (isEmpty(stompClientParam)) {
      // Disconnect the current connection.
      stompClientParam = stompClient;
    }
    if (!isEmpty(stompClientParam)) {
      if (!isConnected(stompClientParam)) {
        logger.debug("WebSocket is not connected. No need to disconnect but attempting anyway.");
      }
      try {
        const subscriptions = Object.keys(stompClientParam.subscriptions);
        if (!isEmpty(subscriptions)) {
          subscriptions.forEach((subscription) => {
            logger.debug("Unsubscribing from " + subscription);
            stompClientParam.unsubscribe(subscription);
          });
        }
        logger.debug("Disconnecting websocket.");
        stompClientParam.disconnect();
      } catch (error) {
        logger.error("Error disconnecting web socket: " + error);
      }
    } else {
      logger.error("stompClient is not set. Can't disconnect.");
      return;
    }
  }

  /** Clean up previous stompClient connections. */
  function disconnectPreviousConnections() {
    for (let i = stompClients.length - 1; i >= 0; i--) {
      disconnect(stompClients[i]); 
      stompClients.splice(i, 1);
    }
  }

  /** Poll for an updated from the server. */
  function poll(pollBody, pollHeaders) {
    // Setting this as trace as it executes every second in VlcPlayer 
    // so if I want to debug other stuff it's noisy.
    logger.trace(arguments.callee.name);
    if (isEmpty(pollUrl)) {
      logger.error("pollUrl is not set. Can't poll");
      return;
    }
    if (!isConnected()) {
      logger.warn("WebSocket is not connected. Can't poll");
    }
    if (isEmpty(pollBody)) {
      pollBody = {};
    }
    if (isEmpty(pollHeaders)) {
      pollHeaders = {};
    }
    try {
      stompClient.send(pollUrl, pollHeaders, pollBody);
    } catch (error) {
      logger.error("Error polling the websocket: " + error);
    }
  }

  //Enable console messages for stomp. Only enable if I need to debug connection issues.
  function enableStompDebugMode() { stompClient.debug = stompClientDebugFunction; }

  //Disable console messages for stomp. Only enable if I need to debug connection issues.
  function disableStompDebugMode() { stompClient.debug = null; }
}
