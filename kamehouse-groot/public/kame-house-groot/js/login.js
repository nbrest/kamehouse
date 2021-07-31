function mainLoginGroot() {
  const urlParams = new URLSearchParams(window.location.search);
  const referrer = urlParams.get('referrer');
  if (referrer) {
    document.getElementById('login-referrer').setAttribute('value', referrer);
  }
}

$(document).ready(mainLoginGroot);