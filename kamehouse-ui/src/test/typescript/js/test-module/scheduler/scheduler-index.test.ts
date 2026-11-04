/// <reference types="vitest/globals" />

import { describe, it, expect, vi, beforeEach } from 'vitest';
import { installKameHouseMock, createDocumentMock } from '../../../helpers/kamehouse-mock';

describe('test-module scheduler-index', () => {
  let get: any;
  let post: any;
  let del: any;

  beforeEach(() => {
    vi.resetModules();
    get = vi.fn();
    post = vi.fn();
    del = vi.fn();
  });

  function install() {
    return installKameHouseMock({
      plugin: {
        modal: {
          loadingWheelModal: { open: vi.fn(), close: vi.fn() },
          basicModal: { openApiError: vi.fn() }
        },
        debugger: { http: { get, post, delete: del } }
      }
    }, {
      window: { location: { search: '' } },
      document: createDocumentMock({
        'sample-job-delay-dropdown': { value: '30' }
      })
    });
  }

  async function importScheduler() {
    const mock = install();
    await import('@/js/test-module/scheduler/scheduler-index');
    return { mock, scheduler: mock.extension.testScheduler };
  }

  it('loads the scheduler and fetches the current status', async () => {
    const { mock, scheduler } = await importScheduler();

    expect(mock.logger.info).toHaveBeenCalledWith('Loading TestScheduler', null);
    expect(mock.util.banner.setRandomAllBanner).toHaveBeenCalledWith(null);
    expect(mock.util.module.waitForModules).toHaveBeenCalledWith(['kameHouseModal', 'kameHouseDebugger'], expect.any(Function));
    expect(scheduler).toBeDefined();
    expect(get).toHaveBeenCalledWith(expect.anything(), '/kame-house-testmodule/api/v1/test-module/test-scheduler/sample-job', null, null, expect.any(Function), expect.any(Function));
  });

  it('sets a sample job and refreshes the status on success', async () => {
    const { mock, scheduler } = await importScheduler();

    scheduler.setSampleJob();

    expect(mock.logger.trace).toHaveBeenCalledWith('Sample job delay: 30', null);
    expect(mock.plugin.modal.loadingWheelModal.open).toHaveBeenCalled();
    expect(post).toHaveBeenCalledTimes(1);
    const call = post.mock.calls[0];
    expect(call[1]).toBe('/kame-house-testmodule/api/v1/test-module/test-scheduler/sample-job');
    expect(call[3]).toEqual({ delay: '30' });

    call[4]('ok', 200, '', {});
    expect(mock.plugin.modal.loadingWheelModal.close).toHaveBeenCalled();
    expect(get).toHaveBeenCalledTimes(2);
  });

  it('sets a sample job and handles an error', async () => {
    const { mock, scheduler } = await importScheduler();

    scheduler.setSampleJob();
    const call = post.mock.calls[0];
    call[5]('error', 500, 'Server Error', {});
    expect(mock.plugin.modal.loadingWheelModal.close).toHaveBeenCalled();
    expect(mock.plugin.modal.basicModal.openApiError).toHaveBeenCalledWith('error', 500, 'Server Error', {});
    expect(get).toHaveBeenCalledTimes(2);
  });

  it('cancels a sample job and refreshes the status on success', async () => {
    const { mock, scheduler } = await importScheduler();

    scheduler.cancelSampleJob();

    expect(del).toHaveBeenCalledTimes(1);
    const call = del.mock.calls[0];
    expect(call[1]).toBe('/kame-house-testmodule/api/v1/test-module/test-scheduler/sample-job');
    call[4]('ok', 200, '', {});
    expect(mock.plugin.modal.loadingWheelModal.close).toHaveBeenCalled();
    expect(get).toHaveBeenCalledTimes(2);
  });

  it('cancels a sample job and handles an error', async () => {
    const { mock, scheduler } = await importScheduler();

    scheduler.cancelSampleJob();
    del.mock.calls[0][5]('error', 500, 'Server Error', {});
    expect(mock.plugin.modal.basicModal.openApiError).toHaveBeenCalledWith('error', 500, 'Server Error', {});
  });

  it('opens a modal when getting the status with openModal', async () => {
    const { mock, scheduler } = await importScheduler();

    scheduler.getSampleJobStatus(true);

    expect(mock.plugin.modal.loadingWheelModal.open).toHaveBeenCalled();
    expect(get).toHaveBeenCalledTimes(2);
  });

  it('updates the status on success', async () => {
    const { mock, scheduler } = await importScheduler();

    scheduler.getSampleJobStatus(false);
    get.mock.calls[1][4]({ message: 'Job running' }, 200, '', {});

    expect(mock.plugin.modal.loadingWheelModal.close).toHaveBeenCalled();
    expect(mock.util.dom.setHtmlById).toHaveBeenCalledWith('sample-job-status', 'Job running');
  });

  it('updates the status on error', async () => {
    const { mock, scheduler } = await importScheduler();

    scheduler.getSampleJobStatus(false);
    get.mock.calls[1][5]('error', 500, 'Server Error', {});

    expect(mock.plugin.modal.loadingWheelModal.close).toHaveBeenCalled();
    expect(mock.plugin.modal.basicModal.openApiError).toHaveBeenCalledWith('error', 500, 'Server Error', {});
    expect(mock.util.dom.setHtmlById).toHaveBeenCalledWith('sample-job-status', 'Error getting the status of SampleJob command');
  });
});
