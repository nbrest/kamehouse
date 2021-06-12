/**
 * HttpClient object to perform http calls.
 * 
 * Dependencies: logger.
 * 
 * @author nbrest
 */
function HttpClient() {
  let self = this;

  /** Execute an http GET request.
   * Implement and pass successCallback(responseBody, responseCode, responseDescription) 
   * and errorCallback(responseBody, responseCode, responseDescription) */
  this.get = function httpGet(url, requestHeaders, successCallback, errorCallback) {
    logger.traceFunctionCall();
    self.httpRequest("GET", url, requestHeaders, null, successCallback, errorCallback)
  }

  /** Execute an http PUT request.
   * Implement and pass successCallback(responseBody, responseCode, responseDescription) 
   * and errorCallback(responseBody, responseCode, responseDescription) */
  this.put = function httpPut(url, requestHeaders, requestBody, successCallback, errorCallback) {
    logger.traceFunctionCall();
    self.httpRequest("PUT", url, requestHeaders, requestBody, successCallback, errorCallback)
  }

  /** Execute an http POST request.
   * Implement and pass successCallback(responseBody, responseCode, responseDescription) 
   * and errorCallback(responseBody, responseCode, responseDescription) */
  this.post = function httpPost(url, requestHeaders, requestBody, successCallback, errorCallback) {
    logger.traceFunctionCall();
    self.httpRequest("POST", url, requestHeaders, requestBody, successCallback, errorCallback)
  }

  /** Execute an http PUT request.
   * Implement and pass successCallback(responseBody, responseCode, responseDescription) 
   * and errorCallback(responseBody, responseCode, responseDescription) */
  this.put = function httpPut(url, requestHeaders, requestBody, successCallback, errorCallback) {
    logger.traceFunctionCall();
    self.httpRequest("PUT", url, requestHeaders, requestBody, successCallback, errorCallback)
  }

  /** Execute an http DELETE request.
   * Implement and pass successCallback(responseBody, responseCode, responseDescription) 
   * and errorCallback(responseBody, responseCode, responseDescription) */
  this.delete = function httpDelete(url, requestHeaders, requestBody, successCallback, errorCallback) {
    logger.traceFunctionCall();
    self.httpRequest("DELETE", url, requestHeaders, requestBody, successCallback, errorCallback)
  }

  /** Execute an http request with the specified http method. 
   * Implement and pass successCallback(responseBody, responseCode, responseDescription) 
   * and errorCallback(responseBody, responseCode, responseDescription)
   * Don't call this method directly, instead call the wrapper get(), post(), put(), delete() */
  this.httpRequest = function httpRequest(httpMethod, url, requestHeaders, requestBody, successCallback, errorCallback) {
    if (isNullOrUndefined(requestBody)) {
      $.ajax({
        type: httpMethod,
        url: url,
        headers: requestHeaders,
        success: (data, status, xhr) => processSuccess(data, status, xhr, successCallback),
        error: (jqXhr, textStatus, errorMessage) => processError(jqXhr, textStatus, errorMessage, errorCallback)
      });
    } else {
      $.ajax({
        type: httpMethod,
        url: url,
        data: JSON.stringify(requestBody),
        headers: requestHeaders,
        success: (data, status, xhr) => processSuccess(data, status, xhr, successCallback),
        error: (jqXhr, textStatus, errorMessage) => processError(jqXhr, textStatus, errorMessage, errorCallback)
      });
    }
  }

  /** Process a successful response from the api call */
  function processSuccess(data, status, xhr, successCallback) {
    /**
     * data: response body
     * status: success/error
     * xhr: {
     *    readyState: 4
     *    responseText: response body as text
     *    responseJson: response body as json
     *    status: numeric status code
     *    statusText: status code as text (success/error)
     * }
     */
    let responseBody = data;
    let responseCode = xhr.status;
    let responseDescription = xhr.statusText;
    successCallback(responseBody, responseCode, responseDescription);
  }

  /** Process an error response from the api call */
  function processError(jqXhr, textStatus, errorMessage, errorCallback) {
     /**
      * jqXhr: {
      *    readyState: 4
      *    responseText: response body as text
      *    status: numeric status code
      *    statusText: status code as text (success/error)
      * }
      * textStatus: response body
      * errorMessage: (so far came empty, might have the response body)
      */
     let responseBody = jqXhr.responseText;
     let responseCode = jqXhr.status;
     let responseDescription = jqXhr.statusText;
     logger.error(JSON.stringify(jqXhr));
     errorCallback(responseBody, responseCode, responseDescription);
  }

  /** Get request headers object with Url Encoded content type. */
  this.getUrlEncodedHeaders = () => {
    let requestHeaders = {};
    requestHeaders.Accept = '*/*';
    requestHeaders['Content-Type'] = "application/x-www-form-urlencoded";
    logger.trace("request headers: " + JSON.stringify(requestHeaders));
    return requestHeaders;
  }

  /** Get request headers object with application json content type. */
  this.getApplicationJsonHeaders = () => {
    let requestHeaders = {};
    requestHeaders.Accept = '*/*';
    requestHeaders['Content-Type'] = 'application/json';
    logger.trace("request headers: " + JSON.stringify(requestHeaders));
    return requestHeaders;
  }
}
