console.log("KameHouse Chrome Extension Script");

function main() {
  setGlobalVariables();
  //createAutoCloseableModal();
  //testFunctionLoop();
}

function setGlobalVariables() {
  if (!window.kameHouseGlobal) {
    console.log("Setting KameHouse global variables");
    window.kameHouseGlobal = 1;
  } else {
    console.log("KameHouse global variables already set");
  }  
}

function testFunctionLoop() {
  let count = 1;
  while (count <= 5) {
    setTimeout(testFunction, 5000 + count * 5000);
    count = count + 1;
  }
}

function testFunction() {
  console.log(getTimestamp() + " : window.kameHouseGlobal " + window.kameHouseGlobal);
}

function createAutoCloseableModal() {
  //scrollToTop();
  console.log("Creating KameHouse Chrome extension modal");
  const modal = document.createElement("div");
  modal.setAttribute("id", "kamehouse-chrome-extension-div");
  document.body.appendChild(modal);
  modal.innerHTML = getTimestamp() + " - KameHouse Chrome Extension is running<br><br>Auto closing in 5 seconds";
  setTimeout(()=> {
    console.log("Hiding KameHouse Chrome extension modal")
    modal.style.display = "none";
  }, 5000);
}

function getTimestamp() {
  const date = new Date();
  const offsetTime = date.getTimezoneOffset() * -1 * 60 * 1000;
  const currentDateTime = date.getTime();
  return new Date(currentDateTime + offsetTime).toISOString().replace("T"," ").slice(0,19);
}

function scrollToTop() {
  document.body.scrollTop = document.documentElement.scrollTop = 0;
}

/**
 * Execute existing function in page:
(function() {
  location.href="javascript:someExistingFunctionInPage(); void 0";
})();
 */

main();