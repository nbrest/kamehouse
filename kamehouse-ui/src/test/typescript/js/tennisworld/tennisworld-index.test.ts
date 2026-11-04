/// <reference types="vitest/globals" />

import { describe, it, expect, vi, beforeEach } from 'vitest';
import { installKameHouseMock, createDocumentMock } from '../../helpers/kamehouse-mock';

describe('tennisworld-index', () => {
  beforeEach(() => {
    vi.resetModules();
  });

  it('sets the random banner', async () => {
    const mock = installKameHouseMock({}, {
      window: { location: { search: '' } },
      document: createDocumentMock()
    });
    await import('@/js/tennisworld/tennisworld-index');
    expect(mock.util.banner.setRandomAllBanner).toHaveBeenCalled();
  });
});
