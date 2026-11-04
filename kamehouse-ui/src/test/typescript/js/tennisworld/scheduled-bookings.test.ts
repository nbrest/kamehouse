/// <reference types="vitest/globals" />

import { describe, it, expect, vi, beforeEach } from 'vitest';
import { installKameHouseMock, createDocumentMock } from '../../helpers/kamehouse-mock';

describe('scheduled-bookings', () => {
  let post: any;
  let success: any;
  let failure: any;

  beforeEach(() => {
    vi.resetModules();
    post = vi.fn((config: any, url: any, data: any, headers: any, successCb: any, errorCb: any) => {
      success = successCb;
      failure = errorCb;
    });
  });

  function install() {
    return installKameHouseMock({
      plugin: {
        modal: { loadingWheelModal: { open: vi.fn(), close: vi.fn() } },
        debugger: { http: { post } }
      }
    }, {
      window: { location: { search: '' } },
      document: createDocumentMock()
    });
  }

  it('loads the scheduled booking service', async () => {
    const mock = install();
    await import('@/js/tennisworld/scheduled-bookings');

    expect(mock.logger.info).toHaveBeenCalledWith('Loading ScheduledBookingService', null);
    expect(mock.util.banner.setRandomAllBanner).toHaveBeenCalled();
    expect(mock.extension.scheduledBookingService).toBeDefined();
  });

  it('triggers the scheduled bookings and reports executed bookings', async () => {
    const mock = install();
    await import('@/js/tennisworld/scheduled-bookings');
    const service = mock.extension.scheduledBookingService;

    service.triggerScheduledBookings();

    expect(mock.plugin.modal.loadingWheelModal.open).toHaveBeenCalled();
    expect(post).toHaveBeenCalled();
    const call = post.mock.calls[0];
    expect(call[1]).toBe('/kame-house-tennisworld/api/v1/tennis-world/scheduled-bookings');

    success([{ status: 201 }], 200, 'OK', {});
    expect(mock.plugin.modal.loadingWheelModal.close).toHaveBeenCalled();
    expect(mock.util.dom.setHtmlById).toHaveBeenCalledWith('scheduled-bookings-status', expect.stringContaining('Scheduled bookings executed successfully'));
  });

  it('reports when no scheduled bookings were triggered', async () => {
    const mock = install();
    await import('@/js/tennisworld/scheduled-bookings');
    const service = mock.extension.scheduledBookingService;

    service.triggerScheduledBookings();
    success([], 201, 'Created', {});

    expect(mock.util.dom.setHtmlById).toHaveBeenCalledWith('scheduled-bookings-status', expect.stringContaining('No scheduled bookings triggered'));
  });

  it('reports a failure when the response code is not successful', async () => {
    const mock = install();
    await import('@/js/tennisworld/scheduled-bookings');
    const service = mock.extension.scheduledBookingService;

    service.triggerScheduledBookings();
    failure('error', 500, 'Server Error', {});

    expect(mock.plugin.modal.loadingWheelModal.close).toHaveBeenCalled();
    expect(mock.util.dom.setHtmlById).toHaveBeenCalledWith('scheduled-bookings-status', expect.stringContaining('Failed to execute scheduled bookings'));
    expect(mock.logger.error).toHaveBeenCalledWith('Error executing scheduled bookings', null);
  });

  it('handles a missing response body', async () => {
    const mock = install();
    await import('@/js/tennisworld/scheduled-bookings');
    const service = mock.extension.scheduledBookingService;

    service.triggerScheduledBookings();
    success(null, 200, 'OK', {});

    expect(mock.util.dom.setHtmlById).toHaveBeenCalledWith('scheduled-bookings-status', expect.stringContaining('No scheduled bookings triggered'));
  });
});
