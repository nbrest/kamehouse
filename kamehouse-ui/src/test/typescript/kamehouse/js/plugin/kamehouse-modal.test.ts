/// <reference types="vitest/globals" />

import { describe, it, expect, vi, beforeEach } from 'vitest';
import { installKameHouseMock, createDocumentMock } from '../../../helpers/kamehouse-mock';

describe('kamehouse-modal', () => {
  beforeEach(() => {
    vi.resetModules();
  });

  async function loadPlugin(overrides: any = {}) {
    const mock = installKameHouseMock(overrides, {
      window: { location: { search: '' } },
      document: createDocumentMock()
    });
    await import('@/kamehouse/js/plugin/kamehouse-modal');
    await mock.plugin.modal.load();
    return mock;
  }

  it('loads the modal framework and both modals', async () => {
    const mock = await loadPlugin();

    expect(mock.logger.info).toHaveBeenCalledWith('Started initializing kamehouse modal framework', null);
    expect(mock.util.fetch.loadHtmlSnippet).toHaveBeenCalled();
    expect(mock.util.module.setModuleLoaded).toHaveBeenCalledWith('kameHouseModal');
    expect(mock.plugin.modal.basicModal).toBeDefined();
    expect(mock.plugin.modal.loadingWheelModal).toBeDefined();
  });

  it('opens, closes and resets the basic modal', async () => {
    const mock = await loadPlugin();
    const modal = mock.plugin.modal.basicModal;

    modal.open('hello');
    expect(mock.util.dom.setHtmlById).toHaveBeenCalledWith('kamehouse-modal-basic-text', 'hello');
    expect(mock.util.dom.setDisplay).toHaveBeenCalledWith(expect.anything(), 'block');

    modal.close();
    expect(mock.util.dom.setDisplay).toHaveBeenCalledWith(expect.anything(), 'none');

    modal.appendHtml('more');
    expect(mock.util.dom.appendById).toHaveBeenCalledWith('kamehouse-modal-basic-text', 'more');

    modal.setIsErrorMessage(true);
    expect(modal.isErrorMessage()).toBe(true);
    modal.reset();
    expect(modal.isErrorMessage()).toBe(false);
  });

  it('does not open a modal that is already open', async () => {
    const mock = await loadPlugin();
    const modal = mock.plugin.modal.basicModal;

    modal.open('first');
    modal.open('second');

    expect(mock.logger.error).toHaveBeenCalled();
  });

  it('opens the site under construction modal', async () => {
    const mock = await loadPlugin();
    const modal = mock.plugin.modal.basicModal;

    modal.openSiteUnderConstruction();

    expect(mock.util.dom.setDisplay).toHaveBeenCalledWith(expect.anything(), 'block');
  });

  it('opens an api error modal with an empty and a non-empty body', async () => {
    const mock = await loadPlugin();
    const modal = mock.plugin.modal.basicModal;

    modal.openApiError(null, 500, 'Error', {});
    expect(mock.util.dom.setHtmlById).toHaveBeenCalled();

    modal.close();
    modal.openApiError({ message: 'boom' }, 400, 'Bad request', {});
    expect(mock.json.stringify).toBeDefined();
  });

  it('auto closes the modal', async () => {
    const mock = await loadPlugin();
    const modal = mock.plugin.modal.basicModal;

    modal.open('auto');
    await modal.autoClose(2000);

    expect(mock.util.dom.classListRemoveById).toHaveBeenCalledWith('kamehouse-modal-basic-autoclose', 'hidden-kh');
    expect(mock.util.dom.classListAddById).toHaveBeenCalledWith('kamehouse-modal-basic-autoclose', 'hidden-kh');
  });

  it('uses the default auto close time when none is provided', async () => {
    const mock = await loadPlugin();
    const modal = mock.plugin.modal.basicModal;

    modal.open('auto');
    await modal.autoClose(null);

    expect(mock.logger.trace).toHaveBeenCalled();
    expect(mock.core.sleep).toHaveBeenCalled();
  });

  it('stops auto closing if the modal was closed', async () => {
    const mock = await loadPlugin();
    const modal = mock.plugin.modal.basicModal;

    modal.open('auto');
    modal.close();
    await modal.autoClose(1000);

    expect(mock.logger.debug).toHaveBeenCalledWith(expect.stringContaining('already closed'), null);
  });

  it('opens the loading wheel modal with the session first name', async () => {
    const mock = await loadPlugin({ session: { firstName: 'Goku' } });
    const modal = mock.plugin.modal.loadingWheelModal;

    modal.open(null);

    expect(mock.util.dom.setHtmlById).toHaveBeenCalledWith('kamehouse-modal-loading-wheel-text', expect.stringContaining('Goku'));
  });

  it('opens the loading wheel modal with an explicit message', async () => {
    const mock = await loadPlugin();
    const modal = mock.plugin.modal.loadingWheelModal;

    modal.open('Loading');

    expect(mock.util.dom.setHtmlById).toHaveBeenCalledWith('kamehouse-modal-loading-wheel-text', 'Loading');
  });
});
