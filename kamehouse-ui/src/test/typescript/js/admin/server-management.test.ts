/// <reference types="vitest/globals" />

import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { installKameHouseMock, createDocumentMock } from '../../helpers/kamehouse-mock';

describe('admin server-management', () => {
  let get: any;
  let post: any;
  let del: any;
  let last: any;

  beforeEach(() => {
    vi.resetModules();
    last = null;
    get = vi.fn((config: any, url: any, data: any, headers: any, successCb: any, errorCb: any) => {
      last = { url, success: successCb, error: errorCb };
    });
    post = vi.fn((config: any, url: any, headers: any, body: any, successCb: any, errorCb: any) => {
      last = { url, body, success: successCb, error: errorCb };
    });
    del = vi.fn((config: any, url: any, headers: any, body: any, successCb: any, errorCb: any) => {
      last = { url, body, success: successCb, error: errorCb };
    });
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  function install(doc: any = createDocumentMock({
    'shutdown-delay-dropdown': { value: '10' },
    'suspend-delay-dropdown-hours': { value: '2' },
    'suspend-delay-dropdown-minutes': { value: '30' }
  })) {
    return installKameHouseMock({
      plugin: {
        debugger: { http: { get, post, delete: del } },
        modal: {
          loadingWheelModal: { open: vi.fn(), close: vi.fn() },
          basicModal: {
            openApiError: vi.fn(), open: vi.fn(), close: vi.fn(),
            setHtml: vi.fn(), appendHtml: vi.fn()
          }
        },
        kameHouseCommandManager: {
          renderKameHouseCommandResult: vi.fn(),
          renderErrorExecutingCommand: vi.fn()
        }
      }
    }, {
      window: { location: { search: '' } },
      document: doc
    });
  }

  it('initializes the server manager', async () => {
    const mock = install();
    await import('@/js/admin/server-management');

    expect(mock.logger.info).toHaveBeenCalledWith('Started initializing server management', null);
    expect(mock.util.banner.setRandomAllBanner).toHaveBeenCalledWith(null);
    expect(mock.util.dom.append).toHaveBeenCalled();
    expect(get).toHaveBeenCalledTimes(3);
    expect(mock.extension.serverManager).toBeDefined();
  });

  it('executes wake on lan and handles responses', async () => {
    const mock = install();
    await import('@/js/admin/server-management');
    const manager = mock.extension.serverManager;

    manager.execAdminWakeOnLan('/power-management/wake', 'server1');
    expect(last.url).toBe('/kame-house-admin/api/v1/admin/power-management/wake');
    expect(last.body).toEqual({ server: 'server1' });
    last.success('ok', 200, '', {});
    expect(mock.plugin.modal.loadingWheelModal.close).toHaveBeenCalled();

    manager.execAdminWakeOnLan('/power-management/wake', 'server1');
    last.error('err', 500, 'desc', {});
    expect(mock.plugin.modal.basicModal.openApiError).toHaveBeenCalled();
  });

  it('sets and cancels a shutdown command', async () => {
    const mock = install();
    await import('@/js/admin/server-management');
    const manager = mock.extension.serverManager;

    manager.setShutdownCommand();
    expect(last.url).toBe('/kame-house-admin/api/v1/admin/power-management/shutdown');
    expect(last.body).toEqual({ delay: '10' });
    last.success('ok', 200, '', {});
    last.success({ message: 'shutdown scheduled' }, 200, '', {});
    expect(mock.util.dom.setHtmlById).toHaveBeenCalledWith('shutdown-status', 'shutdown scheduled');

    manager.setShutdownCommand();
    last.error('err', 500, 'desc', {});
    expect(mock.plugin.modal.basicModal.openApiError).toHaveBeenCalled();
    last.error('err', 500, 'desc', {});
    expect(mock.util.dom.setHtmlById).toHaveBeenCalledWith('shutdown-status', 'Error getting the status of Shutdown command');

    manager.cancelShutdownCommand();
    expect(del).toHaveBeenCalled();
    last.success('ok', 200, '', {});
    last.success({ message: 'shutdown cancelled' }, 200, '', {});
    expect(mock.util.dom.setHtmlById).toHaveBeenCalledWith('shutdown-status', 'shutdown cancelled');
  });

  it('gets the shutdown status', async () => {
    const mock = install();
    await import('@/js/admin/server-management');
    const manager = mock.extension.serverManager;

    manager.getShutdownStatus(true);
    expect(mock.plugin.modal.loadingWheelModal.open).toHaveBeenCalled();
    last.success({ message: 'status' }, 200, '', {});
    expect(mock.util.dom.setHtmlById).toHaveBeenCalledWith('shutdown-status', 'status');
  });

  it('sets and cancels a suspend command', async () => {
    const mock = install();
    await import('@/js/admin/server-management');
    const manager = mock.extension.serverManager;

    manager.setSuspendCommand();
    expect(last.url).toBe('/kame-house-admin/api/v1/admin/power-management/suspend');
    expect(last.body).toEqual({ delay: 32 });
    last.success('ok', 200, '', {});
    last.success({ message: 'suspend scheduled' }, 200, '', {});
    expect(mock.util.dom.setHtmlById).toHaveBeenCalledWith('suspend-status', 'suspend scheduled');

    manager.setSuspendCommand();
    last.error('err', 500, 'desc', {});
    expect(mock.plugin.modal.basicModal.openApiError).toHaveBeenCalled();
    last.error('err', 500, 'desc', {});
    expect(mock.util.dom.setHtmlById).toHaveBeenCalledWith('suspend-status', 'Error getting the status of Suspend command');

    manager.cancelSuspendCommand();
    expect(del).toHaveBeenCalled();
    last.success('ok', 200, '', {});
    last.success({ message: 'suspend cancelled' }, 200, '', {});
    expect(mock.util.dom.setHtmlById).toHaveBeenCalledWith('suspend-status', 'suspend cancelled');
  });

  it('gets the suspend status', async () => {
    const mock = install();
    await import('@/js/admin/server-management');
    const manager = mock.extension.serverManager;

    manager.getSuspendStatus(true);
    expect(mock.plugin.modal.loadingWheelModal.open).toHaveBeenCalled();
    last.success({ message: 'suspend status' }, 200, '', {});
    expect(mock.util.dom.setHtmlById).toHaveBeenCalledWith('suspend-status', 'suspend status');
  });

  it('confirms a reboot', async () => {
    const mock = install();
    await import('@/js/admin/server-management');
    const manager = mock.extension.serverManager;

    manager.confirmRebootServer();

    expect(mock.plugin.modal.basicModal.setHtml).toHaveBeenCalled();
    expect(mock.plugin.modal.basicModal.appendHtml).toHaveBeenCalled();
    expect(mock.plugin.modal.basicModal.open).toHaveBeenCalled();
  });

  it('reboots the server through the confirm button', async () => {
    const mock = install();
    mock.util.dom.getButton = vi.fn((config: any) => {
      config.click(null, null);
      return {};
    });
    await import('@/js/admin/server-management');
    const manager = mock.extension.serverManager;

    manager.confirmRebootServer();

    expect(mock.plugin.modal.basicModal.close).toHaveBeenCalled();
    expect(last.url).toBe('/kame-house-admin/api/v1/admin/power-management/reboot');
    last.success('ok', 200, '', {});
    expect(mock.plugin.modal.loadingWheelModal.close).toHaveBeenCalled();
    last.error('err', 500, 'desc', {});
    expect(mock.plugin.modal.basicModal.openApiError).toHaveBeenCalled();
  });

  it('executes system state commands', async () => {
    const mock = install();
    await import('@/js/admin/server-management');
    const manager = mock.extension.serverManager;
    const commandManager = mock.plugin.kameHouseCommandManager;

    ['uptime', 'free', 'df', 'top'].forEach((method) => {
      manager[method]();
      expect(last.url).toBe('/kame-house-admin/api/v1/admin/system-state/' + method);
      last.success('output', 200, '', {});
      expect(commandManager.renderKameHouseCommandResult).toHaveBeenCalledWith('output', false, null, 'system-health-command-output');

      manager[method]();
      last.error('err', 500, 'desc', {});
      expect(mock.plugin.modal.basicModal.openApiError).toHaveBeenCalled();
      expect(commandManager.renderErrorExecutingCommand).toHaveBeenCalledWith('system-health-command-output');
    });
  });

  it('gets the httpd status', async () => {
    const mock = install();
    await import('@/js/admin/server-management');
    const manager = mock.extension.serverManager;

    manager.getHttpdStatus(true);
    expect(mock.plugin.modal.loadingWheelModal.open).toHaveBeenCalled();
    last.success('httpd output', 200, '', {});
    expect(mock.plugin.kameHouseCommandManager.renderKameHouseCommandResult).toHaveBeenCalledWith('httpd output', false, 'httpd-status', null);

    manager.getHttpdStatus(false);
    last.error('err', 404, 'desc', {});
    expect(mock.util.dom.setHtmlById).toHaveBeenCalledWith('httpd-status', 'Unable to get the status of apache httpd server. Is it running?');

    manager.getHttpdStatus(false);
    last.error('err', 500, 'desc', {});
    expect(mock.plugin.modal.basicModal.openApiError).toHaveBeenCalled();
    expect(mock.util.dom.setHtmlById).toHaveBeenCalledWith('httpd-status', 'Error getting the status of the apache httpd server');
  });

  it('restarts httpd and refreshes its status afterwards', async () => {
    const mock = install();
    await import('@/js/admin/server-management');
    const manager = mock.extension.serverManager;

    vi.useFakeTimers();
    manager.restartHttpd(true);
    expect(mock.plugin.modal.loadingWheelModal.open).toHaveBeenCalled();
    last.success('restarted', 200, '', {});
    expect(mock.plugin.kameHouseCommandManager.renderKameHouseCommandResult).toHaveBeenCalledWith('restarted', false, null, null);
    get.mockClear();
    vi.advanceTimersByTime(5000);
    expect(get).toHaveBeenCalledTimes(1);

    manager.restartHttpd(false);
    last.error('err', 500, 'desc', {});
    expect(mock.plugin.modal.basicModal.openApiError).toHaveBeenCalled();
    expect(mock.plugin.kameHouseCommandManager.renderErrorExecutingCommand).toHaveBeenCalledWith(null);
    get.mockClear();
    vi.advanceTimersByTime(5000);
    expect(get).toHaveBeenCalledTimes(1);
  });
});
