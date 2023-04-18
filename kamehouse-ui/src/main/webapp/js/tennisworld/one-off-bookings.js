function BookingService() {

  this.load = load;
  this.book = book;
  this.clearBookingDetails = clearBookingDetails;
  this.clearPaymentDetails = clearPaymentDetails;
  this.togglePasswordField = togglePasswordField;

  const BOOK_API_URL = '/kame-house-tennisworld/api/v1/tennis-world/bookings';

  function load() {
    kameHouse.logger.info("Loading BookingService");
    kameHouse.util.banner.setRandomPrinceOfTennisBanner();
  }

  /**
   * Execute a booking request.
   */
  function book() {
    kameHouse.logger.info("Executing booking request...");
    kameHouse.plugin.modal.loadingWheelModal.open("Executing booking request...");
    const bookingRequest = getFormData();
    kameHouse.plugin.debugger.http.post(BOOK_API_URL, kameHouse.http.getApplicationJsonHeaders(), bookingRequest,
      (responseBody, responseCode, responseDescription) => {
        kameHouse.logger.info("Booking request completed successfully");
        kameHouse.plugin.modal.loadingWheelModal.close();
        updateBookingResponseTable(responseBody, responseCode);
      },
      (responseBody, responseCode, responseDescription) => {
        kameHouse.logger.logApiError(responseBody, responseCode, responseDescription, "Error executing booking request");
        kameHouse.plugin.modal.loadingWheelModal.close();
        try {
          const bookingResponse = JSON.parse(responseBody);
          updateBookingResponseTable(bookingResponse, responseCode);
        } catch (error) {
          kameHouse.logger.error("Error parsing the response: " + error);
          kameHouse.util.dom.setHtml($('#brt-status'), "Error parsing response body");
        }
      });
  }

  /**
   * Get booking request to send to the backend.
   */
  function getFormData() {
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
    return bookingRequest;
  }

  /**
   * Show/hide masked fields.
   */
  function togglePasswordField(fieldId) {
    const passwordField = document.getElementById(fieldId);
    if (passwordField.type === "password") {
      kameHouse.util.dom.setAttribute(passwordField, "type", "text");
    } else {
      kameHouse.util.dom.setAttribute(passwordField, "type", "password");
    }
  }

  /**
   * Clear the booking details.
   */
  function clearBookingDetails() {
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
  function clearPaymentDetails() {
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
   * Update the view with the booking response.
   */
  function updateBookingResponseTable(bookingResponse, responseCode) {
      kameHouse.util.dom.setHtml($('#brt-response-code'), responseCode);
      kameHouse.util.dom.setHtml($('#brt-response-id'), bookingResponse.id);
      kameHouse.util.dom.setHtml($('#brt-status'), bookingResponse.status);
      kameHouse.util.dom.setHtml($('#brt-message'), bookingResponse.message);
      kameHouse.util.dom.setHtml($('#brt-request-id'), bookingResponse.request.id);
      kameHouse.util.dom.setHtml($('#brt-username'), bookingResponse.request.username);
      const date = kameHouse.util.time.getDateFromEpoch(bookingResponse.request.date);
      kameHouse.util.dom.setHtml($('#brt-date'), date.toLocaleDateString());
      kameHouse.util.dom.setHtml($('#brt-time'), bookingResponse.request.time);
      kameHouse.util.dom.setHtml($('#brt-session-type'), bookingResponse.request.sessionType);
      kameHouse.util.dom.setHtml($('#brt-site'), bookingResponse.request.site);
      kameHouse.util.dom.setHtml($('#brt-duration'), bookingResponse.request.duration);   
      kameHouse.util.dom.setHtml($('#brt-court-number'), bookingResponse.request.courtNumber);    
      const creationDate = kameHouse.util.time.getDateFromEpoch(bookingResponse.request.creationDate);
      kameHouse.util.dom.setHtml($('#brt-creation-date'), creationDate.toLocaleString());   
  }
}

$(document).ready(() => {
  kameHouse.addExtension("bookingService", new BookingService());
});