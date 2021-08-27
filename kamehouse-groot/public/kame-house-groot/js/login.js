function mainLoginGroot() {
  const urlParams = new URLSearchParams(window.location.search);
  const referrer = urlParams.get('referrer');
  if (!isEmpty(referrer)) {
    domUtils.setValue(document.getElementById('login-referrer'), referrer);
  }
}

$(document).ready(mainLoginGroot);