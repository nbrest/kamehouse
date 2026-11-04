/// <reference types="vitest/globals" />

import { describe, it, expect, vi, beforeEach } from 'vitest';
import { installKameHouseMock, createDocumentMock } from '../../../../../helpers/kamehouse-mock';

describe('dragonball-users-list', () => {
  beforeEach(() => {
    vi.resetModules();
  });

  it('loads all dragonball users', async () => {
    const getAllDragonBallUsers = vi.fn();
    const mock = installKameHouseMock({
      extension: { dragonBallUserServiceJsp: { getDragonBallUser: vi.fn(), getAllDragonBallUsers } }
    }, {
      window: { location: { search: '' } },
      document: createDocumentMock()
    });

    await import('@/js/jsp/test-module/dragonball/users/dragonball-users-list');

    expect(mock.util.banner.setRandomAllBanner).toHaveBeenCalled();
    expect(mock.util.module.waitForModules).toHaveBeenCalledWith(['dragonBallUserServiceJsp'], expect.any(Function));
    expect(getAllDragonBallUsers).toHaveBeenCalled();
  });
});
