/// <reference types="vitest/globals" />

import { describe, it, expect, vi, beforeEach } from 'vitest';
import { installKameHouseMock, createDocumentMock } from '../../helpers/kamehouse-mock';

describe('tennisworld booking-response', () => {
  beforeEach(() => {
    vi.resetModules();
  });

  it('initializes the crud manager with the booking response configuration', async () => {
    const init = vi.fn();
    const mock = installKameHouseMock({
      plugin: { crudManager: { init } }
    }, {
      window: { location: { search: '' } },
      document: createDocumentMock()
    });

    await import('@/js/tennisworld/booking-response');

    expect(mock.util.module.waitForModules).toHaveBeenCalledWith(['crudManager'], expect.any(Function));
    expect(init).toHaveBeenCalledTimes(1);
    const config = init.mock.calls[0][0];
    expect(config.entityName).toBe('Booking Response');
    expect(config.entityNameJapanese).toBe('予約応答');
    expect(config.url).toBe('/kame-house-tennisworld/api/v1/tennis-world/booking-responses');
    expect(config.readOnly).toBe(true);
    expect(config.defaultSorting).toEqual({ columnNumber: 11, sortType: 'timestamp', direction: 'desc' });
    expect(config.readAll).toEqual({ maxRows: 500, sortColumn: 'id', sortAscending: false });
    expect(config.columns.map((c: any) => c.name)).toEqual(['id', 'status', 'message', 'request']);
    const request = config.columns.find((c: any) => c.name === 'request');
    expect(request.columns.map((c: any) => c.name)).toEqual([
      'id', 'username', 'date', 'time', 'sessionType', 'site', 'duration',
      'dryRun', 'creationDate', 'scheduled', 'courtNumber'
    ]);
    const sessionType = request.columns.find((c: any) => c.name === 'sessionType');
    expect(sessionType.values).toHaveLength(11);
    expect(sessionType.displayValues).toHaveLength(11);
  });
});
