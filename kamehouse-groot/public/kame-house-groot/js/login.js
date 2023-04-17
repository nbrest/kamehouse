function mainLoginGroot() {
  const urlParams = new URLSearchParams(window.location.search);
  const referrer = urlParams.get('referrer');
  if (!kameHouse.core.isEmpty(referrer)) {
    kameHouse.util.dom.setValue(document.getElementById('login-referrer'), referrer);
  }
}

$(document).ready(mainLoginGroot);