/** Set a random image from the banner classes list */
function setRandomBanner() {
  let bannerClasses = ["banner-fuego-12-casas", "banner-sanctuary", "banner-goku-ssj4-earth"];
  let randomBannerIndex = Math.floor(Math.random() * bannerClasses.length);
  let element = document.getElementById("banner");
  bannerClasses.forEach((bannerClass) => {
    element.classList.remove(bannerClass);
  });
  element.classList.add(bannerClasses[randomBannerIndex]);
}

window.onload = () => {
  setRandomBanner();
};
