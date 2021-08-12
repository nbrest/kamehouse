/** Set alert messages */
function setAlertMessages() {
  const urlParams = new URLSearchParams(window.location.search);
  const error = urlParams.get('error');
  const logout = urlParams.get('logout');
  if (!isEmpty(error)) {
    const element = document.getElementById("login-alert-group-error");
    domUtils.classListRemove(element, "hidden-kh");
  }
  if (!isEmpty(logout)) {
    const element = document.getElementById("login-alert-group-logout");
    domUtils.classListRemove(element, "hidden-kh");
  }
}

window.onload = () => {
  setAlertMessages();
};