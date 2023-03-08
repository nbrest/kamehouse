/**
  Common functions shared throught the code.
*/
module.exports.isEmpty = isEmpty;
module.exports.getTimestamp = getTimestamp;
module.exports.getMatchFileName = getMatchFileName;
module.exports.writePropertyToFile = writePropertyToFile;
module.exports.writeHeaderToFile = writeHeaderToFile;
module.exports.writeMatchProperties = writeMatchProperties;
module.exports.writeMatchPageProperties = writeMatchPageProperties;
module.exports.getMinTicketAvailableAmount = getMinTicketAvailableAmount;
module.exports.isTicketAvailable = isTicketAvailable;
module.exports.processMatch = processMatch;
module.exports.getSendHomePageEmail = getSendHomePageEmail;
module.exports.isTicketAvailableForMatchPage = isTicketAvailableForMatchPage;
module.exports.sendEmailForMatchPage = sendEmailForMatchPage;
module.exports.parseCategories = parseCategories;
module.exports.getDocument = getDocument;

const fs = require('fs');
const os = require("os");
const htmlParser = require('node-html-parser');
const logger = require("nbrest-logger");
const config = require('./config');

const matches = config.MATCHES;

const userHomeDir = os.homedir();
const outputDataDir = userHomeDir + "/my.scripts/data/world-cup-tickets-check/";

function getMatchFileName(matchName) {
  return matchName.toUpperCase().replace(/ /g, '_');
}

function writePropertyToFile(matchFileName, propertyName, propertyValue) {
  try {
    const content = propertyName + "=" + propertyValue + '\n\n';
    fs.appendFileSync(outputDataDir + matchFileName + ".data", content);
  } catch (err) {
    console.log(err);
  }
}

function writeHeaderToFile(matchFileName, header) {
  try {
    const content = "# " + header + '\n\n';
    fs.appendFileSync(outputDataDir + matchFileName + ".data", content);
  } catch (err) {
    console.log(err);
  }
}

function writeMatchProperties(matchName, matchProperties) {
  logger.trace("matchProperties : " + JSON.stringify(matchProperties));
  const matchFileName = getMatchFileName(matchName);
  for (const [key, value] of Object.entries(matchProperties)) {
    writePropertyToFile(matchFileName, key, value);
  }
}

function getTimestamp() {
  const date = new Date();
  const offsetTime = date.getTimezoneOffset() * -1 * 60 * 1000;
  return new Date(date.getTime() + offsetTime).toISOString().replace("T", " ").slice(0, 19);
}


function getMinTicketAvailableAmount(matchListItem) {
  const minTicketAmount = matchListItem.querySelector("span.int_part");
  if (isEmpty(minTicketAmount)) {
    return 0;
  }
  return minTicketAmount.innerText.replace(/ /g,'');
}

function isTicketAvailable(ticketAvailability) {
  logger.trace("ticketAvailability: " + ticketAvailability);
  if (isEmpty(ticketAvailability)) {
    return false;
  }
  return ticketAvailability.toLowerCase().includes("tickets available") || 
    ticketAvailability.toLowerCase().includes("low availability");
}

function isEmpty(val) {
  return val === undefined || val == null;
}

function processMatch(id) {
  let processMatch = false;
  matches.forEach((match) => {
    if (match.id == id && match.enabled) {
      processMatch = true;
    }
  });
  return processMatch;
}

function getSendHomePageEmail(id, minTicketAmount, sendEmailMatchProperty) {
  let sendHomePageEmail = false;
  matches.forEach((match) => {
    if (match.id == id) {
      sendHomePageEmail = match[sendEmailMatchProperty];
      logger.trace("match[sendEmailMatchProperty] " + match[sendEmailMatchProperty] + " " + match.id);
      const minAmountOverLimit = minTicketAmount > 201;
      if (match.sendEmailForNonWheelChairTickets && minAmountOverLimit) {
        sendHomePageEmail = true; 
      }
    }
  });
  return sendHomePageEmail;
}

function isTicketAvailableForMatchPage(categories) {
  let isTicketAvailable = "false";
  categories.forEach((category) => {
    if (category.isAvailable == "true") {
      isTicketAvailable = "true";
    }
  });
  return isTicketAvailable;
}

function sendEmailForMatchPage(categories, match) {
  let sendEmail = "false";
  categories.forEach((category) => {
    if (category.isAvailable == "true") {
      match.categoriesToEmail.forEach((categoryToEmail) => {
        if (categoryToEmail == category.name) {
          sendEmail = "true";
        }
      })
    }
  });
  return sendEmail;
}

function writeMatchPageProperties(matchName, matchProperties, categories) {
  const matchFileName = getMatchFileName(matchName);
  writeHeaderToFile(matchFileName, getTimestamp() + " - " + matchName + " match status:");
  writeMatchProperties(matchFileName, matchProperties);
  categories.forEach((category) => {
    writePropertyToFile(matchFileName, category.propertyName, category.isAvailable);
  });
}

function parseCategories(categoriesData, categories, matchName, saleType) {
  categoriesData.forEach((categoryTr) => {
    const categoryTh = categoryTr.querySelectorAll('th')[0];
    if (categoryTh) {
      if (categoryTh.innerText) {
        const categoryName = categoryTh.innerText.trim();
        categories.forEach((category) => {
          if (categoryName.includes(category.name)) {
            logger.debug("For match '" + matchName + "' category '" + category.name + "' " + saleType + " tickets might be available. Checking if there are tickets");
            const quantity = categoryTr.querySelector(".quantity");
            if (quantity) {
              const select = quantity.querySelector("select");
              if (select) {
                const options = select.querySelectorAll("option");
                if (options != null && options != undefined && options.length > 0) {
                  logger.info("For match '" + matchName + "' There are tickets for category '" + category.name + "' on " + saleType);
                  category.isAvailable = "true";
                } else {
                  category.isAvailable = "false";
                }
              }
            }
          }
        });
      }
    }
  });
}

function getDocument(html, saleType, verbose) {
  if (verbose) {
    logger.trace("http response for " + saleType + "  home page: " + html);
  }
  return htmlParser.parse(html);
}