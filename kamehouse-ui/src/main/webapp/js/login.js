/** Set alert messages */
$(document).ready(() => {
  const urlParams = new URLSearchParams(window.location.search);
  const unauthorizedPageAccess = urlParams.get('unauthorizedPageAccess');
  const error = urlParams.get('error');
  const logout = urlParams.get('logout');
  if (!kameHouse.core.isEmpty(unauthorizedPageAccess)) {
    const element = document.getElementById("login-alert-group-unauthorized");
    kameHouse.util.dom.classListRemove(element, "hidden-kh");
  }
  if (!kameHouse.core.isEmpty(error)) {
    const element = document.getElementById("login-alert-group-error");
    kameHouse.util.dom.classListRemove(element, "hidden-kh");
  }
  if (!kameHouse.core.isEmpty(logout)) {
    const element = document.getElementById("login-alert-group-logout");
    kameHouse.util.dom.classListRemove(element, "hidden-kh");
  }
});