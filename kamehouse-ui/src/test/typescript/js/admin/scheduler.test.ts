/// <reference types="vitest/globals" />

import { describe, it, expect, vi, beforeEach } from 'vitest';
import { installKameHouseMock, createDocumentMock } from '../../helpers/kamehouse-mock';

describe('admin scheduler', () => {
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
    del = vi.fn((config: any, url: any, headers: any, data: any, successCb: any, errorCb: any) => {
      success = successCb;
      failure = errorCb;
    });
  });

  function install() {
    return installKameHouseMock({
      plugin: {
        webappTabsManager: { cookiePrefix: vi.fn(), loadStateFromCookies: vi.fn() },
        debugger: { http: { get, delete: del } },
        modal: {
          loadingWheelModal: { open: vi.fn(), close: vi.fn() },
          basicModal: { openApiError: vi.fn() }
        }
      }
    }, {
      window: { location: { search: '' } },
      document: createDocumentMock()
    });
  }

  const flush = () => new Promise((resolve) => setTimeout(resolve, 0));

  it('initializes the scheduler and loads jobs for all webapps', async () => {
    const mock = install();
    await import('@/js/admin/scheduler');
    await flush();

    expect(mock.logger.info).toHaveBeenCalledWith('Started initializing scheduler', null);
    expect(mock.util.banner.setRandomAllBanner).toHaveBeenCalledWith(null);
    expect(mock.plugin.webappTabsManager.cookiePrefix).toHaveBeenCalledWith('kh-admin-scheduler');
    expect(mock.plugin.webappTabsManager.loadStateFromCookies).toHaveBeenCalled();
    expect(mock.util.fetch.loadHtmlSnippet).toHaveBeenCalled();
    expect(get).toHaveBeenCalledTimes(6);
    expect(mock.extension.scheduler).toBeDefined();
  });

  it('displays jobs when there are none configured', async () => {
    const mock = install();
    await import('@/js/admin/scheduler');
    await flush();

    success([], 200, '', {});

    expect(mock.util.dom.getSpan).toHaveBeenCalled();
    expect(mock.util.dom.getTrTd).toHaveBeenCalled();
    expect(mock.plugin.modal.loadingWheelModal.close).toHaveBeenCalled();
  });

  it('displays jobs and formats their schedules', async () => {
    const mock = install();
    await import('@/js/admin/scheduler');
    await flush();

    success([
      { key: { name: 'job1', group: 'grp' }, description: 'desc', jobClass: 'Class', schedules: [] },
      { key: { name: 'job2', group: 'grp' }, description: 'desc2', jobClass: 'Class2', schedules: [{ nextRun: '1234567890' }, { nextRun: null }] }
    ], 200, '', {});

    expect(mock.util.dom.getElementFromTemplate).toHaveBeenCalled();
    expect(mock.util.dom.setHtmlById).toHaveBeenCalledWith('scheduler-table-vlcrcjob1-name-val', 'job1');
    expect(mock.util.dom.setHtmlById).toHaveBeenCalledWith('scheduler-table-vlcrcjob1-schedule-val', 'Job not scheduled');
    expect(mock.util.dom.setClickById).toHaveBeenCalled();
    expect(del).toHaveBeenCalled();
    expect(mock.core.configDynamicHtml).toHaveBeenCalled();
  });

  it('cancels a job execution when the clear button is clicked', async () => {
    const mock = install();
    await import('@/js/admin/scheduler');
    await flush();

    success([{ key: { name: 'job1', group: 'grp' }, description: 'desc', jobClass: 'Class', schedules: [] }], 200, '', {});

    expect(del).toHaveBeenCalledTimes(1);
    expect(del.mock.calls[0][1]).toBe('/kame-house-vlcrc/api/v1/commons/scheduler/jobs');
    expect(del.mock.calls[0][3]).toEqual({ name: 'job1', group: 'grp' });
  });

  it('opens the loading modal when requested', async () => {
    const mock = install();
    await import('@/js/admin/scheduler');
    await flush();

    mock.extension.scheduler.getAllJobs('admin', true);

    expect(mock.plugin.modal.loadingWheelModal.open).toHaveBeenCalled();
  });

  it('displays an error when jobs cannot be retrieved', async () => {
    const mock = install();
    await import('@/js/admin/scheduler');
    await flush();

    failure('error', 500, 'Server Error', {});

    expect(mock.util.dom.getP).toHaveBeenCalledWith(null, 'Error retrieving jobs from the backend');
    expect(mock.plugin.modal.basicModal.openApiError).toHaveBeenCalled();
  });
});
