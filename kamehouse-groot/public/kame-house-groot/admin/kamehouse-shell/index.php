<?php require_once("../../api/v1/auth/authorize-page.php") ?>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width">
<meta name="author" content="nbrest">
<meta name="description" content="kame-house application">
<meta name="keywords" content="kame-house nicobrest nbrest">
<meta name="mobile-web-app-capable" content="yes">

<title>GRoot - KameHouse Shell</title>

<link rel="shortcut icon" href="/kame-house-groot/favicon.ico" type="image/x-icon" />
<script src="/cordova.js"></script>
<script src="/kame-house/lib/js/jquery.js"></script>
<script src="/kame-house/kamehouse/js/kamehouse.js"></script>
<script src="/kame-house-groot/kamehouse-groot/js/kamehouse-groot.js"></script>
<link rel="stylesheet" href="/kame-house/lib/css/bootstrap.min.css" />
<link rel="stylesheet" href="/kame-house/kamehouse/css/kamehouse.css" />
<link rel="stylesheet" href="/kame-house-groot/kamehouse-groot/css/kamehouse-groot.css" />
<link rel="stylesheet" href="/kame-house-groot/css/admin/kamehouse-shell.css" />
</head>
<body>
  <div id="groot-menu-wrapper" onmouseover="header.showGrootMenu()" onmouseleave="header.hideGrootMenu()"></div>
  <div class="banner-wrapper">
  <div id="banner" class="fade-in-out-15s banner-goku-ssj4-earth">
    <div class="default-layout banner-text">
      <h1>KameHouse Shell</h1>
      <div id="banner-server-name"></div>
    </div>
  </div>  
  </div>
  <div class="default-layout">
  <br>
  <h3>All Scripts</h3>
  <br>
  <p class="p-15-m-kh">Lists the scripts from kamehouse-shell that can be executed in this server</p>
  <div class="default-layout p-7-d-kh p-7-m-kh"></div>
  <div class="default-layout bg-darker-1-kh border-gray-dark-kh">
    <table id="all-kamehouse-shell-table-controls" class="all-kamehouse-shell-table-controls-table">
      <caption class="hidden-kh">All KameHouse Shell Controls</caption>
      <colgroup>
        <col class="w-80-pc-kh" />
        <col class="w-20-pc-kh" />
      </colgroup>
      <tr class="hidden-kh">
        <th scope="row" class="hidden-kh">All KameHouse Shell Controls</th>
      </tr>
      <tr>
        <td>
          <input class="table-kh-filter-input"
            type="text" placeholder="Search..."
            onkeyup="kameHouseShellManager.filterKameHouseShellRows(this.value)" />
        </td>
        <td class="txt-r-d-kh txt-r-m-kh">
          <img class="img-btn-kh img-btn-s-kh btn-kamehouse-shell-controls"
            onclick="scrollToTopOfDiv('all-kamehouse-shell-table-wrapper')"
            src="/kame-house/img/other/back-to-top.png"
            alt="Back To Top" title="Back To Top" />
        </td>
      </tr> 
    </table>  

  <div id="all-kamehouse-shell-table-wrapper" class="kamehouse-shell-wrapper">
    <table id="all-kamehouse-shell-table" class="kamehouse-shell-table">
      <caption class="hidden-kh">All KameHouse Shell</caption>
      <tr class="hidden-kh">
        <th scope="row">All KameHouse Shell</th>
      </tr>
      <tbody id="all-kamehouse-shell-table-body"></tbody>
    </table>
  </div>
  </div>
  <script src="/kame-house/kamehouse/js/kamehouse-modal.js"></script>
  <script src="/kame-house-groot/js/admin/kamehouse-shell/kamehouse-shell-index.js"></script>
</body>
</html>
