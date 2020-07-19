/**
 * TimeUtils utility object for manipulating time and dates.
 * 
 * Dependencies: none.
 * 
 * @author nbrest
 */
function TimeUtils() {

  /** Get current timestamp with client timezone. */
  this.getTimestamp = () => {
    let newDate = new Date();
    let offsetTime = newDate.getTimezoneOffset() * -1 * 60 * 1000;
    let currentDateTime = newDate.getTime();
    return new Date(currentDateTime + offsetTime).toISOString().replace("T", " ").slice(0, 19);
  }

  /** Convert input in seconds to hh:mm:ss output. */
  this.convertSecondsToHsMsSs = (seconds) => new Date(seconds * 1000).toISOString().substr(11, 8);
}
