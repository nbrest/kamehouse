/// <reference types="vitest/globals" />

import { describe, it, expect, vi, beforeEach } from 'vitest';
import { installKameHouseMock, createDocumentMock } from '../../../../../helpers/kamehouse-mock';

describe('dragonball-users-edit', () => {
  beforeEach(() => {
    vi.resetModules();
  });

  it('loads the dragonball user to edit', async () => {
    const getDragonBallUser = vi.fn();
    const mock = installKameHouseMock({
      extension: { dragonBallUserServiceJsp: { getDragonBallUser, getAllDragonBallUsers: vi.fn() } }
    }, {
      window: { location: { search: '' } },
      document: createDocumentMock()
    });

    await import('@/js/jsp/test-module/dragonball/users/dragonball-users-edit');

    expect(mock.util.banner.setRandomAllBanner).toHaveBeenCalled();
    expect(mock.util.module.waitForModules).toHaveBeenCalledWith(['dragonBallUserServiceJsp'], expect.any(Function));
    expect(getDragonBallUser).toHaveBeenCalled();
  });
});
