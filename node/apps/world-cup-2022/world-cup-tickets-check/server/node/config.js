/**
  Config objects shared by different handlers.
*/
module.exports.VERBOSE = isVerboseEnabled();
module.exports.MATCHES = [
  {
    id: "101437163856", //Senegal VS Netherlands
    categoriesToEmail: [],
    sendSaleHomeEmail: false,
    sendResaleHomeEmail: false,
    sendEmailForNonWheelChairTickets: false,
    enabled: false
  },
  {
    id: "101437163855", //Qatar VS Ecuador
    categoriesToEmail: ["Category 1", "Category 2", "Category 3"],
    sendSaleHomeEmail: true,
    sendResaleHomeEmail: true,
    sendEmailForNonWheelChairTickets: true,
    enabled: true
  },
  {
    id: "101437163862", //Argentina VS Saudi Arabia
    categoriesToEmail: [],
    sendSaleHomeEmail: false,
    sendResaleHomeEmail: false,
    sendEmailForNonWheelChairTickets: false,
    enabled: true
  },
  {
    id: "101437163861", //Mexico VS Poland
    categoriesToEmail: [],
    sendSaleHomeEmail: false,
    sendResaleHomeEmail: false,
    sendEmailForNonWheelChairTickets: false,
    enabled: true
  },
  {
    id: "101437163859", //France VS Australia
    categoriesToEmail: [],
    sendSaleHomeEmail: false,
    sendResaleHomeEmail: false,
    sendEmailForNonWheelChairTickets: false,
    enabled: true
  },
  {
    id: "101437163865", //Germany VS Japan
    categoriesToEmail: [],
    sendSaleHomeEmail: false,
    sendResaleHomeEmail: false,
    sendEmailForNonWheelChairTickets: false,
    enabled: true
  },
  {
    id: "101437163864", //Spain VS Costa Rica
    categoriesToEmail: [],
    sendSaleHomeEmail: false,
    sendResaleHomeEmail: false,
    sendEmailForNonWheelChairTickets: false,
    enabled: true
  },
  {
    id: "101437163868", //Uruguay VS Korea Republic
    categoriesToEmail: [],
    sendSaleHomeEmail: false,
    sendResaleHomeEmail: false,
    sendEmailForNonWheelChairTickets: false,
    enabled: false
  },
  {
    id: "101437163869", //Portugal VS Ghana
    categoriesToEmail: [],
    sendSaleHomeEmail: false,
    sendResaleHomeEmail: false,
    sendEmailForNonWheelChairTickets: false,
    enabled: true
  },
  {
    id: "101437163870", //Brazil VS Serbia
    categoriesToEmail: [],
    sendSaleHomeEmail: false,
    sendResaleHomeEmail: false,
    sendEmailForNonWheelChairTickets: false,
    enabled: true
  },
  {
    id: "101437163891", //Australia VS Denmark
    categoriesToEmail: [],
    sendSaleHomeEmail: false,
    sendResaleHomeEmail: false,
    sendEmailForNonWheelChairTickets: false,
    enabled: true
  }
];

function isVerboseEnabled() {
  const cmdArgs = process.argv.slice(2);
  if (isEmpty(cmdArgs)) {
    return false;
  }
  let verbose = false;
  cmdArgs.forEach((cmdArg) => {
    const cmdArgArray = cmdArg.split("=");
    const cmdArgKey = cmdArgArray[0];
    const cmdArgValue = cmdArgArray[1];
    if (!isEmpty(cmdArgKey) && cmdArgKey.toLowerCase().trim() == "verbose") {
      if (!isEmpty(cmdArgValue) && cmdArgValue.toLowerCase().trim() == "true" ) {
        verbose = true;
      }
    }
  });
  return verbose;
}

/** Checks if a variable is undefined or null. */
function isEmpty(val) {
  return val === undefined || val == null;
}