var bookingService;

function mainBook() {
  bannerUtils.setRandomPrinceOfTennisBanner();
  bookingService = new BookingService();
};

function BookingService() {

  this.book = book;
  this.clear = clear;
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
        updateBookingResponseTable(responseBody, responseCode);
        loadingWheelModal.close();
      },
      (responseBody, responseCode, responseDescription) => {
        logger.error("Error executing booking request: " + responseBody + responseCode + responseDescription);
        updateBookingResponseTable(responseBody, responseCode);
        loadingWheelModal.close();
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
      cardDetails['number'] = document.getElementById('card-holder-name').value;
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
   * Clear the form.
   */
  function clear() {
    logger.info("Clear form");
    document.getElementById('username').value = "";
    document.getElementById('password').value = "";
    document.getElementById('session-type').value = "";
    document.getElementById('site').value = "";
    document.getElementById('time').value = "";
    document.getElementById('date').value = "";
    document.getElementById('duration').value = "";
    document.getElementById('dry-run').checked = "";
    document.getElementById('card-holder-name').value = "";
    document.getElementById('card-exp-month').value = "";
    document.getElementById('card-exp-year').value = "";
    document.getElementById('card-cvv').value = "";
  }

  /**
   * Update the view with the booking response.
   */
  function updateBookingResponseTable(responseBody, responseCode) {
    const bookingResponse = JSON.parse(responseBody);
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