/// <reference types="vitest/globals" />

import { describe, it, expect, vi, beforeEach } from 'vitest';
import { installKameHouseMock, createDocumentMock } from '../../helpers/kamehouse-mock';

describe('groot root-index', () => {
  beforeEach(() => {
    vi.resetModules();
  });

  it('loads the groot home manager', async () => {
    const mock = installKameHouseMock({}, {
      window: { location: { search: '' } },
      document: createDocumentMock()
    });
    await import('@/js/groot/root-index');

    expect(mock.util.banner.setRandomAllBanner).toHaveBeenCalled();
    expect(mock.extension.grootHome).toBeDefined();
  });

  it('sets the client time and date', async () => {
    const mock = installKameHouseMock({}, {
      window: { location: { search: '' } },
      document: createDocumentMock()
    });
    await import('@/js/groot/root-index');

    mock.extension.grootHome.setClientTimeAndDate();
    expect(mock.util.dom.setHtmlById).toHaveBeenCalledWith('client-time-and-date', expect.stringContaining('Client:'));
  });
});
