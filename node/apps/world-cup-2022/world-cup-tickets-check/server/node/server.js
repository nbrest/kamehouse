/**
  Checks if there are any available tickets for some World Cup 2022 matches

  When running with the chrome extension to overcome the browser validation page:

  - Run apache httpd locally with kamehouse-webserver root
  - Install world-cup-bookings chrome extension
  - Open chrome with 2 tabs:
    - https://fcfs-intl.fwc22.tickets.fifa.com/secure/selection/event/date/product/101397570845/lang/en
    - https://resale-intl.fwc22.tickets.fifa.com/secure/selection/event/date/product/101397570845/lang/en
  - Check that the pages are refreshing every few seconds. Then the chrome extension is working
  - Run in the background world-cup-bookings.sh script to keep checking for tickets

  Run with `npm start` at the root of the project

  Requirements: Run on the root of the node app
  - npm install --save node-html-parser
  - npm install --save node-fetch
  - npm install --save nbrest-logger
*/
const logger = require("nbrest-logger");
const matchSaleHandler = require('./match-sale-handler');
const matchResaleHandler = require('./match-resale-handler');
const matchSaleHomeHandler = require('./match-sale-home-handler');
const matchResaleHomeHandler = require('./match-resale-home-handler');

function main() {
  logger.info("Starting world cup tickets check node app");
  matchSaleHandler.checkTickets();
  matchResaleHandler.checkTickets();
  matchSaleHomeHandler.checkTickets();
  matchResaleHomeHandler.checkTickets();
}

main();