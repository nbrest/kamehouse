/// <reference types="vitest/globals" />

import { describe, it, expect, vi, beforeEach } from 'vitest';
import { installKameHouseMock, createDocumentMock } from '../../../../helpers/kamehouse-mock';

describe('kamehouse-mobile-settings-tabs', () => {
  beforeEach(() => {
    vi.resetModules();
  });

  async function loadTabs(documentOverrides: any = {}) {
    const documentMock = createDocumentMock(documentOverrides.elements || {});
    Object.assign(documentMock, documentOverrides);
    const mock = installKameHouseMock({
      extension: {
        mobile: {
          configManager: {
            initBackendOptionsFromConfig: vi.fn(),
            refreshBackendServerViewFromConfig: vi.fn()
          }
        }
      }
    }, { document: documentMock });

    await import('@/kame-house-mobile/kamehouse-mobile/plugin/js/kamehouse-mobile-settings-tabs');
    return { mock, documentMock };
  }

  it('loads the mobile tabs and backend tab content', async () => {
    const { mock } = await loadTabs();

    expect(mock.plugin.mobileTabsManager).toBeDefined();
    expect(mock.util.dom.loadById).toHaveBeenCalledWith(
      'kh-mobile-tabs-wrapper',
      '/kame-house-mobile/kamehouse-mobile/plugin/html/kamehouse-mobile-settings-tabs.html',
      expect.any(Function)
    );
    expect(mock.util.dom.loadById).toHaveBeenCalledWith(
      'tab-backend',
      '/kame-house-mobile/kamehouse-mobile/plugin/html/tab-backend.html',
      expect.any(Function)
    );
    expect(mock.util.module.setModuleLoaded).toHaveBeenCalledWith('mobileTabsManager');
    expect(mock.extension.mobile.configManager.initBackendOptionsFromConfig).toHaveBeenCalled();
    expect(mock.extension.mobile.configManager.refreshBackendServerViewFromConfig).toHaveBeenCalled();
  });

  it('opens the selected tab', async () => {
    const linkOne = { classList: { add: vi.fn(), remove: vi.fn() } };
    const linkTwo = { classList: { add: vi.fn(), remove: vi.fn() } };
    const imageOne = { classList: { add: vi.fn(), remove: vi.fn() } };
    const imageTwo = { classList: { add: vi.fn(), remove: vi.fn() } };
    const contentOne = { style: {} };
    const contentTwo = { style: {} };
    const selectedLink = { classList: { add: vi.fn(), remove: vi.fn() } };
    const selectedImage = { classList: { add: vi.fn(), remove: vi.fn() } };
    const selectedContent = { style: {} };

    const { mock } = await loadTabs({
      elements: {
        'tab-backend-link': selectedLink,
        'tab-backend-link-img': selectedImage,
        'tab-backend': selectedContent
      },
      getElementsByClassName: vi.fn((className: string) => {
        if (className === 'kh-mobile-tab-link') {
          return [linkOne, linkTwo];
        }
        if (className === 'kh-mobile-tab-link-img') {
          return [imageOne, imageTwo];
        }
        return [contentOne, contentTwo];
      })
    });

    mock.plugin.mobileTabsManager.openTab('tab-backend');

    expect(mock.util.dom.classListRemove).toHaveBeenCalledWith(linkOne, 'active');
    expect(mock.util.dom.classListRemove).toHaveBeenCalledWith(linkTwo, 'active');
    expect(mock.util.dom.classListAdd).toHaveBeenCalledWith(selectedLink, 'active');
    expect(mock.util.dom.classListRemove).toHaveBeenCalledWith(imageOne, 'kh-mobile-tab-img-active');
    expect(mock.util.dom.classListRemove).toHaveBeenCalledWith(imageTwo, 'kh-mobile-tab-img-active');
    expect(mock.util.dom.classListAdd).toHaveBeenCalledWith(selectedImage, 'kh-mobile-tab-img-active');
    expect(mock.util.dom.setDisplay).toHaveBeenCalledWith(contentOne, 'none');
    expect(mock.util.dom.setDisplay).toHaveBeenCalledWith(contentTwo, 'none');
    expect(mock.util.dom.setDisplay).toHaveBeenCalledWith(selectedContent, 'block');
  });
});
