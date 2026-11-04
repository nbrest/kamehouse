/// <reference types="vitest/globals" />

import { describe, it, expect, vi, beforeEach } from 'vitest';
import { installKameHouseMock, createDocumentMock } from '../../helpers/kamehouse-mock';

describe('settings', () => {
  beforeEach(() => {
    vi.resetModules();
    window.history.pushState({}, '', '/');
  });

  async function loadSettings(search = '') {
    window.history.pushState({}, '', '/' + search);
    const openAutoCloseable = vi.fn();
    const renderCustomDebugger = vi.fn();
    const mock = installKameHouseMock({
      extension: {
        mobile: {
          core: {
            setMobileBuildInfo: vi.fn(),
            setBatcaveBuildInfo: vi.fn()
          }
        }
      },
      plugin: {
        mobileTabsManager: { openTab: vi.fn() },
        modal: {
          basicModal: {
            openAutoCloseable,
            open: vi.fn(),
            close: vi.fn(),
            reset: vi.fn(),
            setHtml: vi.fn(),
            setIsErrorMessage: vi.fn(),
            isErrorMessage: () => false
          },
          loadingWheelModal: { open: vi.fn(), close: vi.fn() }
        },
        debugger: {
          http: { get: vi.fn(), post: vi.fn() },
          renderCustomDebugger
        }
      }
    }, { document: createDocumentMock() });

    await import('@/kame-house-mobile/js/settings');
    return { mock, openAutoCloseable, renderCustomDebugger };
  }

  it('loads the settings page without url params', async () => {
    const { mock, openAutoCloseable, renderCustomDebugger } = await loadSettings();

    expect(mock.logger.info).toHaveBeenCalledWith('Started initializing kamehouse-mobile app settings page', null);
    expect(mock.util.banner.setRandomAllBanner).toHaveBeenCalledWith(null);
    expect(mock.plugin.mobileTabsManager.openTab).toHaveBeenCalledWith('tab-backend');
    expect(renderCustomDebugger).toHaveBeenCalledWith(
      '/kame-house-mobile/kamehouse-mobile/plugin/html/settings-debug-mode-custom.html',
      expect.any(Function)
    );

    const debugCallback = renderCustomDebugger.mock.calls[0][1];
    debugCallback();
    expect(mock.extension.mobile.core.setMobileBuildInfo).toHaveBeenCalled();
    expect(mock.extension.mobile.core.setBatcaveBuildInfo).toHaveBeenCalled();
    expect(openAutoCloseable).not.toHaveBeenCalled();
  });

  it('shows a request timeout error modal', async () => {
    const { mock, openAutoCloseable } = await loadSettings('?requestTimeout=1000&responseCode=504');

    expect(mock.logger.error).toHaveBeenCalledWith(
      'Unable to connect to the backend. Try again later. responseCode: 504',
      'Unable to connect to the backend. Try again later. responseCode: 504'
    );
    expect(openAutoCloseable).toHaveBeenCalledWith(expect.anything(), 5000);
    expect(mock.util.dom.getImg).toHaveBeenCalled();
    expect(mock.util.dom.append).toHaveBeenCalled();
  });

  it('shows a ssl error modal', async () => {
    const { mock, openAutoCloseable } = await loadSettings('?sslError=true&responseCode=495');

    expect(mock.logger.error).toHaveBeenCalledWith(
      'SSL error connecting to the backend. Check SSL certificate on server or skip ssl validation. responseCode: 495',
      'SSL error connecting to the backend. Check SSL certificate on server or skip ssl validation. responseCode: 495'
    );
    expect(openAutoCloseable).toHaveBeenCalledWith(expect.anything(), 5000);
  });

  it('shows an unauthorized page access modal', async () => {
    const { mock, openAutoCloseable } = await loadSettings('?unauthorizedPageAccess=true&responseCode=403');

    expect(mock.logger.error).toHaveBeenCalledWith(
      'User is not authorized to access the page. Login with an authorized user. responseCode: 403',
      'User is not authorized to access the page. Login with an authorized user. responseCode: 403'
    );
    expect(openAutoCloseable).toHaveBeenCalledWith(expect.anything(), 5000);
  });
});
