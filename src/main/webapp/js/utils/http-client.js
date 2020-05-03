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
    var responseBody;
    var responseCode;
    var responseDescription;
    if (isEmpty(requestBody)) {
      $.ajax({
        type: httpMethod,
        url: url,
        headers: requestHeaders,
        success: function (data, status, xhr) {
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
          responseBody = data;
          responseCode = xhr.status;
          responseDescription = xhr.statusText;
          successCallback(responseBody, responseCode, responseDescription);
        },
        error: function (jqXhr, textStatus, errorMessage) {
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
          responseBody = jqXhr.responseText;
          responseCode = jqXhr.status;
          responseDescription = jqXhr.statusText;
          logger.error(JSON.stringify(jqXhr));
          errorCallback(responseBody, responseCode, responseDescription);
        }
      });
    } else {
      $.ajax({
        type: httpMethod,
        url: url,
        data: JSON.stringify(requestBody),
        headers: requestHeaders,
        success: function (data, status, xhr) {
          responseBody = data;
          responseCode = xhr.status;
          responseDescription = xhr.statusText;
          successCallback(responseBody, responseCode, responseDescription);
        },
        error: function (jqXhr, textStatus, errorMessage) {
          responseBody = jqXhr.responseText;
          responseCode = jqXhr.status;
          responseDescription = jqXhr.statusText;
          logger.error(JSON.stringify(jqXhr));
          errorCallback(responseBody, responseCode, responseDescription);
        }
      });
    }
  }

  /** Get CSRF token. */
  this.getCsrfToken = function getCsrfToken() {
    var token = $("meta[name='_csrf']").attr("content");
    return token;
  }

  /** Get CSRF header. */
  this.getCsrfHeader = function getCsrfHeader() {
    var header = $("meta[name='_csrf_header']").attr("content");
    return header;
  }

  /** Get CSRF standard requestHeaders object. */
  this.getCsrfRequestHeadersObject = function getCsrfRequestHeadersObject() {
    var csrfHeader = self.getCsrfHeader();
    var csrfToken = self.getCsrfToken();
    var requestHeaders = {};
    requestHeaders.Accept = 'application/json';
    requestHeaders['Content-Type'] = 'application/json';
    requestHeaders[csrfHeader] = csrfToken;
    logger.trace("request headers: " + JSON.stringify(requestHeaders));
    return requestHeaders;
  }

  /** Get request headers object with Url Encoded content type. */
  this.getUrlEncodedHeaders = function getUrlEncodedHeaders() {
    var requestHeaders = {};
    requestHeaders.Accept = '*/*';
    requestHeaders['Content-Type'] = "application/x-www-form-urlencoded";
    logger.trace("request headers: " + JSON.stringify(requestHeaders));
    return requestHeaders;
  }

  /** Get request headers object with application json content type. */
  this.getApplicationJsonHeaders = function getApplicationJsonHeaders() {
    var requestHeaders = {};
    requestHeaders.Accept = '*/*';
    requestHeaders['Content-Type'] = 'application/json';
    logger.trace("request headers: " + JSON.stringify(requestHeaders));
    return requestHeaders;
  }
}
