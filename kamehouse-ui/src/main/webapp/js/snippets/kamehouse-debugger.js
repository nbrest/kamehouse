var kameHouseDebugger;

function main() {
  kameHouseDebugger = new KameHouseDebugger();
  kameHouseDebugger.renderDebugMode();
}

/** 
 * Handles the debugger functionality.
 * 
 * @author nbrest
 */
function KameHouseDebugger() {

  /** 
   * Toggle debug mode. 
   */
  this.toggleDebugMode = () => {
    logger.debug("Toggled debug mode")
    let debugModeDiv = document.getElementById("debug-mode");
    debugModeDiv.classList.toggle("hidden-kh");
  }
  
  /**
   * Render debug mode div and it's button.
   */
  this.renderDebugMode = () => {
    $("#debug-mode-button-wrapper").load("/kame-house/html-snippets/kamehouse-debugger-button.html");
    $("#debug-mode-wrapper").load("/kame-house/html-snippets/kamehouse-debugger.html", () => {
      moduleUtils.setModuleLoaded("kameHouseDebugger");
    });
  }

  /**
   * Render the specified html snippet into the custom div of the debugger.
   */
  this.renderCustomDebugger = (htmlSnippet) => {
    $("#debug-mode-custom-wrapper").load(htmlSnippet);
  }
}

/**
 * Call main.
 */
$(document).ready(main);