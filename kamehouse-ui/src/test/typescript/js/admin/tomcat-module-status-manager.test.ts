/// <reference types="vitest/globals" />

import { describe, it, expect, vi, beforeEach } from 'vitest';
import { installKameHouseMock, createDocumentMock } from '../../helpers/kamehouse-mock';

describe('admin tomcat-module-status-manager', () => {
  let get: any;
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
  });

  function install() {
    return installKameHouseMock({
      plugin: { debugger: { http: { get } } }
    }, {
      window: { location: { search: '' } },
      document: createDocumentMock()
    });
  }

  it('loads and fetches module status for all webapps', async () => {
    const mock = install();
    await import('@/js/admin/tomcat-module-status-manager');

    expect(get).toHaveBeenCalledTimes(6);
    expect(mock.util.module.setModuleLoaded).toHaveBeenCalledWith('moduleStatusManager');
    expect(mock.logger.info).toHaveBeenCalledWith('Initialized moduleStatusManager', null);
    expect(mock.extension.tomcatModuleStatusManager).toBeDefined();
  });

  it('displays the module status', async () => {
    const mock = install();
    await import('@/js/admin/tomcat-module-status-manager');

    success({ module: 'admin', buildVersion: '1.0', buildDate: 'today' }, 200, '', {});

    expect(mock.util.dom.setHtmlById).toHaveBeenCalledWith('mst-admin-build-version-val', '1.0');
    expect(mock.util.dom.setHtmlById).toHaveBeenCalledWith('mst-admin-build-date-val', 'today');
  });

  it('displays an error getting the module status', async () => {
    const mock = install();
    await import('@/js/admin/tomcat-module-status-manager');
    mock.extension.tomcatModuleStatusManager.getAllModulesStatus();

    failure('error', 500, '', {});

    expect(mock.util.dom.setHtmlById).toHaveBeenCalledWith('mst-vlcrc-build-version-val', 'Error getting data');
    expect(mock.logger.error).toHaveBeenCalledWith(expect.stringContaining('Error retrieving module status data for vlcrc'), expect.anything());
  });

  it('gets all module statuses again', async () => {
    const mock = install();
    await import('@/js/admin/tomcat-module-status-manager');
    get.mockClear();

    mock.extension.tomcatModuleStatusManager.getAllModulesStatus();

    expect(get).toHaveBeenCalledTimes(6);
  });
});
