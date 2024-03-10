/**
 * Functionality to display all banners in kamehouse.
 * 
 * @author nbrest
 */
class TestBannerRenderer {

  static #TBODY_ID = "banners-table-body";

  /**
   * Load the extension.
   */
  load() {
    kameHouse.logger.info("Loading TestBannerRenderer");
    kameHouse.util.banner.setRandomAllBanner();
    this.#setBannerCategoriesDropdown();
  }

  /**
   * Reload all banners from the selected banners list.
   */
  reloadBanners() {
    kameHouse.logger.info("Reloading banners");
    const bannersTbody = document.getElementById(TestBannerRenderer.#TBODY_ID);
    kameHouse.util.dom.empty(bannersTbody);
    const bannerCategory = this.#getSelectedBannerCategory();
    const selectedBanners = kameHouse.util.banner.getBanners(bannerCategory);
    for (const bannerName of selectedBanners) {
      kameHouse.util.dom.append(bannersTbody, this.#getBannerImage(bannerCategory, bannerName));
    }
  }

  /**
   * Set banner categories dropdown.
   */
  #setBannerCategoriesDropdown() {
    const bannerCategoryDropdown = document.getElementById("banner-category-dropdown");
    kameHouse.util.dom.empty(bannerCategoryDropdown);
    const bannerCategories = kameHouse.util.banner.getBannerCategories();
    kameHouse.util.dom.append(bannerCategoryDropdown, kameHouse.util.dom.getOption({
      value: ""
    }, "Banner Category"));
    bannerCategories.forEach((bannerCategory) => {
      kameHouse.util.dom.append(bannerCategoryDropdown, this.#getBannerCategoryOption(bannerCategory));
    });
  }

  /**
   * Get banner category option.
   */
  #getBannerCategoryOption(bannerCategory) {
    return kameHouse.util.dom.getOption({
      value: bannerCategory
    }, bannerCategory);
  }

  /**
   * Get selected banner category.
   */
  #getSelectedBannerCategory() {
    const bannerCategoryDropdown = document.getElementById('banner-category-dropdown');
    return bannerCategoryDropdown.options[bannerCategoryDropdown.selectedIndex].value;
  }

  /**
   * Get banner image.
   */
  #getBannerImage(bannerCategory, bannerName) {
    return kameHouse.util.dom.getImg({
      src: '/kame-house/img/banners/' + bannerCategory + '/' + bannerName + '.jpg',
      className: "banners-table-entry",
      alt: "Banner Table Entry"
    });
  }
}

kameHouse.ready(() => {
  kameHouse.addExtension("testBannerRenderer", new TestBannerRenderer());
});
