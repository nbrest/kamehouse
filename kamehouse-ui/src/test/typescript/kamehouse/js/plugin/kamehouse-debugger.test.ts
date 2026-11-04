/// <reference types="vitest/globals" />

import { describe, it, expect, vi, beforeEach } from 'vitest';
import { installKameHouseMock, createDocumentMock } from '../../../helpers/kamehouse-mock';

function flush() {
  return new Promise((resolve) => setTimeout(resolve, 0));
}

describe('kamehouse-debugger', () => {
  let mock: any;
  let doc: any;
  let logLevelDropdown: any;
  let requestElements: any[];
  let responseElements: any[];
  let httpGet: any;
  let httpPut: any;
  let httpPost: any;
  let httpDelete: any;

  beforeEach(() => {
    vi.resetModules();
  });

  function install() {
    logLevelDropdown = { value: 'INFO', options: [] };
    requestElements = [{ id: 'req1' }, { id: 'req2' }];
    responseElements = [{ id: 'res1' }];
    httpGet = vi.fn();
    httpPut = vi.fn();
    httpPost = vi.fn();
    httpDelete = vi.fn();

    doc = createDocumentMock({
      'debug-mode': { classList: { add: vi.fn(), remove: vi.fn() } },
      'debug-mode-log-level-dropdown': logLevelDropdown,
      'debug-mode-custom-wrapper': {},
      'debugger-http-client': { id: 'debugger-http-client' },
      'debugger-http-client-req-header': { id: 'req-header' },
      'debugger-http-client-res-header': { id: 'res-header' }
    });
    doc.getElementsByClassName = vi.fn((name: string) => {
      if (name === 'debug-mode-request-data') return requestElements;
      if (name === 'debug-mode-response-data') return responseElements;
      return [];
    });

    mock = installKameHouseMock({
      logger: {
        getGreenText: vi.fn((t: any) => t),
        getBlueText: vi.fn((t: any) => t),
        getYellowText: vi.fn((t: any) => t),
        getCyanText: vi.fn((t: any) => t),
        getRedText: vi.fn((t: any) => t),
        setLogLevel: vi.fn(),
        getLogLevel: vi.fn(() => 'INFO'),
        maskSensitiveData: vi.fn((t: any) => t)
      },
      util: {
        dom: {
          setTextById: vi.fn(),
          toggleClassOnElement: vi.fn()
        }
      },
      plugin: {
        modal: {
          basicModal: { openAutoCloseable: vi.fn(), setHtml: vi.fn(), appendHtml: vi.fn(), open: vi.fn(), close: vi.fn() }
        }
      },
      http: {
        get: httpGet,
        put: httpPut,
        post: httpPost,
        delete: httpDelete
      }
    }, {
      window: { location: { search: '' } },
      document: doc
    });
    return mock;
  }

  async function importDebugger() {
    install();
    await import('@/kamehouse/js/plugin/kamehouse-debugger');
    await flush();
    return mock.plugin.debugger;
  }

  it('loads the debugger plugin and renders its template', async () => {
    const debuggerPlugin = await importDebugger();

    expect(debuggerPlugin).toBeDefined();
    expect(debuggerPlugin.http).toBeDefined();
    expect(mock.logger.info).toHaveBeenCalledWith('Started initializing kameHouseDebugger', null);
    expect(mock.util.dom.append).toHaveBeenCalledWith(expect.anything(), expect.stringContaining('kamehouse-debugger.css'));
    expect(mock.util.fetch.loadHtmlSnippet).toHaveBeenCalledWith('/kame-house/kamehouse/html/plugin/kamehouse-debugger-http-client-table.html');
    expect(mock.logger.debug).toHaveBeenCalledWith('Loaded debuggerHttpClientDivTemplate', null);
    expect(mock.util.dom.loadById).toHaveBeenCalledWith('debug-mode-wrapper', '/kame-house/kamehouse/html/plugin/kamehouse-debugger.html', expect.any(Function));
    expect(mock.util.module.setModuleLoaded).toHaveBeenCalledWith('kameHouseDebugger');
    expect(mock.util.dom.empty).toHaveBeenCalledWith(doc.getElementById('debugger-http-client'));
    expect(mock.util.dom.setHtmlById).toHaveBeenCalledWith('debugger-http-client', '<div></div>');
    expect(mock.core.configDynamicHtml).toHaveBeenCalled();
  });

  it('sets the selected log level option in the dropdown', async () => {
    const optionInfo = { value: 'INFO', selected: false };
    const optionDebug = { value: 'DEBUG', selected: false };
    logLevelDropdown.options = [optionInfo, optionDebug];
    mock = install();
    logLevelDropdown.options = [optionInfo, optionDebug];
    mock.util.dom.loadById = vi.fn((id: any, url: any, cb: any) => cb());
    await import('@/kamehouse/js/plugin/kamehouse-debugger');
    await flush();

    expect(optionInfo.selected).toBe(true);
    expect(optionDebug.selected).toBe(false);
    expect(mock.logger.debug).toHaveBeenCalledWith('Updating debugger console log level dropdown to INFO', null);
  });

  it('toggles debug mode', async () => {
    const debuggerPlugin = await importDebugger();
    debuggerPlugin.toggleDebugMode();
    expect(mock.plugin.modal.basicModal.openAutoCloseable).toHaveBeenCalledWith(expect.anything(), 1000);
    expect(mock.logger.info).toHaveBeenCalledWith('Toggled debug mode', 'Toggled debug mode');
    expect(mock.util.dom.toggleClassOnElement).toHaveBeenCalledWith(doc.getElementById('debug-mode'), 'hidden-kh');
  });

  it.each([
    ['ERROR', 'error', 'getRedText'],
    ['WARN', 'warn', 'getYellowText'],
    ['INFO', 'info', 'getBlueText'],
    ['DEBUG', 'debug', 'getGreenText'],
    ['TRACE', 'trace', 'getCyanText']
  ])('sets the console log level to %s', async (level, loggerMethod, colorMethod) => {
    const debuggerPlugin = await importDebugger();
    logLevelDropdown.value = level;
    logLevelDropdown.options = [{ selected: true, textContent: level }];

    debuggerPlugin.setConsoleLogLevel();
    expect(mock.logger.setLogLevel).toHaveBeenCalledWith(level);
    expect(mock.logger[colorMethod]).toHaveBeenCalledWith('Set log level to ' + level);
    expect(mock.logger[loggerMethod]).toHaveBeenCalledWith('Set log level to ' + level, 'Set log level to ' + level);
    expect(mock.util.cookies.setCookie).toHaveBeenCalledWith('kh-log-level', level, null);
  });

  it('renders a custom debugger snippet', async () => {
    const debuggerPlugin = await importDebugger();
    const callback = vi.fn();
    debuggerPlugin.renderCustomDebugger('<div>custom</div>', callback);
    expect(mock.util.dom.loadById).toHaveBeenCalledWith('debug-mode-custom-wrapper', '<div>custom</div>', callback);
  });

  it('displays the previous requests table and trims long bodies', async () => {
    const debuggerPlugin = await importDebugger();
    for (let i = 0; i < 9; i++) {
      debuggerPlugin.displayPreviousRequestsTable({ url: 'u' + i }, 'x'.repeat(1500), 200, {});
    }
    expect(mock.util.collapsibleDiv.resize).toHaveBeenCalledWith('debugger-http-client-previous-requests');
    expect(mock.util.dom.setTextById).toHaveBeenCalledWith('debugger-http-client-previous-requests-val', expect.stringContaining('trimmed'));
  });

  it('displays request and response data', async () => {
    const debuggerPlugin = await importDebugger();
    debuggerPlugin.displayRequestData({});
    expect(mock.util.dom.setHtmlById).not.toHaveBeenCalledWith('debugger-http-client-req-timestamp-val', 't');

    debuggerPlugin.displayRequestData({ timestamp: 't', method: 'GET', url: 'url', config: { a: 1 }, headers: { h: 'v' }, body: { b: 2 } });
    expect(mock.util.dom.setHtmlById).toHaveBeenCalledWith('debugger-http-client-req-method-val', 'GET');

    debuggerPlugin.displayResponseData({ ok: true }, 200, 'OK', { h: 'v' }, { timestamp: 't', method: 'GET' });
    expect(mock.util.dom.setHtmlById).toHaveBeenCalledWith('debugger-http-client-res-code-val', 200);
    expect(mock.util.dom.setTextById).toHaveBeenCalledWith('debugger-http-client-res-body-val', JSON.stringify({ ok: true }));
  });

  it('toggles request data visibility', async () => {
    const debuggerPlugin = await importDebugger();
    debuggerPlugin.toggleRequestData();
    expect(mock.util.dom.classListAdd).toHaveBeenCalledWith(requestElements[0], 'hidden-kh');
    expect(mock.util.dom.classListRemove).toHaveBeenCalledWith(doc.getElementById('debugger-http-client-req-header'), 'debug-mode-client-table-header-btn-expanded');

    debuggerPlugin.toggleRequestData();
    expect(mock.util.dom.classListRemove).toHaveBeenCalledWith(requestElements[0], 'hidden-kh');
    expect(mock.util.dom.classListAdd).toHaveBeenCalledWith(doc.getElementById('debugger-http-client-req-header'), 'debug-mode-client-table-header-btn-expanded');
  });

  it('toggles response data visibility', async () => {
    const debuggerPlugin = await importDebugger();
    debuggerPlugin.toggleResponseData();
    expect(mock.util.dom.classListRemove).toHaveBeenCalledWith(responseElements[0], 'hidden-kh');

    debuggerPlugin.toggleResponseData();
    expect(mock.util.dom.classListAdd).toHaveBeenCalledWith(responseElements[0], 'hidden-kh');
  });

  it('executes GET requests and processes success and error responses', async () => {
    const debuggerPlugin = await importDebugger();
    httpGet.mockImplementation((config: any, url: any, headers: any, body: any, success: any, error: any) => {
      success('ok', 200, 'OK', { h: 'v' });
      error('err', 500, 'ERR', {});
    });
    const success = vi.fn();
    const error = vi.fn();
    debuggerPlugin.http.get({}, 'url', {}, {}, success, error);
    expect(httpGet).toHaveBeenCalled();
    expect(success).toHaveBeenCalledWith('ok', 200, 'OK', { h: 'v' });
    expect(error).toHaveBeenCalledWith('err', 500, 'ERR', {});
  });

  it('executes PUT, POST and DELETE requests', async () => {
    const debuggerPlugin = await importDebugger();
    httpPut.mockImplementation((config: any, url: any, headers: any, body: any, success: any) => success('put', 200, 'OK', {}));
    httpPost.mockImplementation((config: any, url: any, headers: any, body: any, success: any) => success('post', 201, 'OK', {}));
    httpDelete.mockImplementation((config: any, url: any, headers: any, body: any, success: any) => success('delete', 204, 'OK', {}));
    const success = vi.fn();

    debuggerPlugin.http.put({}, 'url', {}, {}, success, vi.fn());
    debuggerPlugin.http.post({}, 'url', {}, {}, success, vi.fn());
    debuggerPlugin.http.delete({}, 'url', {}, {}, success, vi.fn());

    expect(success).toHaveBeenCalledWith('put', 200, 'OK', {});
    expect(success).toHaveBeenCalledWith('post', 201, 'OK', {});
    expect(success).toHaveBeenCalledWith('delete', 204, 'OK', {});
  });

  it('does not fail when no response callback is provided', async () => {
    const debuggerPlugin = await importDebugger();
    httpGet.mockImplementation((config: any, url: any, headers: any, body: any, success: any) => success('ok', 200, 'OK', {}));
    expect(() => debuggerPlugin.http.get({}, 'url', {}, {}, undefined, undefined)).not.toThrow();
  });
});
