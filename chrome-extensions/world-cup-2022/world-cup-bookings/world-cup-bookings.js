/**
 * Chrome Extension to execute on fifa world cup 2022 ticket sale and resale home pages.
 * It loads the list of matches and posts the content to my localhost apache/php server.
 * The apache/php server then stores the content into a file and is processed by the
 * world-cup-tickets-check.sh script.
 * 
 * @author nbrest
 */
console.log("world-cup-bookings Chrome Extension background process");

// Execute world-cup-bookings Chrome Extension when I click the extension button
chrome.action.onClicked.addListener(function (tab) {
  console.log("Executing world-cup-bookings on action.onClicked");
  chrome.scripting.executeScript({
    target: {tabId: tab.id},
    files: ['world-cup-bookings-inject.js']	
  });
  chrome.scripting.insertCSS({
    target: { tabId: tab.id },
    files: ["world-cup-bookings-inject.css"]
  });
});

// Execute world-cup-bookings Chrome Extension when I open a tab/page
chrome.tabs.onCreated.addListener((tab) => {
  console.log("Executing world-cup-bookings on tabs.onCreated");
  chrome.scripting.executeScript({
    target: {tabId: tab.id},
    files: ['world-cup-bookings-inject.js']	
  });
  chrome.scripting.insertCSS({
    target: { tabId: tab.id },
    files: ["world-cup-bookings-inject.css"]
  });
});

// Execute world-cup-bookings Chrome Extension when I refresh a tab/page
chrome.tabs.onUpdated.addListener((tabId, changeInfo, tab) => {
  if (changeInfo.status === "complete") {
    console.log("Executing world-cup-bookings on tab.onUpdated");
    chrome.scripting.executeScript({
      target: {tabId: tabId},
      files: ['world-cup-bookings-inject.js']	
    });
    chrome.scripting.insertCSS({
      target: { tabId: tabId },
      files: ["world-cup-bookings-inject.css"]
    });
  }
});

