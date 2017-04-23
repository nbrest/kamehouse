function importHeaderAndFooter(path) {

  if (path == undefined || path == null) {
    path = "";
  }
  console.log("Loading header and footer from path: " + path);

  $(function(){
    $("#headerContainer").load(path + "header.html");
  });

  $(function(){
    $("#footerContainer").load(path + "footer.html");
  });
}
