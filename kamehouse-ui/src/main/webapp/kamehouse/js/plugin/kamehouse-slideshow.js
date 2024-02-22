/**
 * Functionality to update the slides view in a slideshow.
 * 
 * Look at downloads.html for an example on how to setup the html to render the slideshow.
 * 
 * @author nbrest
 */
class KameHouseSlideshow {

  #slideIndex = 1;

  /**
   * Load kamehouse slideshow plugin.
   */
  load() {
    kameHouse.util.dom.append('head', '<link rel="stylesheet" type="text/css" href="/kame-house/kamehouse/css/plugin/kamehouse-slideshow.css">');
    kameHouse.util.module.setModuleLoaded("slideshow");
  }

  /**
   * Show previous slide.
   */
  prevSlide() {
    this.#changeSlide(-1);
  }

  /**
   * Show next slide.
   */
  nextSlide() {
    this.#changeSlide(1);
  }

  /**
   * Show the slide of the specified dot index.
   */
  setDotSlide(dotNumber) {
    this.#slideIndex = dotNumber;
    this.#updateSlide(dotNumber);
  }

  /**
   * Add or substract the specified value to the slide index and update the slide.
   */
  #changeSlide(indexValueToAdd) {
    this.#slideIndex += indexValueToAdd;
    this.#updateSlide(this.#slideIndex);
  }
  
  /**
   * Update the slide view with the specified slide index.
   */
  #updateSlide(chosenSlideIndex) {
    const slides = document.getElementsByClassName("kamehouse-slideshow-slide");
    const dots = document.getElementsByClassName("kamehouse-slideshow-dot");
    if (chosenSlideIndex > slides.length) {
      this.#slideIndex = 1;
    }
    if (chosenSlideIndex < 1) {
      this.#slideIndex = slides.length;
    }
    for (const slide of slides) {
      kameHouse.util.dom.setDisplay(slide, "none");
    }
    for (const dot of dots) {
      kameHouse.util.dom.classListRemove(dot, "kamehouse-slideshow-dot-active")
    }
    kameHouse.util.dom.setDisplay(slides[this.#slideIndex-1], "block");
    kameHouse.util.dom.classListAdd(dots[this.#slideIndex-1], "kamehouse-slideshow-dot-active");
  }
}

kameHouse.ready(() => {kameHouse.addPlugin("slideshow", new KameHouseSlideshow())});