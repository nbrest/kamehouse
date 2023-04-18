/**
 * Custom WebSocket functionality to send and receive messages over Stomp. 
 * 
 * Dependencies: logger
 * 
 * External Dependencies: SockJS, Stomp.
 * 
 * @author nbrest
 */
function KameHouseWebSocket() {

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
  
  function setStatusUrl(statusUrlParam) {
    kameHouse.util.mobile.executeOnMobile(
      () => {
        kameHouse.util.module.waitForModules(["kameHouseMobile"], () => {
          statusUrl = kameHouse.extension.mobile.core.getBackendServer() + statusUrlParam;
        });
      },
      () => {
        statusUrl = statusUrlParam; 
      }
    );
  }

  function setTopicUrl(topicUrlParam) { topicUrl = topicUrlParam; }

  function setPollUrl(pollUrlParam) { pollUrl = pollUrlParam; }
 
  /** Checks if the websocket is connected. */
  function isConnected(stompClientParam) {
    try {
      if (kameHouse.core.isEmpty(stompClientParam)) {
        // Check the current stompClient by default.
        stompClientParam = stompClient;
      }
      if (!kameHouse.core.isEmpty(stompClientParam)) {
        return stompClientParam.connected;
      } else {
        return false;
      }
    } catch (error) {
      kameHouse.logger.error("Error in websocket-kamehouse while executing isConnected(): " + error);
    }
  }

  /** Connect the websocket. */
  function connect(topicResponseCallback) {
    if (isConnected()) {
      kameHouse.logger.warn("WebSocket is already connected!");
    }
    if (kameHouse.core.isEmpty(statusUrl) || kameHouse.core.isEmpty(topicUrl) || kameHouse.core.isEmpty(pollUrl)) {
      kameHouse.logger.error("statusUrl or topicUrl are not set. Can't connect.");
      return;
    }
    if (!kameHouse.core.isFunction(topicResponseCallback)) {
      kameHouse.logger.error("The parameter passed is not a valid function. Can't connect.");
      return;
    }
    try {
      disconnectPreviousConnections();
      kameHouse.logger.trace("Attempting to connect websocket to " + statusUrl);
      const socket = new SockJS(statusUrl);
      stompClient = Stomp.over(socket);
      stompClientDebugFunction = stompClient.debug;
      disableStompDebugMode();
      stompClient.connect({}, (frame) => {
        try {
          kameHouse.logger.debug('Connected WebSocket: ' + frame);
          stompClient.subscribe(topicUrl, (topicResponse) => { 
            try {
              topicResponseCallback(topicResponse);
            } catch (error) {
              kameHouse.logger.error("Error during stompClient.subscribe() callback: " + error);
            }
          });
        } catch(error) {
          kameHouse.logger.error("Error during stompClient.connect() callback: " + error);
        }
      },
      () => {
        kameHouse.logger.error("Error during stompClient.connect()");
      });
      stompClients.push(stompClient);
    } catch (error) {
      kameHouse.logger.error("Error connecting websocket: " + error);
    }
  }

  /** Disconnect the websocket. */
  function disconnect(stompClientParam) {
    if (kameHouse.core.isEmpty(stompClientParam)) {
      // Disconnect the current connection.
      stompClientParam = stompClient;
    }
    if (!kameHouse.core.isEmpty(stompClientParam)) {
      if (!isConnected(stompClientParam)) {
        kameHouse.logger.debug("WebSocket is not connected. No need to disconnect but attempting anyway.");
      }
      try {
        const subscriptions = Object.keys(stompClientParam.subscriptions);
        if (!kameHouse.core.isEmpty(subscriptions)) {
          subscriptions.forEach((subscription) => {
            kameHouse.logger.debug("Unsubscribing from " + subscription);
            stompClientParam.unsubscribe(subscription);
          });
        }
        kameHouse.logger.debug("Disconnecting websocket.");
        stompClientParam.disconnect();
      } catch (error) {
        kameHouse.logger.error("Error disconnecting web socket: " + error);
      }
    } else {
      kameHouse.logger.error("stompClient is not set. Can't disconnect.");
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
    if (kameHouse.core.isEmpty(pollUrl)) {
      kameHouse.logger.error("pollUrl is not set. Can't poll");
      return;
    }
    if (!isConnected()) {
      kameHouse.logger.warn("WebSocket is not connected. Can't poll");
    }
    if (kameHouse.core.isEmpty(pollBody)) {
      pollBody = {};
    }
    if (kameHouse.core.isEmpty(pollHeaders)) {
      pollHeaders = {};
    }
    try {
      stompClient.send(pollUrl, pollHeaders, pollBody);
    } catch (error) {
      kameHouse.logger.error("Error polling the websocket: " + error);
    }
  }

  //Enable console messages for stomp. Only enable if I need to debug connection issues.
  function enableStompDebugMode() { stompClient.debug = stompClientDebugFunction; }

  //Disable console messages for stomp. Only enable if I need to debug connection issues.
  function disableStompDebugMode() { stompClient.debug = null; }
}
