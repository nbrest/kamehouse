/**
 * Functionality to update the slides view in a slideshow.
 * 
 * Look at downloads.html for an example on how to setup the html to render the slideshow.
 */
function KameHouseSlideshow() {

  this.load = load;
  this.prevSlide = prevSlide;
  this.nextSlide = nextSlide;
  this.setDotSlide = setDotSlide;

  let slideIndex = 1;

  function load() {
    kameHouse.util.dom.append($('head'), '<link rel="stylesheet" type="text/css" href="/kame-house/kamehouse/css/plugin/kamehouse-slideshow.css">');
    kameHouse.util.module.setModuleLoaded("slideshow");
  }

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
    slideIndex += indexValueToAdd;
    updateSlide(slideIndex);
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
    for (const slide of slides) {
      kameHouse.util.dom.setDisplay(slide, "none");
    }
    for (const dot of dots) {
      kameHouse.util.dom.classListRemove(dot, "kamehouse-slideshow-dot-active")
    }
    kameHouse.util.dom.setDisplay(slides[slideIndex-1], "block");
    kameHouse.util.dom.classListAdd(dots[slideIndex-1], "kamehouse-slideshow-dot-active");
  }
}

$(document).ready(() => {kameHouse.addPlugin("slideshow", new KameHouseSlideshow())});