/**
  Official match SALE functions.

  Checks the official match sale pages for available tickets.
*/
module.exports.checkTickets = checkTickets;

const htmlParser = require('node-html-parser');
const fetch = require('node-fetch');
const logger = require("nbrest-logger");
const utils = require('./utils');
const config = require('./config');

const matches = config.MATCHES;
const verbose = config.VERBOSE;

function checkTickets() {
  logger.info("Checking sale tickets");
  matches.forEach((match) => {
    if (match.enabled) {
      checkTicketsForMatch(match);
    }
  });
}

function checkTicketsForMatch(match) {
  const matchUrl = 'https://fcfs-intl.fwc22.tickets.fifa.com/secure/selection/event/seat/performance/' + match.id + '/lang/en';
  logger.trace("Checking tickets for matchUrl: " + matchUrl);
  fetch(matchUrl, {
      'headers': {
          'accept': '*/*',
          'cookie': ''
      }
    })
    .then(res => res.text())
    .then(text => parseMatch(text, match, matchUrl))
    .catch(e => {
      logger.error("Error processing sale: Url: " + matchUrl + ". Error :" + e);
    });
}

function parseMatch(html, match, matchUrl) {
  try {
    if (verbose) {
      logger.trace("http response for url " + matchUrl + " : " + html);
    }
    const categories = getCategories();
    const document = htmlParser.parse(html);
    const host = document.querySelectorAll("p.title")[0].querySelector("span.host").innerText.trim().trimEnd();
    const opposing = document.querySelectorAll("p.title")[0].querySelector("span.opposing").innerText.trim().trimEnd();
    const matchName = host + " VS " + opposing; 
    const categoriesData = document.querySelectorAll('.seat_category_end');
    utils.parseCategories(categoriesData, categories, matchName, "SALE");  
    const isTicketAvailable = utils.isTicketAvailableForMatchPage(categories);
    const sendEmail = utils.sendEmailForMatchPage(categories, match);
    const matchProperties = {
      "MATCH_NAME": "\"" + matchName + "\"",
      "MATCH_URL": "\"" + matchUrl + "\"",
      "TICKETS_AVAILABLE": isTicketAvailable,
      "SEND_EMAIL": sendEmail
    }
    utils.writeMatchPageProperties(matchName, matchProperties, categories);
  } catch (e) {
    logger.error("Error processing match " + matchUrl + ". Error :" + e);
    logger.trace("Match url could have changed, check that the matchId is still valid");
  }
}

function getCategories() {
  return [
    {
      "name":"Category 1",
      "propertyName" : "CATEGORY_1_AVAILABLE",
      "isAvailable" : "false",
    },
    {
      "name":"Category 2",
      "propertyName" : "CATEGORY_2_AVAILABLE",
      "isAvailable" : "false",
    },
    {
      "name":"Category 3",
      "propertyName" : "CATEGORY_3_AVAILABLE",
      "isAvailable" : "false",
    },
  ];
}

