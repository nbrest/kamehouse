/// <reference types="vitest/globals" />

import { describe, it, expect, vi, beforeEach } from 'vitest';
import { installKameHouseMock, createDocumentMock } from '../../helpers/kamehouse-mock';

describe('tennisworld booking-schedule-config', () => {
  beforeEach(() => {
    vi.resetModules();
  });

  it('initializes the crud manager with the booking schedule configuration', async () => {
    const init = vi.fn();
    const mock = installKameHouseMock({
      plugin: { crudManager: { init } }
    }, {
      window: { location: { search: '' } },
      document: createDocumentMock()
    });

    await import('@/js/tennisworld/booking-schedule-config');

    expect(mock.util.module.waitForModules).toHaveBeenCalledWith(['crudManager'], expect.any(Function));
    expect(init).toHaveBeenCalledTimes(1);
    const config = init.mock.calls[0][0];
    expect(config.entityName).toBe('Booking Schedule Config');
    expect(config.entityNameJapanese).toBe('予約スケジュールの構成');
    expect(config.url).toBe('/kame-house-tennisworld/api/v1/tennis-world/booking-schedule-configs');
    expect(config.columns.map((c: any) => c.name)).toEqual([
      'id', 'tennisWorldUser', 'sessionType', 'site', 'day', 'time',
      'bookingDate', 'bookAheadDays', 'enabled', 'duration', 'courtNumber'
    ]);
    const user = config.columns.find((c: any) => c.name === 'tennisWorldUser');
    expect(user.columns.map((c: any) => c.name)).toEqual(['id', 'email', 'password']);
    const sessionType = config.columns.find((c: any) => c.name === 'sessionType');
    expect(sessionType.values).toHaveLength(11);
    const day = config.columns.find((c: any) => c.name === 'day');
    expect(day.values).toHaveLength(7);
  });
});
