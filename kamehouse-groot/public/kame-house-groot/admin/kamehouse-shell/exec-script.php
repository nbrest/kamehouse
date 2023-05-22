<?php require_once("../../api/v1/auth/authorize-admin-page.php") ?>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width">
<meta name="author" content="nbrest">
<meta name="description" content="kame-house application">
<meta name="keywords" content="kame-house nicobrest nbrest">
<meta name="mobile-web-app-capable" content="yes">

<title>GRoot - KameHouse Shell Exec Script</title>

<link rel="shortcut icon" href="/kame-house-groot/favicon.ico" type="image/x-icon" />
<script src="/cordova.js"></script>
<script src="/kame-house/lib/js/jquery.js"></script>
<script src="/kame-house/kamehouse/js/kamehouse.js" id="kamehouse-data" data-authorized-roles="ROLE_KAMISAMA"></script>
<script src="/kame-house-groot/kamehouse-groot/js/kamehouse-groot.js"></script>
<link rel="stylesheet" href="/kame-house/lib/css/bootstrap.min.css" />
<link rel="stylesheet" href="/kame-house/kamehouse/css/kamehouse.css" />
<link rel="stylesheet" href="/kame-house-groot/kamehouse-groot/css/kamehouse-groot.css" />
<link rel="stylesheet" href="/kame-house-groot/css/admin/kamehouse-shell.css" />
</head>
<body>
  <div class="banner-wrapper">
    <div id="banner" class="fade-in-out-15s banner-ikki">
      <div class="default-layout banner-text">
        <h1>Execute</h1>
        <div id="banner-server-name"></div>
        <br>
        <div id="banner-script-status">not running</div>
      </div>
    </div>
  </div>
  <div id="groot-menu-wrapper"></div>
  <div class="default-layout">

    <div class="groot-image-info-wrapper">
      <table class="info-image-table info-image-table-reverse">
        <caption class="hidden-kh">Image-Info</caption>
        <thead class="hidden-kh"><tr><th>Image-Info</th></tr></thead>
        <tbody>
          <tr>
            <td class="info-image-img">
              <img src="/kame-house/img/dbz/roshi-goku-krillin-fight.jpg" alt="info image"/>
            </td>
            <td class="info-image-info">
              <div class="info-image-title">
                Execute Shell Script
              </div>
              <div class="info-image-desc">
                <p>Runs the specified script from <span class="bold-kh">KameHouse Shell</span> in the current server server and displays the console output</p>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <table id="script-table"
      class="table-kh">
      <tr class="table-kh-header">
        <td>Script Name:</td>
        <td id="st-script-name"></td>
      </tr>
      <tr>
        <td>Script Args:</td>
        <td id="st-script-args"></td>
      </tr>
      <tr>
        <td>Server Name:</td>
        <td id="st-server-name"></td>
      </tr>
      <tr>
        <td>Execution Start Date:</td>
        <td id="st-script-exec-start-date"></td>
      </tr>
      <tr>
        <td>Execution End Date:</td>
        <td id="st-script-exec-end-date"></td>
      </tr>
    </table>
    <br>
    <h4 id="kamehouse-shell-output-header" class="p-15-d-kh">Shell Script Output</h4>
    <br>
    <!-- pre and the divs need to be in the same line or it prints some extra lines -->
    <pre id="kamehouse-shell-output-executing-wrapper" class="kamehouse-shell-output-executing-wrapper hidden-kh"><div id="kamehouse-shell-output-executing" class="txt-c-d-kh txt-c-m-kh"></div><br><div class="txt-c-d-kh txt-c-m-kh">Please wait...</div><div class="spinning-wheel"></div></pre>

    <!-- pre and table need to be in the same line or it prints some extra lines -->
    <pre id="kamehouse-shell-output" class="kamehouse-shell-output"><table class="kamehouse-shell-output-table">
        <caption class="hidden-kh">Shell Script Output</caption>
        <tr class="hidden-kh">
          <th scope="row">Shell Script Output</th>
        </tr>
        <tbody id="kamehouse-shell-output-table-body">
          <tr><td>No script executed yet...</td></tr>
        </tbody>
      </table></pre>
    
    <div class="p-15-d-kh p-15-m-kh"></div>

    <img id="btn-execute-script"
      class="img-btn-kh m-50-d-l-kh m-50-m-l-kh m-25-d-r-kh m-25-m-l-kh"
      onclick="kameHouse.extension.execScriptLoader.executeFromUrlParams()" src="/kame-house/img/mplayer/play.png"
      alt="Execute Script" title="Execute Script" />
    <img id="btn-download-kamehouse-shell-output" class="img-btn-kh hidden-kh"
      onclick="kameHouse.extension.execScriptLoader.downloadBashScriptOutput()"
      src="/kame-house/img/other/download-blue.png" alt="Download Output"
      title="Download Output" />

  </div>
  <span id="debug-mode-wrapper"></span>
  <script src="/kame-house-groot/kamehouse-groot/js/kamehouse-shell.js"></script>
  <script src="/kame-house-groot/js/admin/kamehouse-shell/exec-script.js"></script>
</body>

</html>