/// <reference types="vitest/globals" />

import { describe, it, expect, vi, beforeEach } from 'vitest';
import { installKameHouseMock, createDocumentMock } from '../../helpers/kamehouse-mock';

describe('kamehouse-groot kamehouse-shell', () => {
  let get: any;

  beforeEach(() => {
    vi.resetModules();
    get = vi.fn();
  });

  function install() {
    return installKameHouseMock({
      http: { get },
      plugin: { debugger: { http: { get } } },
      util: {
        dom: {
          getTbody: vi.fn(() => ({})),
          getTrTd: vi.fn(() => ({})),
          replaceWith: vi.fn()
        }
      }
    }, {
      window: { location: { search: '' } },
      document: createDocumentMock({
        'kamehouse-shell-output-table-body': {}
      })
    });
  }

  async function importShell() {
    const mock = install();
    await import('@/kamehouse-groot/js/kamehouse-shell');
    return { mock, shell: mock.extension.kameHouseShell };
  }

  it('loads the shell and marks the module as loaded', async () => {
    const { mock, shell } = await importShell();
    expect(shell).toBeDefined();
    expect(mock.logger.info).toHaveBeenCalledWith('Initialized kameHouseShell', null);
    expect(mock.util.module.setModuleLoaded).toHaveBeenCalledWith('kameHouseShell');
  });

  it('returns the default standard output', async () => {
    const { shell } = await importShell();
    expect(shell.getBashScriptStandardOutput()).toBe('Script output not set yet.');
  });

  it('logs an error when no script is specified', async () => {
    const { mock, shell } = await importShell();
    shell.execute('', null, 'false', 'false', null, vi.fn(), vi.fn());
    expect(mock.logger.error).toHaveBeenCalledWith('No script specified to execute', 'No script specified to execute');
  });

  it('executes a script without args and shows the full output', async () => {
    const { mock, shell } = await importShell();
    const callback = vi.fn();

    shell.execute('test.sh', null, 'false', 'false', null, callback, vi.fn());

    expect(get).toHaveBeenCalledTimes(1);
    const call = get.mock.calls[0];
    expect(call[1]).toBe('/kame-house-groot/api/v1/admin/kamehouse-shell/execute.php');
    expect(call[3]).toEqual({ script: 'test.sh', args: null, executeOnDockerHost: 'false', isDaemon: 'false' });
    expect(call[0].timeout).toBeUndefined();
    expect(mock.util.dom.setHtmlById).toHaveBeenCalledWith('kamehouse-shell-output-executing', expect.anything());

    mock.util.dom.append.mockClear();
    call[4]({ standardOutput: 'out', standardOutputHtml: ['line1', ' ', 'line2'] }, 200, '', {});
    expect(shell.getBashScriptStandardOutput()).toBe('out');
    expect(mock.util.dom.append).toHaveBeenCalledTimes(2);
    expect(mock.util.dom.replaceWith).toHaveBeenCalled();
    expect(callback).toHaveBeenCalled();
  });

  it('executes a script with args and a timeout', async () => {
    const { mock, shell } = await importShell();

    shell.execute('test.sh', '-a', 'true', 'true', '30', vi.fn(), vi.fn());

    const call = get.mock.calls[0];
    expect(call[0].timeout).toBe('30');
    expect(mock.logger.info).toHaveBeenCalledWith("Executing script : test.sh with args : '-a' and executeOnDockerHost: true, isDaemon: true, timeout: 30", null);
    expect(mock.util.dom.setHtmlById).toHaveBeenCalledWith('kamehouse-shell-output-executing', expect.anything());
  });

  it('shows only the first and last lines for long output', async () => {
    const { mock, shell } = await importShell();

    shell.execute('test.sh', null, 'false', 'false', null, vi.fn(), vi.fn());
    const lines = Array.from({ length: 500 }, (_, i) => 'line' + i);
    get.mock.calls[0][4]({ standardOutput: 'out', standardOutputHtml: lines }, 200, '', {});

    expect(mock.util.dom.append.mock.calls.length).toBeGreaterThan(50);
    expect(mock.util.dom.getTrTd).toHaveBeenCalledWith(expect.stringContaining('Script output is too long'));
  });

  it('returns early when the output is empty', async () => {
    const { mock, shell } = await importShell();

    shell.execute('test.sh', null, 'false', 'false', null, vi.fn(), vi.fn());
    get.mock.calls[0][4]({ standardOutputHtml: null }, 200, '', {});

    expect(mock.util.dom.replaceWith).not.toHaveBeenCalled();
  });

  it('shows an error response', async () => {
    const { mock, shell } = await importShell();
    const callback = vi.fn();

    shell.execute('test.sh', null, 'false', 'false', null, vi.fn(), callback);
    get.mock.calls[0][5]({ error: 'bad' }, 500, 'Server Error', {});

    expect(mock.util.dom.getTrTd).toHaveBeenCalledWith('Error response from the backend');
    expect(mock.util.dom.getTrTd).toHaveBeenCalledWith('responseCode : 500');
    expect(mock.util.dom.replaceWith).toHaveBeenCalled();
    expect(callback).toHaveBeenCalled();
  });

  it('uses a custom execute api', async () => {
    const { shell } = await importShell();
    shell.setExecuteApi('/custom/execute.php');
    shell.execute('test.sh', null, 'false', 'false', null, vi.fn(), vi.fn());
    expect(get.mock.calls[0][1]).toBe('/custom/execute.php');
  });
});
