/// <reference types="vitest/globals" />

import { describe, it, expect, vi, beforeEach } from 'vitest';
import { installKameHouseMock, createDocumentMock } from '../helpers/kamehouse-mock';

describe('downloads', () => {
  let addEventListener: any;
  let iframe: any;
  let notAvailableDiv: any;

  beforeEach(() => {
    vi.resetModules();
    addEventListener = vi.fn();
    iframe = { contentWindow: { postMessage: vi.fn() } };
    notAvailableDiv = {};
    const doc = createDocumentMock({
      'apk-status-iframe': iframe,
      'apk-status-not-available': notAvailableDiv
    });
    installKameHouseMock({
      plugin: { slideshow: { setDotSlide: vi.fn() } }
    }, {
      window: { addEventListener, location: { search: '' } },
      document: doc
    });
  });

  it('loads the downloads manager and configures the slideshow', async () => {
    const mock = (global as any).kameHouse;
    await import('@/js/downloads');

    expect(mock.util.banner.setRandomAllBanner).toHaveBeenCalled();
    expect(mock.util.module.waitForModules).toHaveBeenCalledWith(['slideshow'], expect.any(Function));
    expect(mock.plugin.slideshow.setDotSlide).toHaveBeenCalledWith(1);
    expect(addEventListener).toHaveBeenCalledWith('message', expect.any(Function), false);
    expect(mock.extension.downloadsManager).toBeDefined();
  });

  it('ignores messages from unexpected origins', async () => {
    const mock = (global as any).kameHouse;
    await import('@/js/downloads');
    const handler = addEventListener.mock.calls[0][1];

    handler({ origin: 'https://evil.example.com', data: 'apk-status-loaded' });

    expect(mock.logger.trace).toHaveBeenCalledWith('Ignoring message from unexpected origin', null);
    expect(iframe.contentWindow.postMessage).not.toHaveBeenCalled();
  });

  it('ignores messages that are not apk-status-loaded', async () => {
    const mock = (global as any).kameHouse;
    await import('@/js/downloads');
    const handler = addEventListener.mock.calls[0][1];

    handler({ origin: 'https://kame.nicobrest.com', data: 'something-else' });

    expect(iframe.contentWindow.postMessage).not.toHaveBeenCalled();
    expect(mock.util.dom.classListRemove).not.toHaveBeenCalled();
  });

  it('acknowledges apk-status-loaded messages from the expected origin', async () => {
    const mock = (global as any).kameHouse;
    await import('@/js/downloads');
    const handler = addEventListener.mock.calls[0][1];

    handler({ origin: 'https://kame.nicobrest.com', data: 'apk-status-loaded' });

    expect(iframe.contentWindow.postMessage).toHaveBeenCalledWith('apk-status-loaded-ack', '*');
    expect(mock.util.dom.classListRemove).toHaveBeenCalledWith(iframe, 'hidden-kh');
    expect(mock.util.dom.classListAdd).toHaveBeenCalledWith(notAvailableDiv, 'hidden-kh');
  });
});
