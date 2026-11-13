/// <reference types="vitest/globals" />

import { describe, it, expect, vi, beforeEach } from 'vitest';
import { installKameHouseMock, createDocumentMock } from '../../../../helpers/kamehouse-mock';

function flush() {
  return new Promise((resolve) => setTimeout(resolve, 0));
}

describe('groot server-manager', () => {
  let execute: any;
  let shellCalls: any;
  let buttons: any[];
  let imgs: any[];
  let mobileListeners: any;

  beforeEach(() => {
    vi.resetModules();
    (global as any).location = { port: '' };
    shellCalls = {};
    buttons = [];
    imgs = [];
    mobileListeners = {};
    execute = vi.fn((script: any, args: any, onDocker: any, isDaemon: any, timeout: any, success: any, error: any) => {
      shellCalls[script] = { args, onDocker, isDaemon, timeout, success, error };
    });
  });

  function install(overrides: any = {}) {
    return installKameHouseMock({
      extension: {
        kameHouseShell: { execute },
        groot: { session: { isDockerContainer: false } },
        tomcatModuleStatusManager: { getAllModulesStatus: vi.fn() },
        mobile: { core: { getSelectedBackendServer: vi.fn(() => ({ name: 'kamehouse-cc' })) } }
      },
      plugin: {
        debugger: { http: { get: vi.fn() } },
        modal: {
          basicModal: {
            openAutoCloseable: vi.fn(),
            setHtml: vi.fn(),
            appendHtml: vi.fn(),
            open: vi.fn(),
            close: vi.fn()
          },
          loadingWheelModal: { open: vi.fn(), openAutoCloseable: vi.fn(), close: vi.fn() }
        }
      },
      core: {
        isValidKameHouseConfig: vi.fn((line: any) => line.includes('UNDEPLOYED_MODULES')),
        getKameHouseConfigValue: vi.fn(() => 'media, ,desktop')
      },
      util: {
        tab: { openTabFromCookies: vi.fn() },
        dom: {
          getButton: vi.fn((options: any) => { buttons.push(options); return options; }),
          getImg: vi.fn((attrs: any) => { const img = { attrs }; imgs.push(img); return img; }),
          remove: vi.fn(),
          replaceWithById: vi.fn()
        },
        mobile: {
          exec: vi.fn((desktop: any) => desktop()),
          setMobileEventListeners: vi.fn((pause: any, resume: any) => { mobileListeners.pause = pause; mobileListeners.resume = resume; })
        }
      },
      ...overrides
    }, {
      window: { location: { search: '' } },
      document: createDocumentMock({
        'tail-log-dropdown': { value: 'catalina.out' },
        'tail-log-num-lines-dropdown': { value: '100' },
        'tail-log-level-dropdown': { value: 'INFO' },
        'tomcat-debug-mode': { checked: false },
        'tail-log-output-table-body': {}
      })
    });
  }

  async function importManagers(overrides: any = {}) {
    const mock = install(overrides);
    await import('@/js/groot/admin/server-manager/groot-server-manager');
    return {
      mock,
      serverManager: mock.extension.serverManager,
      keepAliveManager: mock.extension.keepAliveManager,
      deploymentManager: mock.extension.deploymentManager,
      tailLogManagerWrapper: mock.extension.tailLogManagerWrapper
    };
  }

  it('loads the server manager', async () => {
    const { mock, serverManager } = await importManagers();
    expect(serverManager).toBeDefined();
    expect(mock.logger.info).toHaveBeenCalledWith('Loading ServerManager', null);
    expect(mock.util.banner.setRandomAllBanner).toHaveBeenCalledWith(null);
    expect(mock.util.tab.openTabFromCookies).toHaveBeenCalledWith('kh-groot-server-manager', 'tab-deployment');
    expect(mock.util.collapsibleDiv.resize).toHaveBeenCalledWith('command-output-wrapper');
    expect(serverManager.isRunningInDockerContainer()).toBe(false);
  });

  it('detects docker container sessions', async () => {
    const { serverManager } = await importManagers({
      extension: { groot: { session: { isDockerContainer: true } }, kameHouseShell: { execute } }
    });
    expect(serverManager.isRunningInDockerContainer()).toBe(true);
  });

  it('manages the command running flag', async () => {
    const { mock, serverManager } = await importManagers();
    serverManager.setCommandRunning();
    expect(serverManager.isCommandRunning()).toBe(true);
    expect(mock.plugin.modal.basicModal.openAutoCloseable).toHaveBeenCalledWith("There's a command already executing. Please wait and retry", 3000);

    serverManager.setCommandNotRunning();
    expect(serverManager.isCommandRunning()).toBe(false);
    serverManager.completeCommandCallback();
    expect(mock.util.collapsibleDiv.resize).toHaveBeenCalledWith('command-output-wrapper');
  });

  it('opens the executing command modal', async () => {
    const { mock, serverManager } = await importManagers();
    serverManager.openExecutingCommandModal();
    expect(mock.plugin.modal.loadingWheelModal.openAutoCloseable).toHaveBeenCalledWith('Executing command. Check command output', 2000);
  });

  it('executes shell scripts and completes the command', async () => {
    const { mock, serverManager } = await importManagers();
    serverManager.executeShellScript('test.sh', '-a');

    expect(shellCalls['test.sh'].args).toBe('-a');
    expect(shellCalls['test.sh'].onDocker).toBe(true);
    expect(shellCalls['test.sh'].timeout).toBe(600);
    expect(serverManager.isCommandRunning()).toBe(true);
    shellCalls['test.sh'].success();
    expect(serverManager.isCommandRunning()).toBe(false);
    expect(mock.util.collapsibleDiv.resize).toHaveBeenCalledWith('command-output-wrapper');
  });

  it('does not execute a shell script when a command is running', async () => {
    const { serverManager } = await importManagers();
    serverManager.setCommandRunning();
    execute.mockClear();
    serverManager.executeShellScript('test.sh', '');
    expect(execute).not.toHaveBeenCalled();
  });

  it('executes the docker status and desktop backgrounds scripts', async () => {
    const { serverManager } = await importManagers();
    serverManager.dockerStatus();
    expect(shellCalls['docker/docker-status-kamehouse.sh']).toBeDefined();
    serverManager.setCommandNotRunning();
    serverManager.listKameHouseDesktopProcessedBackrounds();
    expect(shellCalls['desktop/kamehouse-desktop-list-processed-backgrounds.sh']).toBeDefined();
  });

  it('confirms and reboots the server', async () => {
    const { mock, serverManager } = await importManagers();
    serverManager.confirmRebootServer();
    expect(mock.plugin.modal.basicModal.setHtml).toHaveBeenCalled();
    expect(mock.plugin.modal.basicModal.appendHtml).toHaveBeenCalled();
    expect(mock.plugin.modal.basicModal.open).toHaveBeenCalled();

    const rebootButton = buttons[buttons.length - 1];
    rebootButton.click({}, null);
    expect(mock.plugin.modal.basicModal.close).toHaveBeenCalled();
    expect(shellCalls['/shutdown/reboot.sh']).toBeDefined();
  });

  it('loads keep alive manager and triggers its commands', async () => {
    const { mock, keepAliveManager } = await importManagers();
    expect(mock.logger.info).toHaveBeenCalledWith('Loading KeepAliveManager', null);
    expect(mock.util.module.setModuleLoaded).toHaveBeenCalledWith('keepAliveManager');

    keepAliveManager.keepAliveStatus();
    expect(shellCalls['keep-alive/toggle/keep-alive-status.sh']).toBeDefined();
    mock.extension.serverManager.setCommandNotRunning();
    keepAliveManager.toggleKeepAlive();
    expect(shellCalls['keep-alive/toggle/keep-alive-toggle.sh']).toBeDefined();
  });

  it('hides undeployed modules', async () => {
    const { mock, deploymentManager } = await importManagers();
    const call = shellCalls['deploy/get-undeployed-kamehouse-modules.sh'];
    expect(call).toBeDefined();
    call.success({ standardOutputHtml: ['UNDEPLOYED_MODULES=media, ,desktop'] });

    expect(mock.logger.info).toHaveBeenCalledWith('Hiding undeployed module media', null);
    expect(mock.util.dom.classListAdd).toHaveBeenCalledWith(expect.anything(), 'hidden-kh');
  });

  it('ignores empty undeployed modules output', async () => {
    const { mock } = await importManagers();
    shellCalls['deploy/get-undeployed-kamehouse-modules.sh'].success({ standardOutputHtml: null });
    expect(mock.util.dom.classListAdd).not.toHaveBeenCalledWith(expect.anything(), 'hidden-kh');
  });

  it('displays tomcat module running status', async () => {
    const { mock } = await importManagers();
    const call = shellCalls['tomcat/kamehouse-tomcat-modules-status.sh'];
    call.success({ standardOutputHtml: ['/kame-house-admin:running', '/kame-house-media:stopped', '/kame-house-foo:unknown', 'noise'] });

    expect(mock.util.dom.setHtmlById).toHaveBeenCalledWith('mst-admin-status-val', imgs[2]);
    expect(mock.util.dom.setHtmlById).toHaveBeenCalledWith('mst-media-status-val', imgs[1]);
    expect(mock.util.dom.setHtmlById).toHaveBeenCalledWith('mst-foo-status-val', imgs[0]);
  });

  it('handles empty tomcat module status output', async () => {
    const { mock } = await importManagers();
    shellCalls['tomcat/kamehouse-tomcat-modules-status.sh'].success({ standardOutputHtml: [] });
    expect(mock.logger.info).toHaveBeenCalledWith('Loading DeploymentManager', null);
  });

  it('displays groot running status', async () => {
    const { mock } = await importManagers();
    const call = shellCalls['groot/kamehouse-groot-status.sh'];
    expect(call).toBeDefined();
    expect(call.args).toBe('');
    expect(call.onDocker).toBe(false);
    expect(call.isDaemon).toBe(false);
    expect(call.timeout).toBe(60);

    call.success({ standardOutputHtml: ['', 'noise', 'kamehouse-groot is running with pid 123'] });
    expect(mock.util.dom.setHtmlById).toHaveBeenCalledWith('mst-groot-status-val', imgs[2]);

    call.success({ standardOutputHtml: ['kamehouse-groot is not running'] });
    expect(mock.util.dom.setHtmlById).toHaveBeenCalledWith('mst-groot-status-val', imgs[1]);

    call.success({ standardOutputHtml: null });
    call.success({ standardOutputHtml: [] });

    call.error();
    expect(mock.util.dom.setHtmlById).toHaveBeenCalledWith('mst-groot-status-val', imgs[1]);
  });

  it('displays non tomcat module status', async () => {
    const { mock } = await importManagers();
    const call = shellCalls['cmd/kamehouse-cmd-version.sh'];
    call.success({ standardOutputHtml: ['', 'noise', '{"buildVersion":"1.0","buildDate":"today"}'] });

    expect(mock.logger.info).toHaveBeenCalledWith('Loaded cmd buildInfo: {"buildVersion":"1.0","buildDate":"today"}', null);
    expect(mock.util.dom.setHtmlById).toHaveBeenCalledWith('mst-cmd-build-version-val', '1.0');
    expect(mock.util.dom.setHtmlById).toHaveBeenCalledWith('mst-cmd-build-date-val', 'today');
  });

  it('handles an error getting a non tomcat module status', async () => {
    const { mock } = await importManagers();
    shellCalls['desktop/kamehouse-desktop-version.sh'].error();
    expect(mock.util.dom.setHtmlById).toHaveBeenCalledWith('mst-desktop-build-version-val', 'Error getting data');
    expect(mock.util.dom.setHtmlById).toHaveBeenCalledWith('mst-desktop-build-date-val', 'Error getting data');
  });

  it('displays the tomcat process status', async () => {
    const { mock } = await importManagers();
    const call = shellCalls['tomcat/tomcat-status.sh'];
    call.success({ standardOutputHtml: ['Started executing', '', 'tomcat running', 'Executing script'] });

    expect(mock.util.dom.append).toHaveBeenCalledWith(expect.anything(), 'tomcat running');
    expect(mock.util.dom.remove).toHaveBeenCalled();
  });

  it('refreshes the server view', async () => {
    const { mock, deploymentManager } = await importManagers();
    deploymentManager.refreshServerView();
    expect(mock.logger.info).toHaveBeenCalledWith('Refreshing server view', null);
    expect(mock.extension.tomcatModuleStatusManager.getAllModulesStatus).toHaveBeenCalled();
    expect(mock.util.dom.setHtmlById).toHaveBeenCalledWith('mst-ui-status-val', imgs[0]);
    expect(mock.util.dom.setHtmlById).toHaveBeenCalledWith('mst-groot-status-val', imgs[0]);
    expect(shellCalls['groot/kamehouse-groot-status.sh']).toBeDefined();
  });

  it('starts and stops modules', async () => {
    const { deploymentManager } = await importManagers();
    deploymentManager.startModule('admin');
    expect(shellCalls['tomcat/start-kamehouse.sh'].args).toBe('-m admin');
    shellCalls['tomcat/start-kamehouse.sh'].success();

    deploymentManager.stopModule('media');
    expect(shellCalls['tomcat/stop-kamehouse.sh'].args).toBe('-m media');
  });

  it('does not start or stop modules when a command is running', async () => {
    const { mock, deploymentManager } = await importManagers();
    mock.extension.serverManager.isCommandRunning = vi.fn(() => true);
    execute.mockClear();
    deploymentManager.startModule('admin');
    deploymentManager.stopModule('media');
    expect(execute).not.toHaveBeenCalled();
  });

  it('restarts and stops the desktop app', async () => {
    const { deploymentManager } = await importManagers();
    deploymentManager.restartDesktop();
    shellCalls['desktop/kamehouse-desktop-restart.sh'].success();
    deploymentManager.stopDesktop();
    shellCalls['desktop/kamehouse-desktop-stop.sh'].error();
    expect(shellCalls['desktop/kamehouse-desktop-stop.sh'].onDocker).toBe(true);
  });

  function serverManagerFrom(mock: any) {
    return mock.extension.serverManager;
  }

  it('deploys and undeploys modules', async () => {
    const { mock, deploymentManager } = await importManagers();
    deploymentManager.deployModule('admin');
    expect(shellCalls['deploy/deploy-kamehouse.sh'].args).toBe('-m admin');
    serverManagerFrom(mock).setCommandNotRunning();
    deploymentManager.undeployModule('admin');
    expect(shellCalls['deploy/undeploy-kamehouse.sh'].args).toBe('-m admin');
    serverManagerFrom(mock).setCommandNotRunning();
    deploymentManager.deployModuleAllServers('admin');
    expect(shellCalls['deploy/exec-kamehouse-all-servers.sh'].args).toBe('-s deploy/deploy-kamehouse.sh -a -m admin');
    serverManagerFrom(mock).setCommandNotRunning();
    deploymentManager.deployAllModules();
    expect(shellCalls['deploy/deploy-kamehouse.sh']).toBeDefined();
    serverManagerFrom(mock).setCommandNotRunning();
    deploymentManager.deployAllModulesAllServers();
    expect(shellCalls['deploy/exec-kamehouse-all-servers.sh']).toBeDefined();
  });

  it('uses the debug argument when restarting tomcat in debug mode', async () => {
    const { deploymentManager } = await importManagers();
    (global as any).document.getElementById('tomcat-debug-mode').checked = true;
    deploymentManager.restartTomcat();
    expect(shellCalls['tomcat/tomcat-restart.sh'].args).toBe('-d');
  });

  it('gets the non tomcat module status without dev arguments', async () => {
    const { deploymentManager } = await importManagers();
    deploymentManager.getNonTomcatModulesStatus('cmd');
    expect(shellCalls['cmd/kamehouse-cmd-version.sh'].args).toBe('');
  });

  it('loads the tail log manager wrapper and starts tailing', async () => {
    let resolveSleep: any;
    const sleep = vi.fn(() => new Promise((resolve) => { resolveSleep = resolve; }));
    const { mock, tailLogManagerWrapper } = await importManagers();
    mock.core.sleep = sleep;
    mock.extension.tailLogManager = { tailLog: vi.fn() };
    expect(mock.logger.info).toHaveBeenCalledWith('Loading TailLogManagerWrapper', null);
    expect(tailLogManagerWrapper.getTailLogManager()).toBe(mock.extension.tailLogManager);

    tailLogManagerWrapper.toggleTailLog();
    await flush();
    expect(mock.extension.tailLogManager.tailLog).toHaveBeenCalledWith('catalina.out', '100', 'INFO', false, false, expect.any(Function));
    expect(mock.util.dom.replaceWithById).toHaveBeenCalledWith('toggle-tail-log-btn', buttons[0]);

    tailLogManagerWrapper.toggleTailLog();
    expect(mock.plugin.modal.loadingWheelModal.open).toHaveBeenCalledWith('Stopping tail log...');
    resolveSleep();
    await flush();
    expect(mock.logger.info).toHaveBeenCalledWith('Finished tailLog loop', null);
    expect(mock.plugin.modal.loadingWheelModal.close).toHaveBeenCalled();
  });

  it('pauses and resumes tail logs', async () => {
    const { mock } = await importManagers();
    mobileListeners.pause();
    expect(mock.logger.info).toHaveBeenCalledWith('KameHouse sent to background. Tail log not running', null);
    mobileListeners.resume();
    expect(mock.logger.info).toHaveBeenCalledWith('KameHouse sent to foreground. Tail log not running', null);
  });
});
