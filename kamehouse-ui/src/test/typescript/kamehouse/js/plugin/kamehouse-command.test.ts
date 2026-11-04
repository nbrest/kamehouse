/// <reference types="vitest/globals" />

import { describe, it, expect, vi, beforeEach } from 'vitest';
import { installKameHouseMock, createDocumentMock } from '../../../helpers/kamehouse-mock';

describe('kamehouse-command', () => {
  let resultDiv: any;

  beforeEach(() => {
    vi.resetModules();
    resultDiv = {};
  });

  function install() {
    return installKameHouseMock({}, {
      window: { location: { search: '' } },
      document: createDocumentMock({ 'kamehouse-command-result': resultDiv })
    });
  }

  it('loads the command manager plugin', async () => {
    const mock = install();
    await import('@/kamehouse/js/plugin/kamehouse-command');
    expect(mock.logger.info).toHaveBeenCalledWith('Started initializing kameHouseCommandManager', null);
    expect(mock.plugin.kameHouseCommandManager).toBeDefined();
  });

  it('renders command results with output, errors and a running daemon', async () => {
    const mock = install();
    await import('@/kamehouse/js/plugin/kamehouse-command');
    const manager = mock.plugin.kameHouseCommandManager;

    manager.renderKameHouseCommandResult([
      {
        command: 'ls -la',
        standardOutputHtml: ['line1', 'line2'],
        standardErrorHtml: ['error1'],
        status: 'running'
      },
      {
        command: 'whoami',
        standardOutputHtml: [],
        standardErrorHtml: [],
        status: 'finished'
      }
    ], true, null, 'btn-id');

    expect(mock.util.dom.empty).toHaveBeenCalledWith(resultDiv);
    expect(mock.util.dom.append).toHaveBeenCalled();
    expect(mock.util.collapsibleDiv.resize).toHaveBeenCalledWith('btn-id');
  });

  it('renders command results without the command line and with a custom div id', async () => {
    const customDiv = {};
    const mock = install();
    (global as any).document = createDocumentMock({ 'custom-result': customDiv });
    await import('@/kamehouse/js/plugin/kamehouse-command');
    const manager = mock.plugin.kameHouseCommandManager;

    manager.renderKameHouseCommandResult([], false, 'custom-result', 'btn-id');

    expect(mock.util.dom.empty).toHaveBeenCalledWith(customDiv);
  });

  it('renders an error executing the command', async () => {
    const mock = install();
    await import('@/kamehouse/js/plugin/kamehouse-command');
    const manager = mock.plugin.kameHouseCommandManager;

    manager.renderErrorExecutingCommand('btn-id');

    expect(mock.util.dom.empty).toHaveBeenCalledWith(resultDiv);
    expect(mock.util.dom.append).toHaveBeenCalledWith(resultDiv, expect.stringContaining('Error executing kamehouse command'));
    expect(mock.util.collapsibleDiv.resize).toHaveBeenCalledWith('btn-id');
  });
});
