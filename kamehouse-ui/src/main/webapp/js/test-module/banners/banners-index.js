/**
 * Functionality to display all banners in kamehouse.
 * 
 * @author nbrest
 */
function TestBannerRenderer() {

  this.load = load;
  this.reloadBanners = reloadBanners;

  const TBODY_ID = "banners-table-body";
  let selectedBanners = [];

  function load() {
    kameHouse.logger.info("Loading TestBannerRenderer");
    kameHouse.util.banner.setRandomAllBanner();
    setBannerCategoriesDropdown();
  }

  function setBannerCategoriesDropdown() {
    const bannerCategoryDropdown = $("#banner-category-dropdown");
    kameHouse.util.dom.empty(bannerCategoryDropdown);
    const bannerCategories = kameHouse.util.banner.getBannerCategories();
    kameHouse.util.dom.append(bannerCategoryDropdown, kameHouse.util.dom.getOption({
      value: ""
    }, "Select banner category"));
    bannerCategories.forEach((bannerCategory) => {
      kameHouse.util.dom.append(bannerCategoryDropdown, getBannerCategoryOption(bannerCategory));
    });
  }

  function getBannerCategoryOption(bannerCategory) {
    return kameHouse.util.dom.getOption({
      value: bannerCategory
    }, bannerCategory);
  }

  /**
   * Reload all banners from the selected banners list.
   */
  function reloadBanners() {
    kameHouse.logger.info("Reloading banners");
    const bannersTbody = $('#' + TBODY_ID);
    kameHouse.util.dom.empty(bannersTbody);
    bannerCategory = getSelectedBannerCategory();
    selectedBanners = kameHouse.util.banner.getBanners(bannerCategory);
    for (const bannerName of selectedBanners) {
      kameHouse.util.dom.append(bannersTbody, getBannerImage(bannerCategory, bannerName));
    }
  }

  function getSelectedBannerCategory() {
    const bannerCategoryDropdown = document.getElementById('banner-category-dropdown');
    return bannerCategoryDropdown.options[bannerCategoryDropdown.selectedIndex].value;
  }

  function getBannerImage(bannerCategory, bannerName) {
    return kameHouse.util.dom.getImgBtn({
      src: '/kame-house/img/banners/' + bannerCategory + '/' + bannerName + '.jpg',
      className: "banners-table-entry",
      alt: "Banner Table Entry",
      onClick: () => {return;}
    });
  }
}

$(document).ready(() => {
  kameHouse.addExtension("testBannerRenderer", new TestBannerRenderer());
});
