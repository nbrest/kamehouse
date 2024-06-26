/**
 * Execute one of bookings on tennisworld. 
 * 
 * @author nbrest
 */
class BookingService {

  static #BOOK_API_URL = '/kame-house-tennisworld/api/v1/tennis-world/bookings';

  /**
   * Load the extension.
   */
  load() {
    kameHouse.logger.info("Loading BookingService");
    kameHouse.util.banner.setRandomAllBanner();
  }

  /**
   * Execute a booking request.
   */
  book() {
    kameHouse.logger.info("Executing booking request...");
    kameHouse.plugin.modal.loadingWheelModal.open("Executing booking request. This could take a few minutes...");
    const bookingRequest = this.#getBookingRequest();
    const config = kameHouse.http.getConfig();
    config.timeout = 300;
    kameHouse.plugin.debugger.http.post(config, BookingService.#BOOK_API_URL, kameHouse.http.getApplicationJsonHeaders(), bookingRequest,
      (responseBody, responseCode, responseDescription, responseHeaders) => {
        kameHouse.logger.info("Booking request completed successfully");
        kameHouse.plugin.modal.loadingWheelModal.close();
        this.#updateBookingResponseTable(responseBody, responseCode);
      },
      (responseBody, responseCode, responseDescription, responseHeaders) => {
        kameHouse.plugin.modal.loadingWheelModal.close();
        try {
          this.#updateBookingResponseTable(kameHouse.json.parse(responseBody), responseCode);
        } catch (error) {
          kameHouse.logger.error("Error parsing the response: " + error);
          kameHouse.util.dom.setHtmlById('brt-status', "Error parsing response body");
        }
        kameHouse.logger.error("Error executing the booking request");
      });
  }

  /**
   * Clear the booking details.
   */
  clearBookingDetails() {
    kameHouse.logger.info("clearBookingDetails");
    document.getElementById('username').value = "";
    document.getElementById('password').value = "";
    document.getElementById('session-type').value = "";
    document.getElementById('site').value = "";
    document.getElementById('time').value = "";
    document.getElementById('date').value = "";
    document.getElementById('duration').value = "";
    document.getElementById('court-number').value = "";
    document.getElementById('dry-run').checked = "";
  }

  /**
   * Clear the payment details.
   */
  clearPaymentDetails() {
    kameHouse.logger.info("clearPaymentDetails");
    document.getElementById('card-holder-name').value = "";
    document.getElementById('card-number-1').value = "";
    document.getElementById('card-number-2').value = "";
    document.getElementById('card-number-3').value = "";
    document.getElementById('card-number-4').value = "";
    document.getElementById('card-exp-month').value = "";
    document.getElementById('card-exp-year').value = "";
    document.getElementById('card-cvv').value = "";
  }

  /**
   * Show/hide masked fields.
   */
  togglePasswordField(fieldId) {
    const passwordField = document.getElementById(fieldId);
    if (passwordField.type === "password") {
      kameHouse.util.dom.setAttribute(passwordField, "type", "text");
    } else {
      kameHouse.util.dom.setAttribute(passwordField, "type", "password");
    }
  }

  /**
   * Get booking request to send to the backend.
   */
  #getBookingRequest() {
    const bookingRequest = {};
    bookingRequest['username'] = document.getElementById('username').value;
    bookingRequest['password'] = document.getElementById('password').value;
    bookingRequest['sessionType'] = document.getElementById('session-type').value;
    bookingRequest['site'] = document.getElementById('site').value;
    bookingRequest['time'] = document.getElementById('time').value;
    bookingRequest['date'] = document.getElementById('date').value;
    bookingRequest['duration'] = document.getElementById('duration').value;
    bookingRequest['courtNumber'] = document.getElementById('court-number').value;
    const dryRun = document.getElementById('dry-run').checked;
    if (!kameHouse.core.isEmpty(dryRun)) {
      bookingRequest['dryRun'] = dryRun;
    }
    const cardHolder = document.getElementById('card-holder-name').value;
    if (!kameHouse.core.isEmpty(cardHolder)) {
      const cardDetails = {};
      cardDetails['name'] = cardHolder;
      const cardNumber = document.getElementById('card-number-1').value + "" + document.getElementById('card-number-2').value + "" + document.getElementById('card-number-3').value + "" + document.getElementById('card-number-4').value;
      cardDetails['number'] = cardNumber;
      const expiryDate = document.getElementById('card-exp-month').value + "/" + document.getElementById('card-exp-year').value;
      cardDetails['expiryDate'] = expiryDate;
      cardDetails['cvv'] = document.getElementById('card-cvv').value;
      bookingRequest['cardDetails'] = cardDetails;
    }
    bookingRequest['retries'] = 0;
    return bookingRequest;
  }

  /**
   * Update the view with the booking response.
   */
  #updateBookingResponseTable(bookingResponse, responseCode) {
    kameHouse.util.dom.classListRemoveById("brt", "hidden-kh");
    kameHouse.util.dom.setHtmlById('brt-response-code', responseCode);
    kameHouse.util.dom.setHtmlById('brt-response-id', bookingResponse.id);
    kameHouse.util.dom.setHtmlById('brt-status', bookingResponse.status);
    kameHouse.util.dom.setHtmlById('brt-message', bookingResponse.message);
    const bookingRequest = bookingResponse.request;
    if (!kameHouse.core.isEmpty(bookingRequest)) {
      kameHouse.util.dom.setHtmlById('brt-request-id', bookingRequest.id);
      kameHouse.util.dom.setHtmlById('brt-username', bookingRequest.username);
      const date = kameHouse.util.time.getDateFromEpoch(bookingRequest.date);
      kameHouse.util.dom.setHtmlById('brt-date', date.toLocaleDateString());
      kameHouse.util.dom.setHtmlById('brt-time', bookingRequest.time);
      kameHouse.util.dom.setHtmlById('brt-session-type', bookingRequest.sessionType);
      kameHouse.util.dom.setHtmlById('brt-site', bookingRequest.site);
      kameHouse.util.dom.setHtmlById('brt-duration', bookingRequest.duration);   
      kameHouse.util.dom.setHtmlById('brt-court-number', bookingRequest.courtNumber);    
      const creationDate = kameHouse.util.time.getDateFromEpoch(bookingRequest.creationDate);
      kameHouse.util.dom.setHtmlById('brt-creation-date', creationDate.toLocaleString());   
    }
  }
}

kameHouse.ready(() => {
  kameHouse.addExtension("bookingService", new BookingService());
});