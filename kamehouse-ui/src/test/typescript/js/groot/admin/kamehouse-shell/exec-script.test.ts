/// <reference types="vitest/globals" />

import { describe, it, expect, vi, beforeEach } from 'vitest';
import { installKameHouseMock, createDocumentMock } from '../../../../helpers/kamehouse-mock';

describe('groot exec-script', () => {
  let execute: any;
  let downloadLink: any;

  beforeEach(() => {
    vi.resetModules();
    execute = vi.fn();
    downloadLink = { click: vi.fn() };
  });

  function install(search: string, server: any = 'server1') {
    return installKameHouseMock({
      extension: {
        kameHouseShell: {
          execute,
          getBashScriptStandardOutput: vi.fn(() => 'bash output')
        },
        groot: { session: { server } }
      },
      util: {
        dom: {
          getA: vi.fn(() => downloadLink),
          getBody: vi.fn(() => ({})),
          appendChild: vi.fn(),
          removeChild: vi.fn()
        }
      }
    }, {
      window: { location: { search } },
      document: createDocumentMock()
    });
  }

  async function importLoader(search = '') {
    const mock = install(search);
    await import('@/js/groot/admin/kamehouse-shell/exec-script');
    return { mock, loader: mock.extension.execScriptLoader };
  }

  it('loads the exec script loader and reads url params', async () => {
    const { mock, loader } = await importLoader('?script=test.sh&args=-a&executeOnDockerHost=true&isDaemon=false');
    expect(loader).toBeDefined();
    expect(mock.util.banner.setRandomAllBanner).toHaveBeenCalledWith(null);
    expect(mock.util.dom.setHtmlById).toHaveBeenCalledWith('st-script-name', 'test.sh');
    expect(mock.util.dom.setHtmlById).toHaveBeenCalledWith('st-script-args', '-a');
    expect(mock.util.dom.setHtmlById).toHaveBeenCalledWith('st-script-exec-docker-host', 'true');
    expect(mock.util.dom.setHtmlById).toHaveBeenCalledWith('st-script-is-daemon', 'false');
    expect(mock.util.dom.setHtmlById).toHaveBeenCalledWith('st-server-name', 'server1');
  });

  it('does not update the server name when the session has no server', async () => {
    const mock = install('', '');
    await import('@/js/groot/admin/kamehouse-shell/exec-script');
    expect(mock.util.dom.setHtmlById).not.toHaveBeenCalledWith('st-server-name', expect.anything());
  });

  it('executes a script from url params and handles success', async () => {
    const { mock, loader } = await importLoader('?script=test.sh&args=-a&executeOnDockerHost=true&isDaemon=false&timeout=10');

    loader.executeFromUrlParams();

    expect(mock.util.dom.classListAddById).toHaveBeenCalledWith('kamehouse-shell-output-header', 'hidden-kh');
    expect(mock.util.dom.setHtmlById).toHaveBeenCalledWith('banner-script-status', 'in progress...');
    expect(execute).toHaveBeenCalledTimes(1);
    const call = execute.mock.calls[0];
    expect(call.slice(0, 5)).toEqual(['test.sh', '-a', 'true', 'false', '10']);

    call[5]();
    expect(mock.util.dom.classListRemoveById).toHaveBeenCalledWith('kamehouse-shell-output-header', 'hidden-kh');
    expect(mock.util.dom.setHtmlById).toHaveBeenCalledWith('banner-script-status', 'finished!');
  });

  it('handles script execution errors', async () => {
    const { mock, loader } = await importLoader('?script=test.sh');
    loader.executeFromUrlParams();

    execute.mock.calls[0][6]();
    expect(mock.util.dom.classListRemoveById).toHaveBeenCalledWith('btn-execute-script', 'hidden-kh');
    expect(mock.util.dom.setHtmlById).toHaveBeenCalledWith('banner-script-status', 'finished!');
  });

  it('downloads the bash script output', async () => {
    const { mock, loader } = await importLoader();

    loader.downloadBashScriptOutput();

    expect(mock.util.dom.getA).toHaveBeenCalled();
    expect(mock.util.dom.appendChild).toHaveBeenCalled();
    expect(downloadLink.click).toHaveBeenCalled();
    expect(mock.util.dom.removeChild).toHaveBeenCalled();
  });

  it('returns the shell extension', async () => {
    const { mock, loader } = await importLoader();
    expect(loader.getShell()).toBe(mock.extension.kameHouseShell);
  });
});
