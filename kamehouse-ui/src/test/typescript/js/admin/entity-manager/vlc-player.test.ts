/// <reference types="vitest/globals" />

import { describe, it, expect, vi, beforeEach } from 'vitest';
import { installKameHouseMock, createDocumentMock } from '../../../helpers/kamehouse-mock';

describe('admin vlc-player entity manager', () => {
  beforeEach(() => {
    vi.resetModules();
  });

  it('initializes the crud manager with the vlc player configuration', async () => {
    const init = vi.fn();
    const mock = installKameHouseMock({
      plugin: { crudManager: { init } }
    }, {
      window: { location: { search: '' } },
      document: createDocumentMock()
    });

    await import('@/js/admin/entity-manager/vlc-player');

    expect(mock.util.module.waitForModules).toHaveBeenCalledWith(['crudManager'], expect.any(Function));
    expect(init).toHaveBeenCalledTimes(1);
    const config = init.mock.calls[0][0];
    expect(config.entityName).toBe('Vlc Player');
    expect(config.entityNameJapanese).toBe('VLCプレーヤー');
    expect(config.url).toBe('/kame-house-vlcrc/api/v1/vlc-rc/players');
    expect(config.columns.map((c: any) => c.name)).toEqual(['id', 'hostname', 'port', 'username', 'password']);
  });
});
