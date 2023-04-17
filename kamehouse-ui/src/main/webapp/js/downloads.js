/**
 * Mobile app link page startup function.
 */
 var kameHouseSlideShowManager;

function mainMobileLinks() {
  kameHouse.util.banner.setRandomAllBanner();
  generateAndroidQrCode();
  kameHouseSlideShowManager = new KameHouseSlideShowManager();
  kameHouseSlideShowManager.setDotSlide(1);
}

function generateAndroidQrCode() {
  $(".android-app-qrcode").qrcode({
    text: 'https://kame.nicobrest.com/kame-house-mobile/kamehouse.apk',
    ecLevel: 'L',
    size: 256
  });
}

/**
 * Functionality to update the slides view in a slideshow.
 */
function KameHouseSlideShowManager() {
  let slideIndex = 1;

  this.prevSlide = prevSlide;
  this.nextSlide = nextSlide;
  this.setDotSlide = setDotSlide;

  /**
   * Show previous slide.
   */
  function prevSlide() {
    changeSlide(-1);
  }

  /**
   * Show next slide.
   */
  function nextSlide() {
    changeSlide(1);
  }

  /**
   * Add or substract the specified value to the slide index and update the slide.
   */
  function changeSlide(indexValueToAdd) {
    updateSlide(slideIndex += indexValueToAdd);
  }
  
  /**
   * Show the slide of the specified dot index.
   */
  function setDotSlide(dotNumber) {
    slideIndex = dotNumber;
    updateSlide(dotNumber);
  }
  
  /**
   * Update the slide view with the specified slide index.
   */
  function updateSlide(chosenSlideIndex) {
    const slides = document.getElementsByClassName("kamehouse-slideshow-slide");
    const dots = document.getElementsByClassName("kamehouse-slideshow-dot");
    if (chosenSlideIndex > slides.length) {
      slideIndex = 1;
    }
    if (chosenSlideIndex < 1) {
      slideIndex = slides.length;
    }
    for (let i = 0; i < slides.length; i++) {
      kameHouse.util.dom.setDisplay(slides[i], "none");
    }
    for (let i = 0; i < dots.length; i++) {
      kameHouse.util.dom.classListRemove(dots[i], "kamehouse-slideshow-dot-active")
    }
    kameHouse.util.dom.setDisplay(slides[slideIndex-1], "block");
    kameHouse.util.dom.classListAdd(dots[slideIndex-1], "kamehouse-slideshow-dot-active");
  }
}

$(document).ready(mainMobileLinks);