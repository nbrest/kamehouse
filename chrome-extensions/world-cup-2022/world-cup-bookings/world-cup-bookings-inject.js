/**
 * World Cup Bookings Chrome Extension inject script.
 * 
 * @author nbrest
 */
function main() {
  if (isFifaWebsite()) {
    console.log("WorldCupBookings Chrome Extension Script");
  } else {
    console.log("WorldCupBookings Chrome Extension Script - Not on fifa website, exiting");
    return;
  }
  setTimeout(savePageData, 5000);
}

function savePageData() {
  if (isResaleSite()) {
    saveResaleHomePageData(); 
  } else {
    saveSaleHomePageData()
  }
}

function saveResaleHomePageData() {
  const url = "http://localhost/world-cup-bookings/fifa-resale-home-processor.php";
  saveHomePageData(url, "RESALE")
}

function saveSaleHomePageData() {
  const url = "http://localhost/world-cup-bookings/fifa-sale-home-processor.php";
  saveHomePageData(url, "SALE")
}

function saveHomePageData(url, saleType) {
  const matchesList = document.querySelector("ul.performances_group_container");
  if (isEmpty(matchesList)) {
    console.log("ERROR: Can't find match data in the page");
    reloadPage();
  }
  try {
    const matchesListData = matchesList.innerHTML;
    console.log("Sending " + saleType + " home page data to: " + url);
    fetch(url, {
      method: 'post',
      body: matchesListData
    }).then((response) => {
      return response.text();
    }).then((data) => {
      console.log("Received response from: " + url);
      reloadPage();
    });
  } catch (e) {
    console.log("ERROR: Sending data to " + url + ". Error: " + e);
    reloadPage();
  }
}

function reloadPage() {
  console.log("Reloading page");
  setTimeout(() => {
    location.reload();
  }, 5000);
}

function isFifaWebsite() {
  return location.host.includes("fifa");
}

function isResaleSite() {
  return location.host.includes("resale-");
}

function isEmpty(val) {
  return val === undefined || val == null;
}

main();