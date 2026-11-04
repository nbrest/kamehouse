/// <reference types="vitest/globals" />

import { describe, it, expect, vi, beforeEach } from 'vitest';
import { installKameHouseMock, createDocumentMock } from '../../helpers/kamehouse-mock';

describe('tennisworld user entity manager', () => {
  beforeEach(() => {
    vi.resetModules();
  });

  it('initializes the crud manager with the tennisworld user configuration', async () => {
    const init = vi.fn();
    const mock = installKameHouseMock({
      plugin: { crudManager: { init } }
    }, {
      window: { location: { search: '' } },
      document: createDocumentMock()
    });

    await import('@/js/tennisworld/tennisworld-user');

    expect(mock.util.module.waitForModules).toHaveBeenCalledWith(['crudManager'], expect.any(Function));
    expect(init).toHaveBeenCalledTimes(1);
    const config = init.mock.calls[0][0];
    expect(config.entityName).toBe('TennisWorld User');
    expect(config.url).toBe('/kame-house-tennisworld/api/v1/tennis-world/users');
    expect(config.columns.map((c: any) => c.name)).toEqual(['id', 'email', 'password']);
  });
});
