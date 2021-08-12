function mainLoginGroot() {
  const urlParams = new URLSearchParams(window.location.search);
  const referrer = urlParams.get('referrer');
  if (!isEmpty(referrer)) {
    document.getElementById('login-referrer').setAttribute('value', referrer);
  }
}

$(document).ready(mainLoginGroot);