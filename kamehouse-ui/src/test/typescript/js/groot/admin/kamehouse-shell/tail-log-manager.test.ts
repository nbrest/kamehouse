/// <reference types="vitest/globals" />

import { describe, it, expect, vi, beforeEach } from 'vitest';
import { installKameHouseMock, createDocumentMock } from '../../../../helpers/kamehouse-mock';

describe('groot tail-log-manager', () => {
  let get: any;

  beforeEach(() => {
    vi.resetModules();
    get = vi.fn();
  });

  function install() {
    return installKameHouseMock({
      http: { get },
      logger: {
        getCyanText: (t: any) => t,
        getRedText: (t: any) => t
      },
      util: {
        dom: {
          getTbody: vi.fn(() => ({})),
          getTrTd: vi.fn(() => ({})),
          replaceWith: vi.fn()
        },
        collapsibleDiv: { resize: vi.fn() },
        time: { getTimestamp: vi.fn(() => '123') }
      }
    }, {
      window: { location: { search: '' } },
      document: createDocumentMock({
        'tail-log-output-table-body': {}
      })
    });
  }

  async function importManager() {
    const mock = install();
    await import('@/js/groot/admin/kamehouse-shell/tail-log-manager');
    return { mock, manager: mock.extension.tailLogManager };
  }

  it('loads the tail log manager', async () => {
    const { mock, manager } = await importManager();
    expect(manager).toBeDefined();
    expect(mock.logger.info).toHaveBeenCalledWith('Initialized tailLogManager', null);
  });

  it('tails a log with the default log level', async () => {
    const { mock, manager } = await importManager();
    const callback = vi.fn();

    manager.tailLog('catalina.out', 10, '', 'false', 'false', callback);

    expect(mock.logger.trace).toHaveBeenCalledWith('Tailing log: catalina.out', null);
    expect(get).toHaveBeenCalledTimes(1);
    const call = get.mock.calls[0];
    expect(call[1]).toBe('/kame-house-groot/api/v1/admin/kamehouse-shell/execute');
    expect(call[0].timeout).toBe(45);
    expect(call[3]).toEqual({ script: 'cat/cat-log.sh', args: '-f catalina.out -l ', executeOnDockerHost: 'false', isDaemon: 'false' });

    call[4]({ standardOutputHtml: ['line1', '  ', 'line2'] }, 200, '', {});
    expect(mock.util.dom.append).toHaveBeenCalledTimes(2);
    expect(mock.util.dom.replaceWith).toHaveBeenCalled();
    expect(mock.util.collapsibleDiv.resize).toHaveBeenCalledWith('tail-log-output-wrapper');
    expect(callback).toHaveBeenCalledWith({ standardOutputHtml: ['line1', '  ', 'line2'] });
  });

  it('tails groot logs with the tail log script', async () => {
    const { manager } = await importManager();

    manager.tailLog('groot', 10, 'ALL', 'false', 'false', vi.fn());
    manager.tailLog('groot', 10, 'INFO', 'false', 'false', vi.fn());

    const allLogsCall = get.mock.calls[0];
    expect(allLogsCall[1]).toBe('/kame-house-groot/api/v1/admin/kamehouse-shell/execute');
    expect(allLogsCall[3]).toEqual({ script: 'tail/tail-log.sh', args: '-f groot -q -n 2000', executeOnDockerHost: 'false', isDaemon: 'false' });

    const infoLogsCall = get.mock.calls[1];
    expect(infoLogsCall[3]).toEqual({ script: 'tail/tail-log.sh', args: '-f groot -q -n 2000 -l INFO', executeOnDockerHost: 'false', isDaemon: 'false' });
  });

  it('tails keep-alive-groot logs with the cat log script', async () => {
    const { manager } = await importManager();

    manager.tailLog('keep-alive-groot.log', 10, 'ALL', 'false', 'false', vi.fn());

    const call = get.mock.calls[0];
    expect(call[1]).toBe('/kame-house-groot/api/v1/admin/kamehouse-shell/execute');
    expect(call[3]).toEqual({ script: 'cat/cat-log.sh', args: '-f keep-alive-groot.log -l ALL', executeOnDockerHost: 'false', isDaemon: 'false' });
  });

  it('tails only the last lines when the output is long', async () => {
    const { mock, manager } = await importManager();

    manager.tailLog('catalina.out', 2, 'DEBUG', 'true', 'true');

    const call = get.mock.calls[0];
    expect(call[3].args).toBe('-f catalina.out -l DEBUG');
    call[4]({ standardOutputHtml: ['a', 'b', 'c', 'd'] }, 200, '', {});
    expect(mock.util.dom.append).toHaveBeenCalledTimes(2);
  });

  it('returns early when the output is empty', async () => {
    const { mock, manager } = await importManager();
    const callback = vi.fn();

    manager.tailLog('catalina.out', 10, '', 'false', 'false', callback);
    get.mock.calls[0][4]({ standardOutputHtml: null }, 200, '', {});

    expect(mock.util.dom.replaceWith).not.toHaveBeenCalled();
    expect(callback).not.toHaveBeenCalled();
  });

  it('logs a warning when tailing times out', async () => {
    const { mock, manager } = await importManager();
    const callback = vi.fn();

    manager.tailLog('catalina.out', 10, '', 'false', 'false', callback);
    get.mock.calls[0][5]('', '0', 'timeout', {});

    expect(mock.logger.warn).toHaveBeenCalledWith('Tail log request timed out', null);
    expect(mock.util.collapsibleDiv.resize).toHaveBeenCalledWith('tail-log-output-wrapper');
    expect(callback).toHaveBeenCalled();
  });

  it('appends an error message for other failures', async () => {
    const { mock, manager } = await importManager();

    manager.executeTailLog('/custom/url', 'catalina.out', 10, '', 'false', 'false', null);
    expect(get.mock.calls[0][1]).toBe('/custom/url');
    get.mock.calls[0][5]('body', 500, 'Server Error', {});

    expect(mock.util.dom.getTrTd).toHaveBeenCalledWith(expect.stringContaining('ERROR'));
    expect(mock.util.collapsibleDiv.resize).toHaveBeenCalledWith('tail-log-output-wrapper');
  });
});
