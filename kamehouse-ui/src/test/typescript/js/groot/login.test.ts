/// <reference types="vitest/globals" />

import { describe, it, expect, vi, beforeEach } from 'vitest';
import { installKameHouseMock, createDocumentMock } from '../../helpers/kamehouse-mock';

describe('groot login', () => {
  beforeEach(() => {
    vi.resetModules();
  });

  it('loads without a referrer', async () => {
    const mock = installKameHouseMock({}, {
      window: { location: { search: '' } },
      document: createDocumentMock()
    });
    await import('@/js/groot/login');

    expect(mock.util.banner.setRandomAllBanner).toHaveBeenCalled();
    expect(mock.util.dom.setValueById).not.toHaveBeenCalled();
    expect(mock.extension.grootLogin).toBeDefined();
  });

  it('sets the referrer when present', async () => {
    const mock = installKameHouseMock({}, {
      window: { location: { search: '?referrer=https%3A%2F%2Fexample.com' } },
      document: createDocumentMock()
    });
    await import('@/js/groot/login');

    expect(mock.util.dom.setValueById).toHaveBeenCalledWith('login-referrer', 'https://example.com');
  });
});
