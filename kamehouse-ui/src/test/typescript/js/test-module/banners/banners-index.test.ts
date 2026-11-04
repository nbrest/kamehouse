/// <reference types="vitest/globals" />

import { describe, it, expect, vi, beforeEach } from 'vitest';
import { installKameHouseMock, createDocumentMock } from '../../../helpers/kamehouse-mock';

describe('test-module banners-index', () => {
  let buttonOptions: any;

  beforeEach(() => {
    vi.resetModules();
    buttonOptions = null;
  });

  function install() {
    const dropdown: any = { options: [{ value: 'dragonball' }], selectedIndex: 0 };
    const tbody: any = {};
    return installKameHouseMock({
      util: {
        banner: {
          setRandomAllBanner: vi.fn(),
          getBannerCategories: vi.fn(() => ['dragonball', 'onepiece']),
          getBanners: vi.fn(() => ['banner-goku', 'banner-luffy']),
          setBanner: vi.fn()
        },
        dom: {
          getOption: vi.fn((attrs: any, text: any) => ({ attrs, text })),
          getTrTd: vi.fn(() => ({})),
          getDiv: vi.fn((attrs: any, text: any) => ({ attrs, text })),
          getButton: vi.fn((options: any) => { buttonOptions = options; return {}; }),
          setStyle: vi.fn(),
          classListAddById: vi.fn(),
          append: vi.fn()
        }
      }
    }, {
      window: { location: { search: '' } },
      document: createDocumentMock({
        'banner-category-dropdown': dropdown,
        'banners-table-body': tbody
      })
    });
  }

  it('loads the banner renderer and populates the category dropdown', async () => {
    const mock = install();
    await import('@/js/test-module/banners/banners-index');

    expect(mock.logger.info).toHaveBeenCalledWith('Loading TestBannerRenderer', null);
    const renderer = mock.extension.testBannerRenderer;
    expect(renderer).toBeDefined();
    expect(mock.util.banner.getBannerCategories).toHaveBeenCalled();
    expect(mock.util.dom.append).toHaveBeenCalled();
  });

  it('reloads banners for the selected category', async () => {
    const mock = install();
    await import('@/js/test-module/banners/banners-index');
    const renderer = mock.extension.testBannerRenderer;

    renderer.reloadBanners();

    expect(mock.logger.info).toHaveBeenCalledWith('Reloading banners', null);
    expect(mock.util.dom.empty).toHaveBeenCalled();
    expect(mock.util.banner.getBanners).toHaveBeenCalledWith('dragonball');
    expect(mock.util.dom.append).toHaveBeenCalledTimes(15);
    expect(buttonOptions.backgroundImg).toBe('/kame-house/img/banners/dragonball/banner-luffy.jpg');
    expect(mock.util.dom.setStyle).toHaveBeenCalledWith(expect.anything(), 'background-size', 'cover');
  });

  it('sets the selected banner when its button is clicked', async () => {
    const mock = install();
    await import('@/js/test-module/banners/banners-index');
    const renderer = mock.extension.testBannerRenderer;

    renderer.reloadBanners();
    buttonOptions.click({}, { bannerName: 'banner-goku' });

    expect(mock.util.dom.classListAddById).toHaveBeenCalledWith('banner', 'fade-in-out-15s');
    expect(mock.util.banner.setBanner).toHaveBeenCalledWith('banner-goku');
    expect(mock.util.dom.setHtmlById).toHaveBeenCalledWith('banner-name', 'banner-goku');
    expect(mock.core.backToTop).toHaveBeenCalled();
  });
});
