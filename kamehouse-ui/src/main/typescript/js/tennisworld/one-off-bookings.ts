/**
 * Execute one of bookings on tennisworld. 
 * 
 * @author nbrest
 */
class BookingService {

  #BOOK_API_URL = '/kame-house-tennisworld/api/v1/tennis-world/bookings';

  /**
   * Load the extension.
   */
  load() {
    kameHouse.logger.info("Loading BookingService", null);
    kameHouse.util.banner.setRandomAllBanner(null);
  }

  /**
   * Execute a booking request.
   */
  book() {
    kameHouse.logger.info("Executing booking request...", null);
    kameHouse.plugin.modal.loadingWheelModal.open("Executing booking request. This could take a few minutes...");
    const bookingRequest = this.#getBookingRequest();
    const config = kameHouse.http.getConfig();
    config.timeout = 300;
    kameHouse.plugin.debugger.http.post(config, this.#BOOK_API_URL, kameHouse.http.getApplicationJsonHeaders(), bookingRequest,
      (responseBody, responseCode, responseDescription, responseHeaders) => {
        kameHouse.logger.info("Booking request completed successfully", null);
        kameHouse.plugin.modal.loadingWheelModal.close();
        this.#updateBookingResponseTable(responseBody, responseCode);
      },
      (responseBody, responseCode, responseDescription, responseHeaders) => {
        kameHouse.plugin.modal.loadingWheelModal.close();
        try {
          this.#updateBookingResponseTable(kameHouse.json.parse(responseBody), responseCode);
        } catch (error) {
          kameHouse.logger.error("Error parsing the response: " + error, null);
          kameHouse.util.dom.setHtmlById('brt-status', "Error parsing response body");
        }
        kameHouse.logger.error("Error executing the booking request", null);
      });
  }

  /**
   * Clear the booking details.
   */
  clearBookingDetails() {
    kameHouse.logger.info("clearBookingDetails", null);
    (document.getElementById('username') as HTMLInputElement).value = "";
    (document.getElementById('password') as HTMLInputElement).value = "";
    (document.getElementById('session-type') as HTMLInputElement).value = "";
    (document.getElementById('site') as HTMLInputElement).value = "";
    (document.getElementById('time') as HTMLInputElement).value = "";
    (document.getElementById('date') as HTMLInputElement).value = "";
    (document.getElementById('duration') as HTMLInputElement).value = "";
    (document.getElementById('court-number') as HTMLInputElement).value = "";
    (document.getElementById('dry-run') as HTMLInputElement).checked = null;
  }

  /**
   * Clear the payment details.
   */
  clearPaymentDetails() {
    kameHouse.logger.info("clearPaymentDetails", null);
    (document.getElementById('card-holder-name') as HTMLInputElement).value = "";
    (document.getElementById('card-number-1') as HTMLInputElement).value = "";
    (document.getElementById('card-number-2') as HTMLInputElement).value = "";
    (document.getElementById('card-number-3') as HTMLInputElement).value = "";
    (document.getElementById('card-number-4') as HTMLInputElement).value = "";
    (document.getElementById('card-exp-month') as HTMLInputElement).value = "";
    (document.getElementById('card-exp-year') as HTMLInputElement).value = "";
    (document.getElementById('card-cvv') as HTMLInputElement).value = "";
  }

  /**
   * Show/hide masked fields.
   */
  togglePasswordField(fieldId) {
    const passwordField = document.getElementById(fieldId) as HTMLInputElement;
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
    bookingRequest['username'] = (document.getElementById('username') as HTMLInputElement).value;
    bookingRequest['password'] = (document.getElementById('password') as HTMLInputElement).value;
    bookingRequest['sessionType'] = (document.getElementById('session-type') as HTMLInputElement).value;
    bookingRequest['site'] = (document.getElementById('site') as HTMLInputElement).value;
    bookingRequest['time'] = (document.getElementById('time') as HTMLInputElement).value;
    bookingRequest['date'] = (document.getElementById('date') as HTMLInputElement).value;
    bookingRequest['duration'] = (document.getElementById('duration') as HTMLInputElement).value;
    bookingRequest['courtNumber'] = (document.getElementById('court-number') as HTMLInputElement).value;
    const dryRun = (document.getElementById('dry-run') as HTMLInputElement).checked;
    if (!kameHouse.core.isEmpty(dryRun)) {
      bookingRequest['dryRun'] = dryRun;
    }
    const cardHolder = (document.getElementById('card-holder-name') as HTMLInputElement).value;
    if (!kameHouse.core.isEmpty(cardHolder)) {
      const cardDetails = {};
      cardDetails['name'] = cardHolder;
      const cardNumber = (document.getElementById('card-number-1') as HTMLInputElement).value + "" + (document.getElementById('card-number-2') as HTMLInputElement).value + "" + (document.getElementById('card-number-3') as HTMLInputElement).value + "" + (document.getElementById('card-number-4') as HTMLInputElement).value;
      cardDetails['number'] = cardNumber;
      const expiryDate = (document.getElementById('card-exp-month') as HTMLInputElement).value + "/" + (document.getElementById('card-exp-year') as HTMLInputElement).value;
      cardDetails['expiryDate'] = expiryDate;
      cardDetails['cvv'] = (document.getElementById('card-cvv') as HTMLInputElement).value;
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