function ScheduledBookingService() {

  this.load = load;
  this.triggerScheduledBookings = triggerScheduledBookings;

  const SCHEDULED_BOOKINGS_API_URL = '/kame-house-tennisworld/api/v1/tennis-world/scheduled-bookings';

  function load() {
    kameHouse.logger.info("Loading ScheduledBookingService");
    kameHouse.util.banner.setRandomPrinceOfTennisBanner();
  }

  /**
   * Trigger the scheduled bookings.
   */
  function triggerScheduledBookings() {
    kameHouse.logger.info("Triggering execution of scheduled bookings...");
    kameHouse.plugin.modal.loadingWheelModal.open("Triggering execution of scheduled bookings...");
    kameHouse.plugin.debugger.http.post(SCHEDULED_BOOKINGS_API_URL, null, null,
      (responseBody, responseCode, responseDescription, responseHeaders) => {
        kameHouse.logger.info("Scheduled bookings executed successfully");
        kameHouse.plugin.modal.loadingWheelModal.close();
        updateView(responseBody, responseCode);
      },
      (responseBody, responseCode, responseDescription, responseHeaders) => {
        kameHouse.logger.logApiError(responseBody, responseCode, responseDescription, responseHeaders, "Error executing scheduled bookings");
        kameHouse.plugin.modal.loadingWheelModal.close();
        updateView(responseBody, responseCode);
      });
  }

  /**
   * Update the view with the scheduled bookings response.
   */
  function updateView(responseBody, responseCode) {
    let message = kameHouse.util.time.getTimestamp() + " : ";
    kameHouse.logger.debug("Response code " + responseCode);
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

$(document).ready(() => {kameHouse.addExtension("scheduledBookingService", new ScheduledBookingService())});