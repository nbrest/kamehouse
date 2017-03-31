function importHeaderAndFooter(path) {

  console.log("Loading header and footer.");

  if (path == undefined || path == null) {
    path = "";
  }

  $(function(){
    $("#headerContainer").load(path + "header.html");
  });

  $(function(){
    $("#footerContainer").load(path + "footer.html");
  });
}
