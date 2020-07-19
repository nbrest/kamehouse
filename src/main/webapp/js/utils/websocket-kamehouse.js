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
  let self = this;
  this.stompClient = null;
  this.stompClientDebugFunction = null;
  this.stompClients = [];
  this.statusUrl = null;
  this.topicUrl = null;
  this.pollUrl = null;
  
  this.setStatusUrl = function setStatusUrl(statusUrl) {
    self.statusUrl = statusUrl;
  }

  this.setTopicUrl = function setTopicUrl(topicUrl) {
    self.topicUrl = topicUrl;
  }
 
  this.setPollUrl = function setPollUrl(pollUrl) {
    self.pollUrl = pollUrl;
  }

  /** Checks if the websocket is connected. */
  this.isConnected = function isConnected(stompClient) {
    if (isEmpty(stompClient)) {
      // Check the current stompClient by default.
      stompClient = self.stompClient;
    }
    if (!isEmpty(stompClient)) {
      return stompClient.connected;
    } else {
      return false;
    }
  }

  /** Connect the websocket. */
  this.connect = function connect(topicResponseCallback) {
    logger.debugFunctionCall();
    if (self.isConnected()) {
      logger.warn("WebSocket is already connected!");
    }
    if (isEmpty(self.statusUrl) || isEmpty(self.topicUrl) || isEmpty(self.pollUrl)) {
      logger.error("statusUrl or topicUrl are not set. Can't connect.");
      return;
    }
    if (!isFunction(topicResponseCallback)) {
      logger.error("The parameter passed is not a valid function. Can't connect.");
      return;
    }
    try {
      self.disconnectPreviousConnections();
      let socket = new SockJS(self.statusUrl);
      self.stompClient = Stomp.over(socket);
      self.stompClientDebugFunction = self.stompClient.debug;
      self.disableStompDebugMode();
      self.stompClient.connect({}, function(frame) {
        logger.debug('Connected WebSocket: ' + frame);
        self.stompClient.subscribe(self.topicUrl, function (topicResponse) {
          topicResponseCallback(topicResponse);
        });
      });
      self.stompClients.push(self.stompClient);
    } catch (error) {
      logger.error("Error connecting websocket: " + error);
    }
  }

  /** Disconnect the websocket. */
  this.disconnect = function disconnect(stompClient) {
    logger.debugFunctionCall();
    if (isEmpty(stompClient)) {
      // Disconnect the current connection.
      stompClient = self.stompClient;
    }
    if (!isEmpty(stompClient)) {
      if (!self.isConnected(stompClient)) {
        logger.debug("WebSocket is not connected. No need to disconnect but attempting anyway.");
      }
      try {
        let subscriptions = Object.keys(stompClient.subscriptions);
        if (!isEmpty(subscriptions)) {
          subscriptions.forEach(subscription => {
            logger.debug("Unsubscribing from " + subscription);
            stompClient.unsubscribe(subscription);
          });
        }
        logger.debug("Disconnecting websocket.");
        stompClient.disconnect();
      } catch (error) {
        logger.error("Error disconnecting web socket." + error);
      }
    } else {
      logger.error("stompClient is not set. Can't disconnect.");
      return;
    }
  }

  /** Clean up previous stompClient connections. */
  this.disconnectPreviousConnections = function disconnectPreviousConnections() {
    logger.debugFunctionCall();
    self.stompClients.forEach(stompClient => self.disconnect(stompClient));
  }

  /** Poll for an updated from the server. */
  this.poll = function poll(pollBody, pollHeaders) {
    // Setting this as trace as it executes every second in VlcPlayer 
    // so if I want to debug other stuff it's noisy.
    logger.traceFunctionCall();
    if (isEmpty(self.pollUrl)) {
      logger.error("pollUrl is not set. Can't poll");
      return;
    }
    if (!self.isConnected()) {
      logger.warn("WebSocket is not connected. Can't poll");
    }
    if (isEmpty(pollBody)) {
      pollBody = {};
    }
    if (isEmpty(pollHeaders)) {
      pollHeaders = {};
    }
    try {
      self.stompClient.send(self.pollUrl, pollHeaders, pollBody);
    } catch (error) {
      logger.error("Error polling the websocket: " + error);
    }
  }

  this.enableStompDebugMode = function enableStompDebugMode() {
    self.stompClient.debug = self.stompClientDebugFunction;
  } 

  //Disable console messages for stomp. Only enable if I need to debug connection issues.
  this.disableStompDebugMode = function disableStompDebugMode() {
    self.stompClient.debug = null;
  }
}
