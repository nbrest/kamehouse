<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width">
<meta name="description" content="kame-house">
<meta name="keywords" content="kame-house nicobrest nbrest">
<meta name="author" content="nbrest">

<title>kameHouse - Contact Us</title>
<link rel="icon" type="img/ico" href="img/favicon.ico" />
<link rel="stylesheet" href="lib/css/bootstrap.min.css" />
<link rel="stylesheet" href="css/global.css" /> 
<link rel="stylesheet" href="css/contact-us.css" />
</head>
<body>
  <div id="headerContainer"></div>
  <div class="main-body">
      <div class="default-layout">
          <h3 class="h3-kh txt-l-kh">Contact Us</h3>
          <div id="contact-us-form">
            <form>
              <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
              <label>Name</label> 
              <input class="form-control form-input" type="text" placeholder="Name"> 
              <label>Email</label> 
              <input class="form-control" type="email" placeholder="Email Address"> 
              <label>Message</label>
              <textarea class="form-control" placeholder="Message..."></textarea> 
              <br>
              <button class="btn btn-block btn-outline-info btn-borderless" id="submit" type=submit onclick="siteUnderCostructionAlert()">Submit</button>
            </form>
          </div>
      </div> 
    <br>
    <div id="contact-us-newsletter-wrapper">
      <div id="newsletter"></div>
    </div>
  </div>
  <div id="footerContainer"></div>
  <script src="lib/js/jquery-2.0.3.min.js"></script>
  <script src="js/global.js"></script>
  <script src="js/header-footer/headerFooter.js"></script>
  <script src="js/snippets/newsletter.js"></script>
</body>
</html>
