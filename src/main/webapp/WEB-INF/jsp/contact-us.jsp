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
<link rel="stylesheet" href="css/general.css" />
<link rel="stylesheet" href="css/header.css" />
<link rel="stylesheet" href="css/footer.css" />
</head>
<body>
  <div id="headerContainer"></div>
  <div class="main">
    <section id="main">
      <div class="container">
        <article id="main-article">
          <h3 class="page-title">Contact Us</h3>
          <div class="lighter">
            <form class="quote">
              <div>
                <label>Name</label>
                <br>
                <input type="text" placeholder="Name">
              </div>
              <div>
                <label>Email</label>
                <br>
                <input type="email" placeholder="Email Address">
              </div>
              <div>
                <label>Message</label>
                <br>
                <textarea placeholder="Message.."></textarea>
              </div>
              <button class="btn btn-outline-info" type=submit onclick="siteUnderCostructionAlert()">Submit</button>
            </form>
          </div>
      </div>
    </section>
    <br>
    <section id="newsletter"></section>
  </div>
  <div id="footerContainer"></div>
  <script src="lib/js/jquery-2.0.3.min.js"></script>
  <script src="js/general.js"></script>
  <script src="js/importHeaderFooter.js"></script>
  <script type="text/javascript">importHeaderAndFooter("html/", "${requestScope.username}")
  </script>
  <script type="text/javascript">importNewsletter("html/")
  </script>
</body>
</html>
