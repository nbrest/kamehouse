/**
 * Execute scheduled bookings on tennisworld. 
 * 
 * @author nbrest
 */
class ScheduledBookingService {

  #SCHEDULED_BOOKINGS_API_URL = '/kame-house-tennisworld/api/v1/tennis-world/scheduled-bookings';

  /**
   * Load the extension.
   */  
  load() {
    kameHouse.logger.info("Loading ScheduledBookingService", null);
    kameHouse.util.banner.setRandomAllBanner(null);
  }

  /**
   * Trigger the scheduled bookings.
   */
  triggerScheduledBookings() {
    kameHouse.logger.info("Triggering execution of scheduled bookings...", null);
    kameHouse.plugin.modal.loadingWheelModal.open("Triggering execution of scheduled bookings...");
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.post(config, this.#SCHEDULED_BOOKINGS_API_URL, null, null,
      (responseBody, responseCode, responseDescription, responseHeaders) => {
        kameHouse.logger.info("Scheduled bookings executed successfully", null);
        kameHouse.plugin.modal.loadingWheelModal.close();
        this.#updateView(responseBody, responseCode);
      },
      (responseBody, responseCode, responseDescription, responseHeaders) => {
        kameHouse.plugin.modal.loadingWheelModal.close();
        this.#updateView(responseBody, responseCode);
        kameHouse.logger.error("Error executing scheduled bookings", null);
      });
  }

  /**
   * Update the view with the scheduled bookings response.
   */
  #updateView(responseBody, responseCode) {
    let message = kameHouse.util.time.getTimestamp(null) + " : ";
    kameHouse.logger.debug("Response code " + responseCode, null);
    if (responseCode == 200 || responseCode == 201) {
      if (!kameHouse.core.isEmpty(responseBody) && !kameHouse.core.isEmpty(responseBody.length) && responseBody.length > 0) {
        message = message + "Scheduled bookings executed successfully. Check the booking responses view to see the final status of each executed booking";
      } else {
        message = message + "No scheduled bookings triggered with the current configuration"
      }
    } else {
      message = message + "Failed to execute scheduled bookings. Try again later..."
    }

    kameHouse.util.dom.setHtmlById('scheduled-bookings-status', message);
  }
}

kameHouse.ready(() => {kameHouse.addExtension("scheduledBookingService", new ScheduledBookingService())});