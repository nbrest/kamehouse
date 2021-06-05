/** Set alert messages */
function setAlertMessages() {
  const urlParams = new URLSearchParams(window.location.search);
  const error = urlParams.get('error');
  const logout = urlParams.get('logout');
  if (!isNullOrUndefined(error)) {
    let element = document.getElementById("login-alert-group-error");
    element.classList.remove("hidden-kh");
  }
  if (!isNullOrUndefined(logout)) {
    let element = document.getElementById("login-alert-group-logout");
    element.classList.remove("hidden-kh");
  }
}

window.onload = () => {
  setAlertMessages();
};