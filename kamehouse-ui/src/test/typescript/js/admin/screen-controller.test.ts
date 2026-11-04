/// <reference types="vitest/globals" />

import { describe, it, expect, vi, beforeEach } from 'vitest';
import { installKameHouseMock, createDocumentMock } from '../../helpers/kamehouse-mock';

describe('admin screen-controller', () => {
  let post: any;
  let success: any;
  let failure: any;

  beforeEach(() => {
    vi.resetModules();
    success = null;
    failure = null;
    post = vi.fn((config: any, url: any, headers: any, body: any, successCb: any, errorCb: any) => {
      success = successCb;
      failure = errorCb;
    });
  });

  function install(doc: any = createDocumentMock({ 'key-presses-dropdown': { value: '3' } })) {
    return installKameHouseMock({
      plugin: {
        debugger: { http: { post } },
        modal: {
          loadingWheelModal: { open: vi.fn(), close: vi.fn() },
          basicModal: { openApiError: vi.fn() }
        }
      }
    }, {
      window: { location: { search: '' } },
      document: doc
    });
  }

  it('loads the screen controller', async () => {
    const mock = install();
    await import('@/js/admin/screen-controller');

    expect(mock.logger.info).toHaveBeenCalledWith('Started initializing screen controller', null);
    expect(mock.util.banner.setRandomAllBanner).toHaveBeenCalled();
    expect(mock.util.dom.append).toHaveBeenCalled();
    expect(mock.extension.screenController).toBeDefined();
  });

  it('sends a key press with the default number of presses', async () => {
    const mock = install();
    await import('@/js/admin/screen-controller');
    const controller = mock.extension.screenController;

    controller.keyPress('ENTER', null);

    expect(mock.logger.trace).toHaveBeenCalledWith('keyPresses not set. Using default value of 1', null);
    expect(post).toHaveBeenCalled();
    const call = post.mock.calls[0];
    expect(call[3]).toEqual({ key: 'ENTER', keyPresses: 1 });
  });

  it('sends a key press with the specified number of presses', async () => {
    const mock = install();
    await import('@/js/admin/screen-controller');
    const controller = mock.extension.screenController;

    controller.keyPress('ENTER', 5);

    expect(post.mock.calls[0][3]).toEqual({ key: 'ENTER', keyPresses: 5 });
  });

  it('sends a key press with the dropdown value', async () => {
    const mock = install();
    await import('@/js/admin/screen-controller');
    const controller = mock.extension.screenController;

    controller.keyPressWithDropdown('ENTER', 'key-presses-dropdown');

    expect(mock.logger.trace).toHaveBeenCalledWith('Number of key presses: 3', null);
    expect(post.mock.calls[0][3]).toEqual({ key: 'ENTER', keyPresses: '3' });
  });

  it('sends a right mouse click', async () => {
    const mock = install();
    await import('@/js/admin/screen-controller');
    const controller = mock.extension.screenController;

    controller.mouseRightClick();

    expect(post.mock.calls[0][3]).toEqual({ mouseButton: 'RIGHT', positionX: 500, positionY: 500, clickCount: 1 });
  });

  it('posts a request with a body', async () => {
    const mock = install();
    await import('@/js/admin/screen-controller');
    const controller = mock.extension.screenController;

    controller.post('/some-url', { data: 1 });

    expect(post).toHaveBeenCalledWith(expect.anything(), '/kame-house-admin/api/v1/admin/some-url', expect.objectContaining({ 'Content-Type': 'application/json' }), { data: 1 }, expect.any(Function), expect.any(Function));
  });

  it('posts a request without a body', async () => {
    const mock = install();
    await import('@/js/admin/screen-controller');
    const controller = mock.extension.screenController;

    controller.post('/some-url', null);

    expect(post).toHaveBeenCalledWith(expect.anything(), '/kame-house-admin/api/v1/admin/some-url', null, null, expect.any(Function), expect.any(Function));
  });

  it('processes success and error responses', async () => {
    const mock = install();
    await import('@/js/admin/screen-controller');
    const controller = mock.extension.screenController;

    controller.mouseRightClick();
    success('ok', 200, '', {});
    expect(mock.plugin.modal.loadingWheelModal.close).toHaveBeenCalled();

    controller.mouseRightClick();
    failure('error', 500, 'Server Error', {});
    expect(mock.plugin.modal.basicModal.openApiError).toHaveBeenCalled();
  });
});
