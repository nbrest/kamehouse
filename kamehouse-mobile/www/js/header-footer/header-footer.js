/**
 * Header and Footer functions for mobile.
 * 
 * Dependencies: logger, httpClient.
 * 
 * @author nbrest
 */
const header = new Header();
const footer = new Footer();

/**
 * Render header and footer.
 */
function renderHeaderAndFooter() {
  logger.info("Started initializing header and footer");
  header.renderHeader();
  footer.renderFooter();
}

/** Footer functionality */
function Footer() {

  this.renderFooter = renderFooter;

  /** Renders the footer */
  function renderFooter() { 
    domUtils.append($('head'), '<link rel="stylesheet" type="text/css" href="/kame-house/css/header-footer/footer.css">');
    domUtils.append($("body"), getFooterContainerDiv());
    domUtils.load($("#footerContainer"), "/html-snippets/footer.html", () => {
      setAppVersion();
      setGitCommitHash();
      setBuildDate();
    });
  }

  function getFooterContainerDiv() {
    return domUtils.getDiv({
      id: "footerContainer"
    });
  }

  async function setAppVersion() {
    const pom = await fetchUtils.loadHtmlSnippet('/pom.xml');
    const versionPrefix = "<version>";
    const versionSuffix = "-KAMEHOUSE-SNAPSHOT";
    const tempVersion = pom.slice(pom.indexOf(versionPrefix) + versionPrefix.length);
    const appVersion = tempVersion.slice(0, tempVersion.indexOf(versionSuffix));
    logger.info("app version: " + appVersion);
    const footerBuildVersion = document.getElementById("footer-build-version");
    domUtils.setInnerHtml(footerBuildVersion, appVersion);
  }

  async function setGitCommitHash() {
    const gitHash = await fetchUtils.loadHtmlSnippet('/git-commit-hash.txt');
    logger.info("git hash: " + gitHash);
    const gitHashDiv = document.getElementById("footer-git-hash");
    domUtils.setInnerHtml(gitHashDiv, gitHash);
  }

  async function setBuildDate() {
    const buildDate = await fetchUtils.loadHtmlSnippet('/build-date.txt');
    logger.info("build date: " + buildDate);
    const buildDateDiv = document.getElementById("footer-build-date");
    domUtils.setInnerHtml(buildDateDiv, buildDate);
  }
}

/** Header functionality */
function Header() {

  this.renderHeader = renderHeader;

  /** Render the header */
  function renderHeader() {
    domUtils.append($('head'), '<link rel="stylesheet" type="text/css" href="/kame-house/css/header-footer/header.css">');
    domUtils.prepend($("body"), getHeaderContainerDiv());
    domUtils.load($("#headerContainer"), "/html-snippets/header.html");
  }

  /**
   * Get header container.
   */
  function getHeaderContainerDiv() {
    return domUtils.getDiv({
      id: "headerContainer"
    });
  }
}