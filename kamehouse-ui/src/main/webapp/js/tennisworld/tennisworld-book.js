var bookingService;

function mainBook() {
  bannerUtils.setRandomPrinceOfTennisBanner();
  bookingService = new BookingService();
};

function BookingService() {

  this.book = book;
  this.clearBookingDetails = clearBookingDetails;
  this.clearPaymentDetails = clearPaymentDetails;
  this.togglePasswordField = togglePasswordField;

  const BOOK_API_URL = '/kame-house-tennisworld/api/v1/tennis-world/bookings';

  /**
   * Execute a booking request.
   */
  function book() {
    logger.info("Executing booking request...");
    loadingWheelModal.open("Executing booking request...");
    const bookingRequest = getFormData();
    debuggerHttpClient.post(BOOK_API_URL, bookingRequest,
      (responseBody, responseCode, responseDescription) => {
        logger.info("Booking request completed successfully");
        loadingWheelModal.close();
        updateBookingResponseTable(responseBody, responseCode);
      },
      (responseBody, responseCode, responseDescription) => {
        logger.error("Error executing booking request: " + responseBody + responseCode + responseDescription);
        loadingWheelModal.close();
        try {
          const bookingResponse = JSON.parse(responseBody);
          updateBookingResponseTable(bookingResponse, responseCode);
        } catch (error) {
          logger.error("Error parsing the response: " + error);
          domUtils.setHtml($('#brt-status'), "Error parsing response body");
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
    const dryRun = document.getElementById('dry-run').checked;
    if (!isEmpty(dryRun)) {
      bookingRequest['dryRun'] = dryRun;
    }
    const cardHolder = document.getElementById('card-holder-name').value;
    if (!isEmpty(cardHolder) && cardHolder != "") {
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
      domUtils.setAttribute(passwordField, "type", "text");
    } else {
      domUtils.setAttribute(passwordField, "type", "password");
    }
  }

  /**
   * Clear the booking details.
   */
  function clearBookingDetails() {
    logger.info("clearBookingDetails");
    document.getElementById('username').value = "";
    document.getElementById('password').value = "";
    document.getElementById('session-type').value = "";
    document.getElementById('site').value = "";
    document.getElementById('time').value = "";
    document.getElementById('date').value = "";
    document.getElementById('duration').value = "";
    document.getElementById('dry-run').checked = "";
  }

  /**
   * Clear the payment details.
   */
  function clearPaymentDetails() {
    logger.info("clearPaymentDetails");
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
      domUtils.setHtml($('#brt-response-code'), responseCode);
      domUtils.setHtml($('#brt-id'), bookingResponse.id);
      domUtils.setHtml($('#brt-status'), bookingResponse.status);
      domUtils.setHtml($('#brt-message'), bookingResponse.message);
      domUtils.setHtml($('#brt-username'), bookingResponse.username);
      domUtils.setHtml($('#brt-date'), bookingResponse.date);
      domUtils.setHtml($('#brt-time'), bookingResponse.time);
      domUtils.setHtml($('#brt-session-type'), bookingResponse.sessionType);
      domUtils.setHtml($('#brt-site'), bookingResponse.site);
      domUtils.setHtml($('#brt-duration'), bookingResponse.duration);      
  }
}

$(document).ready(mainBook);