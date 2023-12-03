/**
 * Custom WebSocket functionality to send and receive messages over Stomp. 
 * 
 * Dependencies: logger
 * 
 * External Dependencies: SockJS, Stomp.
 * 
 * @author nbrest
 */
class KameHouseWebSocket {

  #stompClients = [];
  #stompClient = null;
  #stompClientDebugFunction = null;
  #statusUrl = null;
  #topicUrl = null;
  #pollUrl = null;
  
  /**
   * Set status url.
   */
  statusUrl(statusUrlParam) {
    kameHouse.util.mobile.exec(
      () => {
        this.#statusUrl = statusUrlParam; 
      },
      () => {
        // It's better to also waitForModule kameHouseMobile in the caller of this function so I get a synchronous set of the statusUrl and the connect call usually done inmediately after doesn't fail. See vlc-player.js
        if (kameHouse.util.module.isModuleLoaded("kameHouseMobile")) {
          this.#statusUrl = kameHouse.extension.mobile.core.getSelectedBackendServerUrl() + statusUrlParam;
        } else {
          kameHouse.util.module.waitForModules(["kameHouseMobile"], () => {
            this.#statusUrl = kameHouse.extension.mobile.core.getSelectedBackendServerUrl() + statusUrlParam;
          });
        }
      }
    );
  }

  /**
   * Set topic url.
   */
  topicUrl(topicUrlParam) { this.#topicUrl = topicUrlParam; }

  /**
   * Set poll url.
   */
  pollUrl(pollUrlParam) { this.#pollUrl = pollUrlParam; }
 
  /** Checks if the websocket is connected. */
  isConnected(stompClientParam) {
    try {
      if (kameHouse.core.isEmpty(stompClientParam)) {
        // Check the current stompClient by default.
        stompClientParam = this.#stompClient;
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
  connect(topicResponseCallback) {
    if (this.isConnected()) {
      kameHouse.logger.warn("WebSocket is already connected!");
    }
    if (kameHouse.core.isEmpty(this.#statusUrl) || kameHouse.core.isEmpty(this.#topicUrl) || kameHouse.core.isEmpty(this.#pollUrl)) {
      const message = "statusUrl or topicUrl are not set. Can't connect.";
      kameHouse.logger.error(message, kameHouse.logger.getRedText(message));
      return;
    }
    if (!kameHouse.core.isFunction(topicResponseCallback)) {
      const message = "The parameter passed is not a valid function. Can't connect.";
      kameHouse.logger.error(message, kameHouse.logger.getRedText(message));
      return;
    }
    try {
      this.#disconnectPreviousConnections();
      kameHouse.logger.trace("Attempting to connect websocket to " + this.#statusUrl);
      const socket = new SockJS(this.#statusUrl);
      this.#stompClient = Stomp.over(socket);
      this.#stompClientDebugFunction = this.#stompClient.debug;
      this.disableStompDebugMode();
      this.#stompClient.connect({}, (frame) => {
        try {
          kameHouse.logger.debug('Connected WebSocket: ' + frame);
          this.#stompClient.subscribe(this.#topicUrl, (topicResponse) => { 
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
        const message = "Error during stompClient.connect()";
        kameHouse.logger.error(message, kameHouse.logger.getRedText(message));
      });
      this.#stompClients.push(this.#stompClient);
    } catch (error) {
      kameHouse.logger.error("Error connecting websocket: " + error);
    }
  }

  /** Disconnect the websocket. */
  disconnect(stompClientParam) {
    if (kameHouse.core.isEmpty(stompClientParam)) {
      // Disconnect the current connection.
      stompClientParam = this.#stompClient;
    }
    if (!kameHouse.core.isEmpty(stompClientParam)) {
      if (!this.isConnected(stompClientParam)) {
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
      const message = "stompClient is not set. Can't disconnect.";
      kameHouse.logger.error(message, kameHouse.logger.getRedText(message));
    }
  }

  /** Poll for an updated from the server. */
  poll(pollBody, pollHeaders) {
    // Setting this as trace as it executes every second in VlcPlayer 
    // so if I want to debug other stuff it's noisy.
    if (kameHouse.core.isEmpty(this.#pollUrl)) {
      const message = "pollUrl is not set. Can't poll";
      kameHouse.logger.error(message, kameHouse.logger.getRedText(message));
      return;
    }
    if (!this.isConnected()) {
      kameHouse.logger.warn("WebSocket is not connected. Can't poll");
    }
    if (kameHouse.core.isEmpty(pollBody)) {
      pollBody = {};
    }
    if (kameHouse.core.isEmpty(pollHeaders)) {
      pollHeaders = {};
    }
    try {
      this.#stompClient.send(this.#pollUrl, pollHeaders, pollBody);
    } catch (error) {
      kameHouse.logger.error("Error polling the websocket: " + error);
    }
  }

  /**
   * Enable console messages for stomp. Only enable if I need to debug connection issues.
   */
  enableStompDebugMode() { this.#stompClient.debug = this.#stompClientDebugFunction; }

  /**
   * Disable console messages for stomp. Only enable if I need to debug connection issues.
   */
  disableStompDebugMode() { this.#stompClient.debug = null; }

  /** Clean up previous stompClient connections. */
  #disconnectPreviousConnections() {
    for (let i = this.#stompClients.length - 1; i >= 0; i--) {
      this.disconnect(this.#stompClients[i]); 
      this.#stompClients.splice(i, 1);
    }
  }
}
