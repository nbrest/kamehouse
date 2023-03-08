/**
  Official match RESALE home page check functions.

  Checks the resale home page for available tickets for the specified matches.
*/
module.exports.checkTickets = checkTickets;

const fetch = require('node-fetch');
const logger = require("nbrest-logger");
const utils = require('./utils');
const config = require('./config');

const verbose = config.VERBOSE;

const resaleHomeUrl = "https://resale-intl.fwc22.tickets.fifa.com/secure/selection/event/date/product/101397570845/lang/en";
const resaleHomeUrlLocalhost = "http://localhost/world-cup-bookings/fifa-resale-home-data.html";

function checkTickets() {
  logger.info("Checking resale home tickets");
  
  fetch(resaleHomeUrl)
    .then(res => res.text())
    .then(text => parseResaleHomePage(text))
    .catch(e => {
      logger.error("Error processing resale home: Url: " + resaleHomeUrl + ". Error :" + e);
    });
  fetch(resaleHomeUrlLocalhost)
    .then(res => res.text())
    .then(text => parseResaleHomePageLocalhost(text))
    .catch(e => {
      logger.error("Error processing resale home: Url: " + resaleHomeUrlLocalhost + ". Error :" + e);
    });
}

function parseResaleHomePage(html) {
  try {
    const document = utils.getDocument(html, "resale", verbose);
    const matchesList = document.querySelector("ul.performances_group_container").querySelectorAll("li");
    matchesList.forEach((matchListItem) => {
      parseResaleHomeMatch(matchListItem);
    });
  } catch (e) { 
    logger.error("Error processing resale home. Error :" + e);
  }
}

function parseResaleHomePageLocalhost(html) {
  try {
    const document = utils.getDocument(html, "resale", verbose);
    const matchesList = document.querySelectorAll("li");
    matchesList.forEach((matchListItem) => {
      parseResaleHomeMatch(matchListItem);
    });
  } catch (e) { 
    logger.error("Error processing resale home. Error :" + e);
  }
}

function parseResaleHomeMatch(matchListItem) {
  try {
    const id = matchListItem.id;
    if (!id) {
      return;
    } 
    if (!utils.processMatch(id)) {
      return;
    }
    logger.trace("Parsing resale home match: " + id); 
    const host = matchListItem.querySelector("span.team.home").querySelector("span.name").innerText;
    const opposite = matchListItem.querySelector("span.team.opposite").querySelector("span.name").innerText;
    const matchName = host + " VS " + opposite;
    const ticketAvailability = matchListItem.querySelector("span.availability_bullet").getAttribute("aria-label");
    const minTicketAmount = utils.getMinTicketAvailableAmount(matchListItem);
    const matchResaleUrl = "https://resale-intl.fwc22.tickets.fifa.com/secure/selection/resale/item?performanceId=" + id + "&lang=en";
    const isResaleHomeTicketAvailable = utils.isTicketAvailable(ticketAvailability);
    const sendResaleHomeEmail = utils.getSendHomePageEmail(id, minTicketAmount, "sendResaleHomeEmail");
    const matchProperties = {
      "MATCH_NAME": "\"" + matchName + "\"",
      "RESALE_HOME_MATCH_URL": "\"" + matchResaleUrl + "\"",
      "RESALE_HOME_TICKETS_AVAILABLE": isResaleHomeTicketAvailable,
      "SEND_RESALE_HOME_EMAIL": sendResaleHomeEmail
    }
    utils.writeMatchProperties(matchName, matchProperties);
  } catch (e) {
    logger.error("Error parsing resale home match entry. Error: " + e);
  }
}
