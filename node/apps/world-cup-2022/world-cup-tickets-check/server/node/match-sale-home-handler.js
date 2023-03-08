/**
  Official match SALE home page check functions.

  Checks the sale home page for available tickets for the specified matches.
*/
module.exports.checkTickets = checkTickets;

const fetch = require('node-fetch');
const logger = require("nbrest-logger");
const utils = require('./utils');
const config = require('./config');

const verbose = config.VERBOSE;

const saleHomeUrl = "https://fcfs-intl.fwc22.tickets.fifa.com/secure/selection/event/date/product/101397570845/lang/en";
const saleHomeUrlLocalhost = "http://localhost/world-cup-bookings/fifa-sale-home-data.html";

function checkTickets() {
  logger.info("Checking sale home tickets");
 
  fetch(saleHomeUrl)
    .then(res => res.text())
    .then(text => parseSaleHomePage(text))
    .catch(e => {
      logger.error("Error processing sale home: Url: " + saleHomeUrl + ". Error :" + e);
    });
  fetch(saleHomeUrlLocalhost)
    .then(res => res.text())
    .then(text => parseSaleHomePageLocalhost(text))
    .catch(e => {
      logger.error("Error processing sale home: Url: " + saleHomeUrlLocalhost + ". Error :" + e);
    });
}

function parseSaleHomePage(html) {
  try {
    const document = utils.getDocument(html, "sale", verbose);
    const container = document.querySelector("ul.performances_group_container");
    if (utils.isEmpty(container)) {
      return;
    }
    const matchesList = container.querySelectorAll("li");
    if (utils.isEmpty(matchesList)) {
      return;
    }
    matchesList.forEach((matchListItem) => {
      parseSaleHomeMatch(matchListItem);
    });
  } catch (e) { 
    logger.error("Error processing sale home. Error :" + e);
  }
}

function parseSaleHomePageLocalhost(html) {
  try {
    const document = utils.getDocument(html, "sale", verbose);
    const matchesList = document.querySelectorAll("li");
    if (utils.isEmpty(matchesList)) {
      return;
    }
    matchesList.forEach((matchListItem) => {
      parseSaleHomeMatch(matchListItem);
    });
  } catch (e) { 
    logger.error("Error processing sale home. Error :" + e);
  }
}

function parseSaleHomeMatch(matchListItem) {
  try {
    const id = matchListItem.id;
    if (!id) {
      return;
    } 
    if (!utils.processMatch(id)) {
      return;
    }
    logger.trace("Parsing sale home match: " + id); 
    const host = matchListItem.querySelector("span.team.home").querySelector("span.name").innerText;
    const opposite = matchListItem.querySelector("span.team.opposite").querySelector("span.name").innerText;
    const matchName = host + " VS " + opposite;
    const ticketAvailability = matchListItem.querySelector("span.availability_bullet").getAttribute("aria-label");
    const minTicketAmount = utils.getMinTicketAvailableAmount(matchListItem);
    const matchSaleUrl = "https://fcfs-intl.fwc22.tickets.fifa.com/secure/selection/event/seat/performance/" + id + "/lang/en";
    const isSaleHomeTicketAvailable = utils.isTicketAvailable(ticketAvailability);
    const sendSaleHomeEmail = utils.getSendHomePageEmail(id, minTicketAmount, "sendSaleHomeEmail");
    const matchProperties = {
      "MATCH_NAME": "\"" + matchName + "\"",
      "SALE_HOME_MATCH_URL": "\"" + matchSaleUrl + "\"",
      "SALE_HOME_TICKETS_AVAILABLE": isSaleHomeTicketAvailable,
      "SEND_SALE_HOME_EMAIL": sendSaleHomeEmail
    }
    utils.writeMatchProperties(matchName, matchProperties);
  } catch (e) {
    logger.error("Error parsing sale home match entry. Error: " + e);
  }
}
