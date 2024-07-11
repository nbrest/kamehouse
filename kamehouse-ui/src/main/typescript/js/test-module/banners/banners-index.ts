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
    kameHouse.logger.info("Loading TestBannerRenderer", null);
    this.#setBannerCategoriesDropdown();
  }

  /**
   * Reload all banners from the selected banners list.
   */
  reloadBanners() {
    kameHouse.logger.info("Reloading banners", null);
    const bannersTbody = document.getElementById(TestBannerRenderer.#TBODY_ID);
    kameHouse.util.dom.empty(bannersTbody);
    const bannerCategory = this.#getSelectedBannerCategory();
    const selectedBanners = kameHouse.util.banner.getBanners(bannerCategory);
    for (const bannerName of selectedBanners) {
      const tr = kameHouse.util.dom.getTrTd(null);
      kameHouse.util.dom.append(tr, this.#getBannerHeader(bannerName));
      kameHouse.util.dom.append(tr, this.#getBannerButton(bannerCategory, bannerName));
      kameHouse.util.dom.append(tr, kameHouse.util.dom.getBr());
      kameHouse.util.dom.append(tr, kameHouse.util.dom.getBr());
      kameHouse.util.dom.append(tr, kameHouse.util.dom.getBr());
      kameHouse.util.dom.append(bannersTbody, tr);
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
    const bannerCategoryDropdown = document.getElementById('banner-category-dropdown') as HTMLSelectElement;
    return bannerCategoryDropdown.options[bannerCategoryDropdown.selectedIndex].value;
  }

  /**
   * Get banner header.
   */
  #getBannerHeader(bannerName) {
    return kameHouse.util.dom.getDiv({
      class: "banners-table-title"
    }, bannerName);
  }

  /**
   * Get banner button.
   */
  #getBannerButton(bannerCategory, bannerName) {
    const button = kameHouse.util.dom.getButton({
      attr: {
        class: "banners-table-btn",
      },
      mobileClass: null,
      backgroundImg: '/kame-house/img/banners/' + bannerCategory + '/' + bannerName + '.jpg',
      html: null,
      data: {
        bannerName: bannerName
      },
      click: (event, data) => this.#setBanner(event, data)
    });
    kameHouse.util.dom.setStyle(button, "background-size", "cover");
    return button;
  }

  /**
   * Set selected banner.
   */
  #setBanner(event, data) {
    kameHouse.util.dom.classListAddById("banner", "fade-in-out-15s");
    kameHouse.util.banner.setBanner(data.bannerName);
    kameHouse.util.dom.setHtmlById("banner-name", data.bannerName);
    kameHouse.core.backToTop();
  }
}

kameHouse.ready(() => {
  kameHouse.addExtension("testBannerRenderer", new TestBannerRenderer());
});
