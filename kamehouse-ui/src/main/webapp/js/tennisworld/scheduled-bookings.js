var scheduledBookingService;

function mainBook() {
  bannerUtils.setRandomPrinceOfTennisBanner();
  scheduledBookingService = new ScheduledBookingService();
};

function ScheduledBookingService() {

  this.triggerScheduledBookings = triggerScheduledBookings;

  const SCHEDULED_BOOKINGS_API_URL = '/kame-house-tennisworld/api/v1/tennis-world/scheduled-bookings';

  /**
   * Trigger the scheduled bookings.
   */
  function triggerScheduledBookings() {
    logger.info("Triggering execution of scheduled bookings...");
    loadingWheelModal.open("Triggering execution of scheduled bookings...");
    debuggerHttpClient.post(SCHEDULED_BOOKINGS_API_URL, null,
      (responseBody, responseCode, responseDescription) => {
        logger.info("Scheduled bookings executed successfully");
        loadingWheelModal.close();
        updateView(responseBody, responseCode);
      },
      (responseBody, responseCode, responseDescription) => {
        logger.logApiError(responseBody, responseCode, responseDescription, "Error executing scheduled bookings");
        loadingWheelModal.close();
        updateView(responseBody, responseCode);
      });
  }

  /**
   * Update the view with the scheduled bookings response.
   */
  function updateView(responseBody, responseCode) {
    let message = timeUtils.getTimestamp() + " : ";
    console.log(responseCode);
    if (responseCode == 200 || responseCode == 201) {
      if (!isEmpty(responseBody) && !isEmpty(responseBody.length) && responseBody.length > 0) {
        message = message + "Scheduled bookings executed successfully. Check the booking responses view to see the final status of each executed booking";
      } else {
        message = message + "No scheduled bookings triggered with the current configuration"
      }
    } else {
      message = message + "Failed to execute scheduled bookings. Try again later..."
    }

    domUtils.setHtml($('#scheduled-bookings-status'), message);
  }
}

$(document).ready(mainBook);