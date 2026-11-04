/// <reference types="vitest/globals" />

import { describe, it, expect, vi, beforeEach } from 'vitest';
import { installKameHouseMock, createDocumentMock } from '../../helpers/kamehouse-mock';

function flush() {
  return new Promise((resolve) => setTimeout(resolve, 0));
}

describe('admin log-level', () => {
  let get: any;
  let del: any;
  let put: any;
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
    put = vi.fn((config: any, url: any, headers: any, params: any, successCb: any, errorCb: any) => {
      success = successCb;
      failure = errorCb;
    });
  });

  function install(overrides: any = {}, doc: any = createDocumentMock({ 'select-kamehouse-log-level-admin': { value: 'INFO' } })) {
    return installKameHouseMock({
      plugin: {
        webappTabsManager: { cookiePrefix: vi.fn(), loadStateFromCookies: vi.fn() },
        debugger: { http: { get, delete: del, put } },
        modal: {
          loadingWheelModal: { open: vi.fn(), close: vi.fn() },
          basicModal: { openApiError: vi.fn(), openAutoCloseable: vi.fn() }
        }
      },
      ...overrides
    }, {
      window: { location: { search: '' } },
      document: doc
    });
  }

  it('loads log-level and fetches all log levels', async () => {
    const mock = install();
    await import('@/js/admin/log-level');
    await flush();

    expect(mock.util.banner.setRandomAllBanner).toHaveBeenCalled();
    expect(mock.plugin.webappTabsManager.cookiePrefix).toHaveBeenCalledWith('kh-admin-log-level');
    expect(get).toHaveBeenCalledTimes(6);
    expect(mock.extension.backendLogLevelUtils).toBeDefined();
  });

  it('gets log levels and updates the table', async () => {
    const mock = install();
    await import('@/js/admin/log-level');
    await flush();
    const utils = mock.extension.backendLogLevelUtils;

    utils.getLogLevels('admin', true);
    expect(mock.plugin.modal.loadingWheelModal.open).toHaveBeenCalled();
    success(['com.foo:INFO', 'com.bar:DEBUG'], 200, '', {});
    expect(mock.plugin.modal.loadingWheelModal.close).toHaveBeenCalled();
    expect(mock.util.dom.append).toHaveBeenCalled();
  });

  it('handles an error getting log levels', async () => {
    const mock = install();
    await import('@/js/admin/log-level');
    await flush();
    const utils = mock.extension.backendLogLevelUtils;

    utils.getLogLevels('admin', false);
    failure('error', 500, 'Server Error', {});
    expect(mock.plugin.modal.basicModal.openApiError).toHaveBeenCalled();
  });

  it('resets log levels', async () => {
    const mock = install();
    await import('@/js/admin/log-level');
    await flush();
    const utils = mock.extension.backendLogLevelUtils;

    utils.resetLogLevels('admin');
    success([], 200, '', {});
    expect(del).toHaveBeenCalled();
    expect(mock.plugin.modal.loadingWheelModal.close).toHaveBeenCalled();
  });

  it('sets the kamehouse log level', async () => {
    const mock = install();
    await import('@/js/admin/log-level');
    await flush();
    const utils = mock.extension.backendLogLevelUtils;

    utils.setKamehouseLogLevel('admin');
    expect(put).toHaveBeenCalledWith(expect.anything(), '/kame-house-admin/api/v1/commons/log-levelINFO', null, null, expect.any(Function), expect.any(Function));
    success([], 200, '', {});
  });

  it('sets the kamehouse log level to debug and trace', async () => {
    const mock = install();
    await import('@/js/admin/log-level');
    await flush();
    const utils = mock.extension.backendLogLevelUtils;

    utils.setKamehouseLogLevelToDebug('admin');
    expect(put).toHaveBeenCalledWith(expect.anything(), '/kame-house-admin/api/v1/commons/log-level/debug', null, null, expect.any(Function), expect.any(Function));

    utils.setKamehouseLogLevelToTrace('admin');
    expect(put).toHaveBeenCalledWith(expect.anything(), '/kame-house-admin/api/v1/commons/log-level/trace', null, null, expect.any(Function), expect.any(Function));
  });

  it('sets all request logger config options', async () => {
    const doc = createDocumentMock({
      'select-kh-req-logger-cfg-payload-admin': { value: 'true' },
      'select-kh-req-logger-cfg-headers-admin': { value: 'false' },
      'select-kh-req-logger-cfg-query-string-admin': { value: 'true' },
      'select-kh-req-logger-cfg-client-info-admin': { value: 'false' }
    });
    const mock = install({}, doc);
    await import('@/js/admin/log-level');
    await flush();
    const utils = mock.extension.backendLogLevelUtils;

    utils.setRequestLoggerConfigPayload('admin');
    utils.setRequestLoggerConfigHeaders('admin');
    utils.setRequestLoggerConfigQueryString('admin');
    utils.setRequestLoggerConfigClientInfo('admin');

    expect(put).toHaveBeenCalledTimes(4);
  });

  it('processes request logger config success and error', async () => {
    const doc = createDocumentMock({
      'select-kh-req-logger-cfg-payload-admin': { value: 'true' }
    });
    const mock = install({}, doc);
    await import('@/js/admin/log-level');
    await flush();
    const utils = mock.extension.backendLogLevelUtils;

    utils.setRequestLoggerConfigPayload('admin');
    success({ message: 'updated' }, 200, '', {});
    expect(mock.plugin.modal.basicModal.openAutoCloseable).toHaveBeenCalledWith('admin : updated', 7000);

    utils.setRequestLoggerConfigPayload('admin');
    failure('error', 500, '', {});
    expect(mock.plugin.modal.basicModal.openApiError).toHaveBeenCalled();
  });
});
