var scheduledBookingService;

function mainBook() {
  kameHouse.util.banner.setRandomPrinceOfTennisBanner();
  scheduledBookingService = new ScheduledBookingService();
}

function ScheduledBookingService() {

  this.triggerScheduledBookings = triggerScheduledBookings;

  const SCHEDULED_BOOKINGS_API_URL = '/kame-house-tennisworld/api/v1/tennis-world/scheduled-bookings';

  /**
   * Trigger the scheduled bookings.
   */
  function triggerScheduledBookings() {
    kameHouse.logger.info("Triggering execution of scheduled bookings...");
    kameHouse.plugin.modal.loadingWheelModal.open("Triggering execution of scheduled bookings...");
    kameHouse.plugin.debugger.http.post(SCHEDULED_BOOKINGS_API_URL, null, null,
      (responseBody, responseCode, responseDescription) => {
        kameHouse.logger.info("Scheduled bookings executed successfully");
        kameHouse.plugin.modal.loadingWheelModal.close();
        updateView(responseBody, responseCode);
      },
      (responseBody, responseCode, responseDescription) => {
        kameHouse.logger.logApiError(responseBody, responseCode, responseDescription, "Error executing scheduled bookings");
        kameHouse.plugin.modal.loadingWheelModal.close();
        updateView(responseBody, responseCode);
      });
  }

  /**
   * Update the view with the scheduled bookings response.
   */
  function updateView(responseBody, responseCode) {
    let message = kameHouse.util.time.getTimestamp() + " : ";
    console.log(responseCode);
    if (responseCode == 200 || responseCode == 201) {
      if (!kameHouse.core.isEmpty(responseBody) && !kameHouse.core.isEmpty(responseBody.length) && responseBody.length > 0) {
        message = message + "Scheduled bookings executed successfully. Check the booking responses view to see the final status of each executed booking";
      } else {
        message = message + "No scheduled bookings triggered with the current configuration"
      }
    } else {
      message = message + "Failed to execute scheduled bookings. Try again later..."
    }

    kameHouse.util.dom.setHtml($('#scheduled-bookings-status'), message);
  }
}

$(document).ready(mainBook);