/// <reference types="vitest/globals" />

import { describe, it, expect, vi, beforeEach } from 'vitest';
import { installKameHouseMock, createDocumentMock } from '../../../helpers/kamehouse-mock';

describe('angular-1-index', () => {
  beforeEach(() => {
    vi.resetModules();
  });

  it('sets the random banner', async () => {
    const mock = installKameHouseMock({}, {
      window: { location: { search: '' } },
      document: createDocumentMock()
    });
    await import('@/js/test-module/angular-1/angular-1-index');
    expect(mock.util.banner.setRandomAllBanner).toHaveBeenCalled();
  });
});
