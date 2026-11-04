/// <reference types="vitest/globals" />

import { describe, it, expect, vi, beforeEach } from 'vitest';
import { installKameHouseMock, createDocumentMock } from '../../helpers/kamehouse-mock';

function createFormElements(overrides: any = {}) {
  const ids = [
    'username', 'password', 'session-type', 'site', 'time', 'date', 'duration',
    'court-number', 'dry-run', 'card-holder-name', 'card-number-1', 'card-number-2',
    'card-number-3', 'card-number-4', 'card-exp-month', 'card-exp-year', 'card-cvv'
  ];
  const elements: any = {};
  ids.forEach((id) => {
    elements[id] = { value: '', checked: false, type: 'text' };
  });
  Object.keys(overrides).forEach((id) => {
    elements[id] = { ...elements[id], ...overrides[id] };
  });
  return elements;
}

describe('tennisworld one-off-bookings', () => {
  let post: any;
  let success: any;
  let failure: any;

  beforeEach(() => {
    vi.resetModules();
    success = null;
    failure = null;
    post = vi.fn((config: any, url: any, headers: any, data: any, successCb: any, errorCb: any) => {
      success = successCb;
      failure = errorCb;
    });
  });

  function install(elements: any = createFormElements()) {
    return installKameHouseMock({
      plugin: {
        modal: { loadingWheelModal: { open: vi.fn(), close: vi.fn() } },
        debugger: { http: { post } }
      },
      util: {
        time: { getDateFromEpoch: vi.fn(() => new Date('2025-01-02T03:04:05Z')) }
      }
    }, {
      window: { location: { search: '' } },
      document: createDocumentMock(elements)
    });
  }

  async function importService(mock: any) {
    await import('@/js/tennisworld/one-off-bookings');
    return mock.extension.bookingService;
  }

  it('loads the booking service', async () => {
    const mock = install();
    await importService(mock);

    expect(mock.logger.info).toHaveBeenCalledWith('Loading BookingService', null);
    expect(mock.util.banner.setRandomAllBanner).toHaveBeenCalled();
    expect(mock.extension.bookingService).toBeDefined();
  });

  it('executes a booking request and updates the response table', async () => {
    const mock = install(createFormElements({
      username: { value: 'goku' },
      password: { value: 'secret' },
      'session-type': { value: 'CARDIO' },
      site: { value: 'MELBOURNE_PARK' },
      time: { value: '10:00' },
      date: { value: '2025-02-03' },
      duration: { value: '60' },
      'court-number': { value: '3' },
      'dry-run': { checked: true }
    }));
    const service = await importService(mock);

    service.book();

    expect(mock.plugin.modal.loadingWheelModal.open).toHaveBeenCalledWith('Executing booking request. This could take a few minutes...');
    expect(post).toHaveBeenCalledTimes(1);
    const call = post.mock.calls[0];
    expect(call[1]).toBe('/kame-house-tennisworld/api/v1/tennis-world/bookings');
    expect(call[0].timeout).toBe(300);
    expect(call[3]).toEqual({
      username: 'goku',
      password: 'secret',
      sessionType: 'CARDIO',
      site: 'MELBOURNE_PARK',
      time: '10:00',
      date: '2025-02-03',
      duration: '60',
      courtNumber: '3',
      dryRun: true,
      retries: 0
    });

    success({
      id: 10,
      status: 'OK',
      message: 'Booked',
      request: {
        id: 5,
        username: 'goku',
        date: 123,
        time: '10:00',
        sessionType: 'CARDIO',
        site: 'MELBOURNE_PARK',
        duration: '60',
        courtNumber: 3,
        creationDate: 456
      }
    }, 200, 'OK', {});

    expect(mock.plugin.modal.loadingWheelModal.close).toHaveBeenCalled();
    expect(mock.util.dom.classListRemoveById).toHaveBeenCalledWith('brt', 'hidden-kh');
    expect(mock.util.dom.setHtmlById).toHaveBeenCalledWith('brt-response-code', 200);
    expect(mock.util.dom.setHtmlById).toHaveBeenCalledWith('brt-status', 'OK');
    expect(mock.util.dom.setHtmlById).toHaveBeenCalledWith('brt-username', 'goku');
    expect(mock.util.dom.setHtmlById).toHaveBeenCalledWith('brt-date', expect.any(String));
    expect(mock.util.dom.setHtmlById).toHaveBeenCalledWith('brt-creation-date', expect.any(String));
  });

  it('includes card details when the card holder is set', async () => {
    const mock = install(createFormElements({
      'card-holder-name': { value: 'Goku' },
      'card-number-1': { value: '1111' },
      'card-number-2': { value: '2222' },
      'card-number-3': { value: '3333' },
      'card-number-4': { value: '4444' },
      'card-exp-month': { value: '12' },
      'card-exp-year': { value: '2030' },
      'card-cvv': { value: '123' }
    }));
    const service = await importService(mock);

    service.book();
    const request = post.mock.calls[0][3];
    expect(request.cardDetails).toEqual({
      name: 'Goku',
      number: '1111222233334444',
      expiryDate: '12/2030',
      cvv: '123'
    });
  });

  it('updates the response table without a request', async () => {
    const mock = install();
    const service = await importService(mock);

    service.book();
    success({ id: 1, status: 'OK', message: 'Done' }, 201, 'Created', {});

    expect(mock.util.dom.setHtmlById).toHaveBeenCalledWith('brt-response-id', 1);
    expect(mock.util.dom.setHtmlById).not.toHaveBeenCalledWith('brt-username', expect.anything());
  });

  it('handles a successful response with a parsable failure body', async () => {
    const mock = install();
    const service = await importService(mock);

    service.book();
    failure(JSON.stringify({ id: 2, status: 'FAILED', message: 'Nope' }), 500, 'Error', {});

    expect(mock.plugin.modal.loadingWheelModal.close).toHaveBeenCalled();
    expect(mock.util.dom.setHtmlById).toHaveBeenCalledWith('brt-response-code', 500);
    expect(mock.logger.error).toHaveBeenCalledWith('Error executing the booking request', null);
  });

  it('handles an unparsable failure body', async () => {
    const mock = install();
    const service = await importService(mock);

    service.book();
    failure('not-json', 500, 'Error', {});

    expect(mock.util.dom.setHtmlById).toHaveBeenCalledWith('brt-status', 'Error parsing response body');
    expect(mock.logger.error).toHaveBeenCalledWith(expect.stringContaining('Error parsing the response:'), null);
    expect(mock.logger.error).toHaveBeenCalledWith('Error executing the booking request', null);
  });

  it('clears booking details', async () => {
    const elements = createFormElements({
      username: { value: 'a' },
      'dry-run': { checked: true }
    });
    const mock = install(elements);
    const service = await importService(mock);

    service.clearBookingDetails();

    expect(mock.logger.info).toHaveBeenCalledWith('clearBookingDetails', null);
    expect(elements['username'].value).toBe('');
    expect(elements['dry-run'].checked).toBe(null);
  });

  it('clears payment details', async () => {
    const mock = install();
    const service = await importService(mock);

    service.clearPaymentDetails();

    expect(mock.logger.info).toHaveBeenCalledWith('clearPaymentDetails', null);
  });

  it('toggles the password field visibility', async () => {
    const elements = createFormElements({
      password: { type: 'password' },
      'card-cvv': { type: 'text' }
    });
    const mock = install(elements);
    const service = await importService(mock);

    service.togglePasswordField('password');
    expect(mock.util.dom.setAttribute).toHaveBeenCalledWith(elements['password'], 'type', 'text');

    service.togglePasswordField('card-cvv');
    expect(mock.util.dom.setAttribute).toHaveBeenCalledWith(elements['card-cvv'], 'type', 'password');
  });
});
