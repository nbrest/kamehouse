/// <reference types="vitest/globals" />

import { describe, it, expect, vi, beforeEach } from 'vitest';
import { installKameHouseMock, createDocumentMock } from '../../../helpers/kamehouse-mock';

describe('test-module dragonball user entity manager', () => {
  beforeEach(() => {
    vi.resetModules();
  });

  it('initializes the crud manager with the dragonball user configuration', async () => {
    const init = vi.fn();
    const mock = installKameHouseMock({
      plugin: { crudManager: { init } }
    }, {
      window: { location: { search: '' } },
      document: createDocumentMock()
    });

    await import('@/js/test-module/entity-manager/dragonball-user');

    expect(mock.util.module.waitForModules).toHaveBeenCalledWith(['crudManager'], expect.any(Function));
    expect(init).toHaveBeenCalledTimes(1);
    const config = init.mock.calls[0][0];
    expect(config.readOnly).toBe(true);
    expect(config.entityName).toBe('DragonBall User');
    expect(config.columns.map((c: any) => c.name)).toEqual(['id', 'username', 'email', 'age', 'powerLevel', 'stamina']);
  });
});
