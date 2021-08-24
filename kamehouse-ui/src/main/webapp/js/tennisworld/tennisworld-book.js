var bookingService;

function mainBook() {
  bannerUtils.setRandomPrinceOfTennisBanner();
  bookingService = new BookingService();
};

function BookingService() {

  this.book = book;
  this.clear = clear;

  const BOOK_API_URL = '/kame-house-tennisworld/api/v1/tennis-world/bookings';

  function book() {
    const bookingRequest = getFormData();
    logger.info("Booking to tennisworld " + JSON.stringify(bookingRequest));
    debuggerHttpClient.post(BOOK_API_URL, bookingRequest,
      (responseBody, responseCode, responseDescription) => {
        logger.info("Booking request executed successfully");
        //TODO Update view with bookingResponse
      },
      (responseBody, responseCode, responseDescription) => {
        logger.error("Error executing booking request: " + responseBody + responseCode + responseDescription);
        basicKamehouseModal.openApiError(responseBody, responseCode, responseDescription);
        //TODO Update view with bookingResponse
      });
  }

  function getFormData() {
    const bookingRequest = {};
    bookingRequest['username'] = document.getElementById('username').value;
    bookingRequest['password'] = document.getElementById('password').value;
    bookingRequest['sessionType'] = document.getElementById('sessionType').value;
    bookingRequest['site'] = document.getElementById('site').value;
    bookingRequest['time'] = document.getElementById('time').value;
    bookingRequest['date'] = document.getElementById('date').value;
    bookingRequest['duration'] = document.getElementById('duration').value;
    const cardHolder = document.getElementById('card-holder-name').value;
    if (!isEmpty(cardHolder)) {
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

  function clear() {
    logger.info("Clear form");
  }
}

$(document).ready(mainBook);