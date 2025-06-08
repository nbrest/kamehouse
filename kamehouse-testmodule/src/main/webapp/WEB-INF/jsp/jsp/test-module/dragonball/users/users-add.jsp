<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page session="true"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta name="viewport" content="width=device-width">
<meta name="author" content="nbrest">
<meta name="mobile-web-app-capable" content="yes">

<title>Add DragonBallUser Form</title>
<link rel="icon" type="img/ico" href="/kame-house/img/favicon.ico" />
<script src="/cordova.js"></script>
<script src="/kame-house/lib/js/jquery.js"></script>
<script src="/kame-house/kamehouse/js/kamehouse.js" id="kamehouse-data" data-authorized-roles="ROLE_SAIYAJIN"></script>
<script src="/kame-house/js/jsp/test-module/dragonball/users/dragonball-user-service-jsp.js"></script>
<script src="/kame-house/js/jsp/test-module/dragonball/users/dragonball-users-add.js"></script>
<link rel="stylesheet" href="/kame-house/lib/css/bootstrap.min.css" />
<link rel="stylesheet" href="/kame-house/kamehouse/css/kamehouse.css" />
<link rel="stylesheet" href="/kame-house/css/test-module/test-module.css" />
</head>
<body> 
  <div class="main-body">
    <div class="banner-wrapper">
      <div id="banner" class="fade-in-out-15s banner-gohan-ssj2-4">
        <div class="default-layout banner-text">
          <h1>ドラゴンボールユーザーを追加</h1>
          <p>Add Dragonball User JSP</p>
        </div>
      </div>
    </div>
    <div class="default-layout">
      <div class="info-image-wrapper-m-80-60">
        <table class="info-image-table">
          <caption class="hidden-kh">Image-Info</caption>
          <thead class="hidden-kh"><tr><th>Image-Info</th></tr></thead>
          <tbody>
            <tr>
              <td class="info-image-img">
                <img src="/kame-house/img/dbz/shen-long-dragonballs.jpg" alt="info img"/>
              </td>
              <td class="info-image-info">
                <div class="info-image-title">
                  Raise your Ki to the limit
                </div>
                <div class="info-image-desc">
                  <p>Join Goku and Bulma to find the 7 dragonballs and make your wishes come true with Shen Long</p>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
    <div class="default-layout p-15-m-kh">
      <h3 class="h3-kh txt-l-d-kh txt-l-m-kh">Add DragonBall User</h3>
      <br>
      <form class="form-kh form-in-page-kh">
        <label for="input-username">Username</label>
        <input type="text" class="form-input-kh" id="input-username" value="user" required/>
        <br>

        <label for="input-email">Email</label>
        <input type="email" class="form-input-kh" id="input-email" value="user@dbz.com" required/>
        <br>

        <label for="input-age">Age</label>
        <input type="text" class="form-input-kh" id="input-age" value="1"/>
        <br>

        <label for="input-powerLevel">Power Level</label>
        <input type="text" class="form-input-kh" id="input-powerLevel" value="2"/>
        <br>

        <label for="input-stamina">Stamina</label>
        <input type="text" class="form-input-kh" id="input-stamina" value="3"/>
        <br>

        <div class="form-submit-wrapper-kh">
          <button class="img-btn-kh m-15-d-r-kh" 
            onclick="kameHouse.extension.dragonBallUserServiceJsp.addDragonBallUser()" 
            data-background-img="/kame-house/img/other/check-gray-dark.png"></button>
          <button class="img-btn-kh fl-r-d-kh" 
            onclick="kameHouse.core.windowLocationHref('users-list')"
            data-background-img="/kame-house/img/other/list-bullet-gray-dark.png"></button>
        </div>
      </form>
      <span id="debug-mode-wrapper"></span>
    </div>
  </div>
</body>
</html>
