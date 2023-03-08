/**
  Official match RESALE functions.

  Checks the resale pages for each match to see if there's available matches.

  THIS CURRENTLY DOESN'T WORK. 
  FIFA requires you to be logged in to access the resale page for specific matches.
  I can only do the general resale check on the resale home page, without knowing the categories available.
*/
module.exports.checkTickets = checkTickets;

const htmlParser = require('node-html-parser');
const fetch = require('node-fetch');
const logger = require("nbrest-logger");
const utils = require('./utils');
const config = require('./config');

const matches = config.MATCHES;
const verbose = config.VERBOSE;

const resaleMaintenanceMode = "We are currently performing maintenance of the ticketing portal. Please come back later. Thank you for your understanding";
const resaleQueuePage = "Thank you for your interest in accessing the Ticket Resale Platform";
const ticketQueuePage = "Thank you for your interest in accessing the ticketing portal";
const unableToProcessRequestPage = "We are unable to process your request at the moment. Please try again later";

function checkTickets() {
  logger.info("Checking resale tickets");
  matches.forEach((match) => {
    if (match.enabled) {
      checkResaleTicketsForMatchHtml(match);
    }
  });
}

function checkResaleTicketsForMatchHtml(match) {
  const matchResaleUrl = 'https://resale-intl.fwc22.tickets.fifa.com/secure/selection/resale/item?performanceId=' + match.id + '&lang=en';
  logger.trace("Checking html resale tickets for matchResaleUrl: " + matchResaleUrl);
  fetch(matchResaleUrl)
    .then(res => res.text())
    .then(text => parseResaleMatchHtml(text, match, matchResaleUrl))
    .catch(e => {
      logger.error("Error processing resale: Url: " + matchResaleUrl + ". Error :" + e);
    });
}

function parseResaleMatchHtml(html, match, matchResaleUrl) {
  try {
    if (verbose) {
      logger.trace("http response for url " + matchResaleUrl + " : " + html);
    }
    if (isResalePageUnavailable(html)) {
      logger.debug("Match id " + match.id + " resale html page is unavailable");
      return;
    } 
    const resaleCategories = getResaleCategories();
    const document = htmlParser.parse(html);
    const host = document.querySelectorAll("p.title")[0].querySelector("span.host").innerText.trim().trimEnd();
    const opposing = document.querySelectorAll("p.title")[0].querySelector("span.opposing").innerText.trim().trimEnd();
    const matchName = host + " VS " + opposing; 
    const resaleCategoriesData = document.querySelectorAll('.seat_cat_group_end');
    utils.parseCategories(resaleCategoriesData, resaleCategories, matchName, "RESALE");  
    const isResaleTicketAvailable = utils.isTicketAvailableForMatchPage(resaleCategories);
    const sendResaleEmail = utils.sendEmailForMatchPage(resaleCategories, match);
    const matchProperties = {
      "MATCH_RESALE_URL": "\"" + matchResaleUrl + "\"",
      "RESALE_TICKETS_AVAILABLE": isResaleTicketAvailable,
      "SEND_RESALE_EMAIL": sendResaleEmail
    }
    utils.writeMatchPageProperties(matchName, matchProperties, resaleCategories);
    checkResaleTicketsForMatchJson(match, matchName);
  } catch (e) { 
    logger.error("Error processing match " + matchResaleUrl + ". Error :" + e);
    logger.trace("Match resale url could have changed, check that the matchId is still valid");
  }
}

function getResaleCategories() {
  return [
    {
      "name":"Category 1",
      "propertyName" : "RESALE_CATEGORY_1_AVAILABLE",
      "isAvailable" : "false",
    },
    {
      "name":"Category 2",
      "propertyName" : "RESALE_CATEGORY_2_AVAILABLE",
      "isAvailable" : "false",
    },
    {
      "name":"Category 3",
      "propertyName" : "RESALE_CATEGORY_3_AVAILABLE",
      "isAvailable" : "false",
    },
  ];
}

function isResalePageUnavailable(html) {
  return html.includes(resaleMaintenanceMode) || html.includes(resaleQueuePage) || html.includes(ticketQueuePage) || html.includes(unableToProcessRequestPage);
}

function checkResaleTicketsForMatchJson(match, matchName) {
  const matchResaleUrl = 'https://resale-intl.fwc22.tickets.fifa.com/selection/resale/resaleItemsWithoutSeating.json?performanceId=' + match.id + '&lang=en';
  logger.trace("Checking json resale tickets for matchResaleUrl: " + matchResaleUrl);
  fetch(matchResaleUrl)
    .then(res => res.text())
    .then(text => parseResaleMatchJson(text, match, matchResaleUrl, matchName));
}

function parseResaleMatchJson(html, match, matchResaleUrl, matchName) {
  try {
    if (verbose) {
      logger.trace("http response for url " + matchResaleUrl + " : " + html);
    }
    if (isResalePageUnavailable(html)) {
      logger.info("Match " + matchName + " resale json page is unavailable");
      return;
    }
    const resaleCategories = getResaleCategories();
    parseResaleCategoriesJson(html, resaleCategories, matchName); 
    const isResaleTicketAvailable = utils.isTicketAvailableForMatchPage(resaleCategories);
    const sendResaleEmail = utils.sendEmailForMatchPage(resaleCategories, match);
    const matchProperties = {
      "MATCH_RESALE_URL": "\"" + matchResaleUrl + "\"",
      "RESALE_TICKETS_AVAILABLE": isResaleTicketAvailable,
      "SEND_RESALE_EMAIL": sendResaleEmail
    }
    // This should override the html properties written in the file when sourced on bash as they will be added to the end of the file
    utils.writeMatchPageProperties(matchName, matchProperties, resaleCategories);
  } catch (e) { 
    logger.error("Error processing match " + matchResaleUrl + ". Error :" + e);
    logger.trace("Match resale url could have changed, check that the matchId is still valid");
  }
}

function parseResaleCategoriesJson(json, resaleCategories, matchName) {
  const resaleCategoriesData = JSON.parse(json);
  const resaleItems = resaleCategoriesData.resaleItems;
  resaleItems.forEach((resaleItem) => {
    resaleCategories.forEach((category) => {
      if (resaleItem.seatCatName.includes(category.name)) {
        logger.debug("For match '" + matchName + "' category '" + category.name + "' might be available on RESALE. Checking if there are tickets");
        if (resaleItem.quantity > 0) {
          logger.info("For match '" + matchName + "' There are RESALE tickets for category '" + category.name + "'");
          category.isAvailable = "true";
        } else {
          category.isAvailable = "false";
        }
      }
    });
  });
}