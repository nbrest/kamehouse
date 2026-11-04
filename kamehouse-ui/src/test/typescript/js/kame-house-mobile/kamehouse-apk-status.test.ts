/// <reference types="vitest/globals" />

import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { installKameHouseMock, createDocumentMock } from '../../helpers/kamehouse-mock';

describe('kamehouse-apk-status', () => {
  beforeEach(() => {
    vi.resetModules();
    vi.useFakeTimers();
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  it('sends the apk status loaded message to the parent', async () => {
    const addEventListener = vi.fn();
    const postMessage = vi.fn();
    const mock = installKameHouseMock({}, {
      window: { addEventListener, parent: { postMessage }, location: { search: '' } },
      document: createDocumentMock()
    });

    await import('@/js/kame-house-mobile/kamehouse-apk-status');

    expect(addEventListener).toHaveBeenCalledWith('message', expect.any(Function), false);
    expect(postMessage).toHaveBeenCalledWith('apk-status-loaded', '*');
    expect(mock.extension.apkStatus).toBeDefined();
  });

  it('ignores messages that are not acks', async () => {
    const addEventListener = vi.fn();
    const postMessage = vi.fn();
    const mock = installKameHouseMock({}, {
      window: { addEventListener, parent: { postMessage }, location: { search: '' } },
      document: createDocumentMock()
    });

    await import('@/js/kame-house-mobile/kamehouse-apk-status');
    const handler = addEventListener.mock.calls[0][1];
    handler({ data: 'other' });

    expect(mock.logger.info).not.toHaveBeenCalledWith('APK status iframe rendered in parent', null);
  });

  it('marks the status as rendered after receiving the ack', async () => {
    const addEventListener = vi.fn();
    const postMessage = vi.fn();
    const mock = installKameHouseMock({}, {
      window: { addEventListener, parent: { postMessage }, location: { search: '' } },
      document: createDocumentMock()
    });

    await import('@/js/kame-house-mobile/kamehouse-apk-status');
    const handler = addEventListener.mock.calls[0][1];
    handler({ data: 'apk-status-loaded-ack' });

    expect(mock.logger.info).toHaveBeenCalledWith('APK status iframe rendered in parent', null);

    postMessage.mockClear();
    vi.advanceTimersByTime(3000);
    expect(postMessage).not.toHaveBeenCalled();
    expect(mock.logger.info).toHaveBeenCalledWith('Apk status is already rendered in the parent', null);
  });
});
