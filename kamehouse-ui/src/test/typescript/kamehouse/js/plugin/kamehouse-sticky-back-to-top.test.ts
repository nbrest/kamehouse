/// <reference types="vitest/globals" />

import { describe, it, expect, vi, beforeEach } from 'vitest';
import { installKameHouseMock, createDocumentMock } from '../../../helpers/kamehouse-mock';

describe('kamehouse-sticky-back-to-top', () => {
  beforeEach(() => {
    vi.resetModules();
  });

  async function loadPlugin(windowOverrides: any = {}) {
    const addEventListener = vi.fn();
    const button = {};
    const mock = installKameHouseMock({}, {
      window: { addEventListener, location: { search: '' }, scrollY: 0, ...windowOverrides },
      document: createDocumentMock({ 'btn-sticky-back-to-top': button })
    });
    await import('@/kamehouse/js/plugin/kamehouse-sticky-back-to-top');
    await mock.plugin.stickyBackToTop.load();
    return { mock, addEventListener, button };
  }

  it('loads the plugin and sets up the event handlers', async () => {
    const { mock, addEventListener, button } = await loadPlugin();

    expect(mock.logger.info).toHaveBeenCalledWith('Started initializing sticky back to top', null);
    expect(mock.util.fetch.loadHtmlSnippet).toHaveBeenCalled();
    expect(mock.util.dom.append).toHaveBeenCalled();
    expect(addEventListener).toHaveBeenCalledWith('scroll', expect.any(Function));
    expect(mock.util.dom.setClick).toHaveBeenCalledWith(button, null, expect.any(Function));
    expect(mock.core.backToTop).toHaveBeenCalled();
  });

  it('shows the button when scrolled down', async () => {
    const { mock, addEventListener, button } = await loadPlugin();
    (global as any).window.scrollY = 100;
    const scrollHandler = addEventListener.mock.calls[0][1];

    scrollHandler();

    expect(mock.util.dom.classListAdd).toHaveBeenCalledWith(button, 'active');
    expect(mock.util.dom.classListRemove).toHaveBeenCalledWith(button, 'hidden');
  });

  it('hides the button when at the top', async () => {
    const { mock, addEventListener, button } = await loadPlugin();
    (global as any).window.scrollY = 0;
    const scrollHandler = addEventListener.mock.calls[0][1];

    scrollHandler();

    expect(mock.util.dom.classListAdd).toHaveBeenCalledWith(button, 'hidden');
    expect(mock.util.dom.classListRemove).toHaveBeenCalledWith(button, 'active');
  });
});
