console.log("KameHouse Chrome Extension background process");

// Execute KameHouse Chrome Extension when I click the extension button
chrome.action.onClicked.addListener(function (tab) {
  console.log("Executing kamehouse on action.onClicked");
  chrome.scripting.executeScript({
    target: {tabId: tab.id},
    files: ['kamehouse-inject.js']	
  });
  chrome.scripting.insertCSS({
    target: { tabId: tab.id },
    files: ["kamehouse-inject.css"]
  });
});

// Execute KameHouse Chrome Extension when I open a tab/page
chrome.tabs.onCreated.addListener((tab) => {
  console.log("Executing kamehouse on tabs.onCreated");
  chrome.scripting.executeScript({
    target: {tabId: tab.id},
    files: ['kamehouse-inject.js']	
  });
  chrome.scripting.insertCSS({
    target: { tabId: tab.id },
    files: ["kamehouse-inject.css"]
  });
});

// Execute KameHouse Chrome Extension when I refresh a tab/page
chrome.tabs.onUpdated.addListener((tabId, changeInfo, tab) => {
  if (changeInfo.status === "complete") {
    console.log("Executing kamehouse on tab.onUpdated");
    chrome.scripting.executeScript({
      target: {tabId: tabId},
      files: ['kamehouse-inject.js']	
    });
    chrome.scripting.insertCSS({
      target: { tabId: tabId },
      files: ["kamehouse-inject.css"]
    });
  }
});

