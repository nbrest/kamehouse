/// <reference types="vitest/globals" />

import { describe, it, expect, vi, beforeEach } from 'vitest';
import { installKameHouseMock, createDocumentMock } from '../../../../helpers/kamehouse-mock';

describe('groot kamehouse-shell-index', () => {
  let get: any;
  let getButton: any;
  let buttonOptions: any;

  beforeEach(() => {
    vi.resetModules();
    buttonOptions = null;
    get = vi.fn();
    getButton = vi.fn((options: any) => { buttonOptions = options; return {}; });
  });

  function install() {
    return installKameHouseMock({
      http: { get },
      core: { windowLocation: vi.fn() },
      util: {
        dom: {
          getButton,
          getTbody: vi.fn(() => ({})),
          getTrTd: vi.fn(() => ({})),
          replaceWith: vi.fn()
        },
        table: { filterTableRows: vi.fn() }
      }
    }, {
      window: { location: { search: '' } },
      document: createDocumentMock({
        'all-kamehouse-shell-table-body': {}
      })
    });
  }

  async function importLoader() {
    const mock = install();
    await import('@/js/groot/admin/kamehouse-shell/kamehouse-shell-index');
    return { mock, loader: mock.extension.kameHouseShellLoader };
  }

  it('loads the shell loader and populates the table', async () => {
    const { mock, loader } = await importLoader();

    expect(loader).toBeDefined();
    expect(mock.util.banner.setRandomAllBanner).toHaveBeenCalledWith(null);
    expect(get).toHaveBeenCalledTimes(1);
    expect(get.mock.calls[0][1]).toBe('/kame-house-groot/api/v1/admin/kamehouse-shell/scripts.php');
    expect(get.mock.calls[0][0].timeout).toBe(15);

    get.mock.calls[0][4](['abc.sh', 'Started executing x', 'Finished executing y'], 200, '', {});
    expect(mock.util.dom.append).toHaveBeenCalledTimes(1);
    expect(mock.util.dom.replaceWith).toHaveBeenCalled();
    expect(buttonOptions.html).toBe('abc.sh');
  });

  it('logs an error when getting the scripts fails', async () => {
    const { mock } = await importLoader();

    get.mock.calls[0][5]('error', 500, 'Server Error', {});
    expect(mock.logger.error).toHaveBeenCalledWith('Error getting scripts csv', 'Error getting scripts csv');
  });

  it('filters the shell table rows', async () => {
    const { mock, loader } = await importLoader();

    loader.filterKameHouseShellRows('abc');
    expect(mock.util.table.filterTableRows).toHaveBeenCalledWith('abc', 'all-kamehouse-shell-table-body', null, null);
  });

  it('executes a script when its row button is clicked', async () => {
    const { mock } = await importLoader();
    get.mock.calls[0][4](['abc.sh'], 200, '', {});

    buttonOptions.click({}, { scriptName: 'abc.sh' });

    expect(mock.logger.info).toHaveBeenCalledWith('Executing script : abc.sh with args: null', null);
    expect(mock.core.windowLocation).toHaveBeenCalledWith('/kame-house/groot/admin/kamehouse-shell/exec-script.html?script=abc.sh');
  });

  it('uses custom urls when configured', async () => {
    const { mock } = await importLoader();
    const loader = mock.extension.kameHouseShellLoader;

    loader.setExecScriptPageUrl('/custom/exec.html');
    loader.setGetScriptsApiUrl('/custom/scripts.php');
    get.mockClear();
    loader.load();

    expect(get.mock.calls[0][1]).toBe('/custom/scripts.php');

    get.mock.calls[0][4](['abc.sh'], 200, '', {});
    buttonOptions.click({}, { scriptName: 'abc.sh' });
    expect(mock.core.windowLocation).toHaveBeenCalledWith('/custom/exec.html?script=abc.sh');
  });
});
