/// <reference types="vitest/globals" />

import { describe, it, expect, vi, beforeEach } from 'vitest';
import { installKameHouseMock, createDocumentMock } from '../../../helpers/kamehouse-mock';

describe('kamehouse-webapp-tabs', () => {
  beforeEach(() => {
    vi.resetModules();
  });

  it('loads the webapp tabs plugin and imports tabs', async () => {
    const mock = installKameHouseMock({}, {
      window: { location: { search: '' } },
      document: createDocumentMock()
    });
    await import('@/kamehouse/js/plugin/kamehouse-webapp-tabs');

    expect(mock.util.dom.append).toHaveBeenCalled();
    expect(mock.util.dom.loadById).toHaveBeenCalledWith('kh-webapp-tabs-wrapper', expect.any(String), expect.any(Function));
    expect(mock.util.module.setModuleLoaded).toHaveBeenCalledWith('webappTabsManager');
    expect(mock.plugin.webappTabsManager).toBeDefined();
  });

  it('uses the default tab when no cookie is set', async () => {
    const mock = installKameHouseMock({}, {
      window: { location: { search: '' } },
      document: createDocumentMock()
    });
    await import('@/kamehouse/js/plugin/kamehouse-webapp-tabs');
    const manager = mock.plugin.webappTabsManager;

    manager.cookiePrefix('kh-admin');
    manager.loadStateFromCookies();

    expect(mock.util.cookies.getCookie).toHaveBeenCalledWith('kh-admin-current-tab');
    expect(mock.util.cookies.setCookie).toHaveBeenCalledWith('kh-admin-current-tab', 'tab-admin', null);
  });

  it('uses the cookie tab when one is set', async () => {
    const mock = installKameHouseMock({
      util: { cookies: { getCookie: vi.fn(() => 'tab-custom') } }
    }, {
      window: { location: { search: '' } },
      document: createDocumentMock()
    });
    await import('@/kamehouse/js/plugin/kamehouse-webapp-tabs');
    const manager = mock.plugin.webappTabsManager;

    manager.cookiePrefix('kh-admin');
    manager.loadStateFromCookies();

    expect(mock.util.cookies.setCookie).toHaveBeenCalledWith('kh-admin-current-tab', 'tab-custom', null);
  });

  it('opens a tab and updates the tab links and content', async () => {
    const links = [{}, {}];
    const contents = [{}, {}];
    const link = {};
    const content = {};
    const doc: any = createDocumentMock({
      'tab-one': content,
      'tab-one-link': link
    });
    doc.getElementsByClassName = vi.fn((name: string) => {
      if (name === 'kh-webapp-tab-link') return links;
      if (name === 'kh-webapp-tab-content') return contents;
      return [];
    });
    const mock = installKameHouseMock({}, {
      window: { location: { search: '' } },
      document: doc
    });
    await import('@/kamehouse/js/plugin/kamehouse-webapp-tabs');
    const manager = mock.plugin.webappTabsManager;

    manager.cookiePrefix('kh-admin');
    manager.openTab('tab-one');

    expect(mock.util.cookies.setCookie).toHaveBeenCalledWith('kh-admin-current-tab', 'tab-one', null);
    expect(mock.util.dom.classListRemove).toHaveBeenCalledWith(links[0], 'active');
    expect(mock.util.dom.classListAdd).toHaveBeenCalledWith(link, 'active');
    expect(mock.util.dom.setDisplay).toHaveBeenCalledWith(contents[0], 'none');
    expect(mock.util.dom.setDisplay).toHaveBeenCalledWith(content, 'block');
  });
});
