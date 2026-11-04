/// <reference types="vitest/globals" />

import { describe, it, expect, vi, beforeEach } from 'vitest';
import { installKameHouseMock, createDocumentMock } from '../../helpers/kamehouse-mock';

function flush() {
  return new Promise((resolve) => setTimeout(resolve, 0));
}

describe('admin ehcache', () => {
  let get: any;
  let del: any;
  let success: any;
  let failure: any;

  beforeEach(() => {
    vi.resetModules();
    success = null;
    failure = null;
    get = vi.fn((config: any, url: any, data: any, headers: any, successCb: any, errorCb: any) => {
      success = successCb;
      failure = errorCb;
    });
    del = vi.fn((config: any, url: any, headers: any, params: any, successCb: any, errorCb: any) => {
      success = successCb;
      failure = errorCb;
    });
  });

  function install(overrides: any = {}) {
    return installKameHouseMock({
      plugin: {
        webappTabsManager: { cookiePrefix: vi.fn(), loadStateFromCookies: vi.fn() },
        debugger: { http: { get, delete: del } },
        modal: { basicModal: { openAutoCloseable: vi.fn(), openApiError: vi.fn() } }
      },
      ...overrides
    }, {
      window: { location: { search: '' } },
      document: createDocumentMock()
    });
  }

  it('loads ehcache and fetches all cache data', async () => {
    const mock = install();
    await import('@/js/admin/ehcache');
    await flush();

    expect(mock.util.banner.setRandomAllBanner).toHaveBeenCalled();
    expect(mock.plugin.webappTabsManager.cookiePrefix).toHaveBeenCalledWith('kh-admin-ehcache');
    expect(mock.plugin.webappTabsManager.loadStateFromCookies).toHaveBeenCalled();
    expect(mock.util.fetch.loadHtmlSnippet).toHaveBeenCalledTimes(2);
    expect(get).toHaveBeenCalledTimes(6);
    expect(mock.extension.ehCacheManager).toBeDefined();
  });

  it('displays cache data and toggles cache views', async () => {
    const mock = install();
    await import('@/js/admin/ehcache');
    await flush();

    mock.extension.ehCacheManager.getAllCacheData('admin');
    success([{ name: 'cache1', status: 'ok', keys: 1, values: 2 }], 200, '', {});
    expect(mock.util.dom.append).toHaveBeenCalled();
    expect(mock.util.dom.setHtmlById).toHaveBeenCalledWith('ehcache-table-cache1-header', 'cache1');
    expect(mock.util.dom.setClickById).toHaveBeenCalled();

    mock.extension.ehCacheManager.toggleCacheView('some-class');
    mock.extension.ehCacheManager.toggleAllCacheView('admin');

    expect(mock.util.dom.toggleClass).toHaveBeenCalledWith('some-class');
    expect(mock.util.dom.toggleClass).toHaveBeenCalledWith('toggle-ehcache-table-cache1');
  });

  it('displays an error when getting cache data fails', async () => {
    const mock = install();
    await import('@/js/admin/ehcache');
    await flush();

    mock.extension.ehCacheManager.getAllCacheData('admin');
    failure('error', 500, 'Server Error', {});

    expect(mock.logger.error).toHaveBeenCalledWith('Error retrieving cache data. Please try again later.', expect.anything());
    expect(mock.util.dom.setHtmlById).toHaveBeenCalledWith('ehcache-table-admin-error-val', expect.stringContaining('Error retrieving cache data'));
  });

  it('clears a cache successfully', async () => {
    const mock = install();
    await import('@/js/admin/ehcache');
    await flush();

    mock.extension.ehCacheManager.clearCacheData('cache1', 'admin');
    success('ok', 200, '', {});

    expect(del).toHaveBeenCalled();
    expect(mock.plugin.modal.basicModal.openAutoCloseable).toHaveBeenCalledWith('Cache cache1 cleared successfully', 3000);
  });

  it('handles an error clearing a cache', async () => {
    const mock = install();
    await import('@/js/admin/ehcache');
    await flush();

    mock.extension.ehCacheManager.clearCacheData('cache1', 'admin');
    failure('error', 500, '', {});

    expect(mock.logger.error).toHaveBeenCalledWith('Error clearing cache cache1', expect.anything());
    expect(mock.plugin.modal.basicModal.openAutoCloseable).toHaveBeenCalledWith('Error clearing cache cache1', 3000);
  });

  it('clears all caches successfully', async () => {
    const mock = install();
    await import('@/js/admin/ehcache');
    await flush();

    mock.extension.ehCacheManager.clearAllCaches('admin');
    success('ok', 200, '', {});

    expect(mock.plugin.modal.basicModal.openAutoCloseable).toHaveBeenCalledWith('All caches cleared successfully', 3000);
  });

  it('handles an error clearing all caches', async () => {
    const mock = install();
    await import('@/js/admin/ehcache');
    await flush();

    mock.extension.ehCacheManager.clearAllCaches('admin');
    failure('error', 500, '', {});

    expect(mock.logger.error).toHaveBeenCalledWith('Error clearing all caches', expect.anything());
    expect(mock.plugin.modal.basicModal.openAutoCloseable).toHaveBeenCalledWith('Error clearing all caches', 3000);
  });

  it('gets cache data for a specific webapp', async () => {
    const mock = install();
    await import('@/js/admin/ehcache');
    await flush();

    get.mockClear();
    mock.extension.ehCacheManager.getAllCacheData('media');

    expect(get).toHaveBeenCalledWith(expect.anything(), '/kame-house-media/api/v1/commons/ehcache', null, null, expect.any(Function), expect.any(Function));
  });
});
