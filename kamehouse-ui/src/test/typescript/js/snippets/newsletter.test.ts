/// <reference types="vitest/globals" />

import { describe, it, expect, vi, beforeEach } from 'vitest';
import { installKameHouseMock, createDocumentMock } from '../../helpers/kamehouse-mock';

describe('newsletter', () => {
  beforeEach(() => {
    vi.resetModules();
  });

  it('loads the newsletter snippet', async () => {
    const head = {};
    const mock = installKameHouseMock({
      util: { dom: { getHead: () => head } }
    }, {
      window: { location: { search: '' } },
      document: createDocumentMock()
    });

    await import('@/js/snippets/newsletter');

    expect(mock.util.dom.append).toHaveBeenCalledWith(head, expect.stringContaining('newsletter.css'));
    expect(mock.util.dom.loadById).toHaveBeenCalledWith('newsletter', '/kame-house/html-snippets/newsletter.html', null);
    expect(mock.extension.newsletter).toBeDefined();
  });
});
