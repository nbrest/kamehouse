/// <reference types="vitest/globals" />

import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { installKameHouseMock, createDocumentMock } from '../../../helpers/kamehouse-mock';

function createModal() {
  return {
    basicModal: {
      open: vi.fn(),
      close: vi.fn(),
      reset: vi.fn(),
      openAutoCloseable: vi.fn(),
      setHtml: vi.fn(),
      setIsErrorMessage: vi.fn(),
      isErrorMessage: vi.fn(() => false)
    },
    loadingWheelModal: { open: vi.fn(), close: vi.fn() }
  };
}

function createDebugger() {
  return { http: { get: vi.fn(), post: vi.fn() }, renderCustomDebugger: vi.fn() };
}

function serverConfig(overrides: any = {}) {
  return {
    backend: {
      selected: 'ServerA',
      servers: [
        {
          name: 'ServerA',
          url: 'http://server-a',
          username: 'goku',
          password: 'kamehameha',
          isLoggedIn: false,
          skipSslCheck: false,
          isUrlEditable: true,
          ...overrides
        }
      ]
    }
  };
}

describe('kamehouse-mobile', () => {
  beforeEach(() => {
    vi.resetModules();
    window.history.pushState({}, '', '/');
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  async function loadModule(options: any = {}) {
    const elements = options.elements || {};
    const documentMock = options.document || createDocumentMock(elements);
    const modal = createModal();
    const debuggerPlugin = createDebugger();
    const mock = installKameHouseMock({
      ...(options.overrides || {}),
      util: {
        ...((options.overrides || {}).util || {}),
        module: {
          waitForModules: options.waitForModules || vi.fn(),
          isModuleLoaded: () => false,
          setModuleLoaded: vi.fn(),
          ...(((options.overrides || {}).util || {}).module || {})
        }
      },
      plugin: {
        modal,
        debugger: debuggerPlugin,
        ...((options.overrides || {}).plugin || {})
      }
    }, { document: documentMock });

    if (options.search) {
      window.history.pushState({}, '', '/' + options.search);
    }
    await import('@/kame-house-mobile/kamehouse-mobile/js/kamehouse-mobile');
    return { mock, ext: mock.extension.mobile, core: mock.extension.mobile.core, cfg: mock.extension.mobile.configManager };
  }

  describe('KameHouseMobile', () => {
    it('loads the extension and initializes core and config manager', async () => {
      const { mock, ext } = await loadModule();

      expect(mock.logger.info).toHaveBeenCalledWith('Started initializing kamehouse-mobile.js', null);
      expect(ext).toBeDefined();
      expect(ext.core).toBeDefined();
      expect(ext.configManager).toBeDefined();
      expect(mock.util.mobile.configureApp).toHaveBeenCalled();
    });
  });

  describe('backend server config', () => {
    it('returns backend servers, selected server, url and login status', async () => {
      const { mock, ext, core } = await loadModule();
      ext.config = serverConfig({ isLoggedIn: true, skipSslCheck: true });

      expect(core.getBackendServers()).toHaveLength(1);
      expect(core.getSelectedBackendServer().name).toBe('ServerA');
      expect(core.getSelectedBackendServerUrl()).toBe('http://server-a');
      expect(core.isLoggedIn()).toBe(true);
      expect(mock.logger.trace).toHaveBeenCalled();
    });

    it('returns null when the config is not initialized', async () => {
      const { mock, ext, core } = await loadModule();
      ext.config = {};

      expect(core.getBackendServers()).toBeNull();
      expect(core.getSelectedBackendServer()).toBeNull();
      expect(core.getSelectedBackendServerUrl()).toBeNull();
      expect(core.isLoggedIn()).toBe(false);
      expect(mock.logger.error).toHaveBeenCalled();
    });

    it('returns null when the selected server or url is missing', async () => {
      const { mock, ext, core } = await loadModule();
      ext.config = serverConfig();
      ext.config.backend.selected = 'Unknown';

      expect(core.getSelectedBackendServer()).toBeNull();
      expect(core.getSelectedBackendServerUrl()).toBeNull();

      ext.config.backend.selected = 'ServerA';
      ext.config.backend.servers[0].url = '';
      expect(core.getSelectedBackendServerUrl()).toBeNull();

      delete ext.config.backend.servers[0].isLoggedIn;
      expect(core.isLoggedIn()).toBe(false);
      expect(mock.logger.error).toHaveBeenCalled();
    });
  });

  describe('login', () => {
    it('logs in successfully', async () => {
      const { mock, ext, core, cfg } = await loadModule();
      ext.config = serverConfig();
      cfg.reGenerateMobileConfigFile = vi.fn();
      mock.plugin.debugger.http.post.mockImplementation((_config, _url, _headers, _data, success) => {
        success('{"status":"ok"}', 200, null, null);
      });

      core.login();

      expect(mock.plugin.modal.loadingWheelModal.open).toHaveBeenCalled();
      expect(mock.plugin.modal.loadingWheelModal.close).toHaveBeenCalled();
      expect(mock.plugin.modal.basicModal.openAutoCloseable).toHaveBeenCalledWith(expect.anything(), 1000);
      expect(ext.config.backend.servers[0].isLoggedIn).toBe(true);
      expect(cfg.reGenerateMobileConfigFile).toHaveBeenCalledWith(false);
      expect(mock.util.dom.classListAddById).toHaveBeenCalledWith('backend-username-password', 'hidden-kh');
      expect(mock.plugin.debugger.http.post.mock.calls[0][0].timeout).toBe(15);
    });

    it('shows an error when the login response redirects to the login page', async () => {
      const { mock, ext, core, cfg } = await loadModule();
      ext.config = serverConfig();
      cfg.reGenerateMobileConfigFile = vi.fn();
      mock.plugin.debugger.http.post.mockImplementation((_config, _url, _headers, _data, success) => {
        success('<html>GRoot - Login</html>', 200, null, null);
      });

      core.login();

      expect(mock.logger.error).toHaveBeenCalledWith(
        'Login error - invalid credentials. Redirected back to login',
        'Login error - invalid credentials. Redirected back to login'
      );
      expect(cfg.reGenerateMobileConfigFile).not.toHaveBeenCalled();
    });

    it('shows an error for unauthorized login response codes', async () => {
      const { mock, ext, core } = await loadModule();
      ext.config = serverConfig();
      mock.plugin.debugger.http.post.mockImplementation((_config, _url, _headers, _data, _success, error) => {
        error('Unauthorized', 401, null, null);
      });

      core.login();

      expect(mock.logger.error).toHaveBeenCalledWith(
        'Login error - invalid credentials',
        'Login error - invalid credentials'
      );
    });

    it('shows an error when connecting to the backend fails', async () => {
      const { mock, ext, core } = await loadModule();
      ext.config = serverConfig();
      mock.plugin.debugger.http.post.mockImplementation((_config, _url, _headers, _data, _success, error) => {
        error('Server error', 500, null, null);
      });

      core.login();

      expect(mock.logger.error).toHaveBeenCalledWith(
        'Error connecting to the backend to login. Response code: 500',
        'Error connecting to the backend to login. Response code: 500'
      );
      expect(mock.plugin.modal.basicModal.openAutoCloseable).toHaveBeenCalledWith(expect.anything(), 2000);
    });

    it('handles missing credentials when the login is redirected', async () => {
      const { mock, ext, core } = await loadModule();
      ext.config = {};
      mock.plugin.debugger.http.post.mockImplementation((_config, _url, _headers, _data, success) => {
        success('GRoot - Login', 200, null, null);
      });

      core.login();

      expect(mock.logger.error).toHaveBeenCalledWith(
        'Unable to get backend credentials. Mobile config manager may not be initialized yet',
        'Unable to get backend credentials. Mobile config manager may not be initialized yet'
      );
    });
  });

  describe('logout', () => {
    it('logs out successfully', async () => {
      const { mock, ext, core, cfg } = await loadModule({
        elements: {
          'backend-username-input': { value: 'goku' },
          'backend-password-input': { value: 'kamehameha' }
        }
      });
      ext.config = serverConfig({ isLoggedIn: true });
      cfg.reGenerateMobileConfigFile = vi.fn();
      mock.cordova = { plugin: { http: { clearCookies: vi.fn() } } };
      mock.plugin.debugger.http.get.mockImplementation((_config, _url, _headers, _data, success) => {
        success('KameHouse - Login', 200, null, null);
      });

      core.logout();

      expect(mock.plugin.modal.loadingWheelModal.close).toHaveBeenCalled();
      expect(mock.plugin.modal.basicModal.openAutoCloseable).toHaveBeenCalledWith(expect.anything(), 1000);
      expect(ext.config.backend.servers[0].isLoggedIn).toBe(false);
      expect(ext.config.backend.servers[0].username).toBe('');
      expect(ext.config.backend.servers[0].password).toBe('');
      expect(cfg.reGenerateMobileConfigFile).toHaveBeenCalledWith(false);
      expect(mock.cordova.plugin.http.clearCookies).toHaveBeenCalled();
    });

    it('shows an error when logout does not return the login page', async () => {
      const { mock, ext, core, cfg } = await loadModule();
      ext.config = serverConfig({ isLoggedIn: true });
      cfg.reGenerateMobileConfigFile = vi.fn();
      mock.cordova = { plugin: { http: { clearCookies: vi.fn() } } };
      mock.plugin.debugger.http.get.mockImplementation((_config, _url, _headers, _data, success) => {
        success('{"message":"error"}', 200, null, null);
      });

      core.logout();

      expect(mock.logger.error).toHaveBeenCalledWith(
        'Logout error: ' + JSON.stringify('{"message":"error"}', null, null),
        null
      );
      expect(cfg.reGenerateMobileConfigFile).not.toHaveBeenCalled();
    });

    it('clears local credentials when the logout request fails', async () => {
      const { mock, ext, core, cfg } = await loadModule();
      ext.config = serverConfig({ isLoggedIn: true });
      cfg.reGenerateMobileConfigFile = vi.fn();
      mock.cordova = { plugin: { http: { clearCookies: vi.fn() } } };
      mock.plugin.debugger.http.get.mockImplementation((_config, _url, _headers, _data, _success, error) => {
        error('Network error', 500, null, null);
      });

      core.logout();

      expect(mock.logger.error).toHaveBeenCalledWith(
        'Error connecting to the backend to logout. Clearing locally stored credentials. Response code: 500',
        'Error connecting to the backend to logout. Clearing locally stored credentials. Response code: 500'
      );
      expect(ext.config.backend.servers[0].isLoggedIn).toBe(false);
      expect(mock.cordova.plugin.http.clearCookies).toHaveBeenCalled();
    });
  });

  describe('mobileHttpRequst', () => {
    function createHttpPlugin(trustErrorMessage: string | null = null, responseMode = 'success') {
      return {
        setServerTrustMode: vi.fn((_trust: string, success: any, error: any) => {
          if (trustErrorMessage) {
            error(trustErrorMessage);
          } else {
            success();
          }
        }),
        sendRequest: vi.fn((_url: string, _options: any, success: any, error: any) => {
          if (responseMode === 'success') {
            success({
              status: 200,
              headers: { 'Content-Type': 'application/json' },
              data: '{"value":42}'
            });
          } else {
            error({ status: 500, error: 'failure', headers: {} });
          }
        }),
        useBasicAuth: vi.fn(),
        setHeader: vi.fn(),
        setRequestTimeout: vi.fn(),
        setReadTimeout: vi.fn()
      };
    }

    it('sends a json GET request and processes a successful response', async () => {
      const { mock, ext, core } = await loadModule();
      ext.config = serverConfig();
      const http = createHttpPlugin();
      mock.cordova = { plugin: { http } };
      const successCallback = vi.fn();
      const errorCallback = vi.fn();

      core.mobileHttpRequst('GET', {}, '/api/status', { 'Content-Type': 'application/json' }, { id: 1 }, successCallback, errorCallback);

      expect(http.setRequestTimeout).toHaveBeenCalledWith(60);
      expect(http.setReadTimeout).toHaveBeenCalledWith(60);
      expect(http.setHeader).toHaveBeenCalledWith('Authorization', null);
      expect(http.setServerTrustMode).toHaveBeenCalledWith('default', expect.any(Function), expect.any(Function));
      expect(http.sendRequest).toHaveBeenCalledWith(
        'http://server-a/api/status',
        expect.objectContaining({ method: 'GET', serializer: 'json', data: { id: 1 } }),
        expect.any(Function),
        expect.any(Function)
      );
      expect(successCallback).toHaveBeenCalledWith({ value: 42 }, 200, null, { 'Content-Type': 'application/json' });
      expect(errorCallback).not.toHaveBeenCalled();
    });

    it('sends an authenticated url encoded GET request and skips ssl checks', async () => {
      const { mock, ext, core } = await loadModule();
      ext.config = serverConfig({ isLoggedIn: true, skipSslCheck: true });
      const http = createHttpPlugin();
      mock.cordova = { plugin: { http } };
      mock.http.isUrlEncodedRequest.mockReturnValue(true);
      mock.http.urlEncodeParams.mockReturnValue('id=1');
      const successCallback = vi.fn();

      core.mobileHttpRequst('GET', { timeout: 30 }, '/api/status', { 'Content-Type': 'application/x-www-form-urlencoded' }, { id: 1 }, successCallback, vi.fn());

      expect(http.setRequestTimeout).toHaveBeenCalledWith(30);
      expect(http.useBasicAuth).toHaveBeenCalledWith('goku', 'kamehameha');
      expect(http.setServerTrustMode).toHaveBeenCalledWith('nocheck', expect.any(Function), expect.any(Function));
      expect(mock.http.urlEncodeParams).toHaveBeenCalledWith({ id: 1 });
      expect(http.sendRequest.mock.calls[0][0]).toBe('http://server-a/api/status?id=1');
      expect(http.sendRequest.mock.calls[0][1].serializer).toBe('utf8');
      expect(successCallback).toHaveBeenCalledWith({ value: 42 }, 200, null, { 'Content-Type': 'application/json' });
    });

    it('sends a url encoded POST request with empty credentials and ssl checks enabled', async () => {
      const { mock, ext, core } = await loadModule();
      ext.config = serverConfig({ isLoggedIn: true, username: '', password: '' });
      const http = createHttpPlugin();
      mock.cordova = { plugin: { http } };
      mock.http.isUrlEncodedRequest.mockReturnValue(true);
      const successCallback = vi.fn();

      core.mobileHttpRequst('POST', {}, '/api/status', { 'Content-Type': 'application/x-www-form-urlencoded' }, { id: 1 }, successCallback, vi.fn());

      expect(http.useBasicAuth).not.toHaveBeenCalled();
      expect(http.setServerTrustMode).toHaveBeenCalledWith('default', expect.any(Function), expect.any(Function));
      expect(http.sendRequest.mock.calls[0][1].serializer).toBe('urlencoded');
      expect(http.sendRequest.mock.calls[0][1].data).toEqual({ id: 1 });
      expect(successCallback).toHaveBeenCalled();
    });

    it('does not send the request when enabling the ssl check fails', async () => {
      const { mock, ext, core } = await loadModule();
      ext.config = serverConfig();
      const http = createHttpPlugin('ssl error');
      mock.cordova = { plugin: { http } };

      core.mobileHttpRequst('GET', {}, '/api/status', null, null, vi.fn(), vi.fn());

      expect(mock.logger.error).toHaveBeenCalledWith(
        "Error setting cordova ssl trustmode to default. Can't proceed with mobile http request to http://server-a/api/status",
        "Error setting cordova ssl trustmode to default. Can't proceed with mobile http request to http://server-a/api/status"
      );
      expect(http.sendRequest).not.toHaveBeenCalled();
    });

    it('sends the request even when disabling the ssl check fails', async () => {
      const { mock, ext, core } = await loadModule();
      ext.config = serverConfig({ skipSslCheck: true });
      const http = createHttpPlugin('ssl error');
      mock.cordova = { plugin: { http } };

      core.mobileHttpRequst('GET', {}, '/api/status', null, null, vi.fn(), vi.fn());

      expect(mock.logger.error).toHaveBeenCalledWith(
        'Error setting cordova ssl trustmode to nocheck. Trying mobile http request anyway',
        'Error setting cordova ssl trustmode to nocheck. Trying mobile http request anyway'
      );
      expect(http.sendRequest).toHaveBeenCalled();
    });

    it('processes an error response', async () => {
      const { mock, ext, core } = await loadModule();
      ext.config = serverConfig();
      const http = createHttpPlugin(null, 'error');
      mock.cordova = { plugin: { http } };
      const errorCallback = vi.fn();

      core.mobileHttpRequst('GET', {}, '/api/status', null, null, vi.fn(), errorCallback);

      expect(mock.logger.logApiError).toHaveBeenCalledWith({}, 'http://server-a/api/status', 'failure', 500, null, {});
      expect(errorCallback).toHaveBeenCalledWith('failure', 500, null, {});
    });

    it('defaults to false when skipSslCheck is missing', async () => {
      const { mock, ext, core } = await loadModule();
      ext.config = serverConfig();
      delete ext.config.backend.servers[0].skipSslCheck;
      const http = createHttpPlugin();
      mock.cordova = { plugin: { http } };

      core.mobileHttpRequst('GET', {}, '/api/status', null, null, vi.fn(), vi.fn());

      expect(mock.logger.error).toHaveBeenCalledWith(
        "Couldn't find backend server skipSslCheck in the config. Mobile app config manager may not have completed initialization yet. Defaulting to false",
        "Couldn't find backend server skipSslCheck in the config. Mobile app config manager may not have completed initialization yet. Defaulting to false"
      );
      expect(http.setServerTrustMode).toHaveBeenCalledWith('default', expect.any(Function), expect.any(Function));
    });

    it('handles a missing backend server while sending a request', async () => {
      const { mock, ext, core } = await loadModule();
      ext.config = {};
      const http = createHttpPlugin();
      mock.cordova = { plugin: { http } };

      core.mobileHttpRequst('GET', {}, '/api/status', null, null, vi.fn(), vi.fn());

      expect(http.sendRequest.mock.calls[0][0]).toBe('null/api/status');
    });

    it('returns a non parsed response body when the response is not json', async () => {
      const { mock, ext, core } = await loadModule();
      ext.config = serverConfig();
      const http = createHttpPlugin();
      http.sendRequest.mockImplementation((_url: string, _options: any, success: any) => {
        success({ status: 200, headers: null, data: 'plain response' });
      });
      mock.cordova = { plugin: { http } };
      const successCallback = vi.fn();

      core.mobileHttpRequst('GET', {}, '/api/status', null, null, successCallback, vi.fn());

      expect(successCallback).toHaveBeenCalledWith('plain response', 200, null, null);
    });
  });

  describe('mock localhost server', () => {
    function mockBackendConfig() {
      return {
        backend: {
          selected: 'Mock Localhost',
          servers: [{ name: 'Mock Localhost', url: 'http://mock-localhost' }]
        }
      };
    }

    async function loadMockBackend() {
      const result = await loadModule();
      result.ext.config = mockBackendConfig();
      return result;
    }

    it('loads the response body from the mock server for a successful GET request', async () => {
      const { mock, ext, core } = await loadMockBackend();
      mock.util.fetch.loadFileWithTimeout.mockResolvedValue('{"value":42}');
      const successCallback = vi.fn();
      const errorCallback = vi.fn();

      await core.mobileHttpRequst('GET', {}, '/kame-house-media/api/v1/media/players', null, null, successCallback, errorCallback);

      expect(mock.logger.info).toHaveBeenCalledWith(
        'Using mock localhost server for http request to: /kame-house-media/api/v1/media/players',
        null
      );
      expect(mock.util.fetch.loadFileWithTimeout).toHaveBeenCalledWith('/kame-house-media/api/v1/media/players', 8000);
      expect(successCallback).toHaveBeenCalledWith({ value: 42 }, '200', '', {});
      expect(errorCallback).not.toHaveBeenCalled();
    });

    it('loads the modified response body for a modified url', async () => {
      const { mock, core } = await loadMockBackend();
      mock.util.fetch.loadFileWithTimeout.mockResolvedValue('[{"id":1}]');
      const successCallback = vi.fn();

      await core.mobileHttpRequst('GET', {}, '/kame-house-vlcrc/api/v1/vlc-rc/players', null, null, successCallback, vi.fn());

      expect(mock.util.fetch.loadFileWithTimeout).toHaveBeenCalledWith('/kame-house-vlcrc/api/v1/vlc-rc/players-modified', 8000);
      expect(successCallback).toHaveBeenCalledWith([{ id: 1 }], '200', '', {});
    });

    it('returns the selected entity for a CRUD GET request', async () => {
      const { mock, core } = await loadMockBackend();
      mock.util.fetch.loadFileWithTimeout.mockResolvedValue('[{"id":1,"name":"goku"},{"id":2,"name":"vegeta"}]');
      const successCallback = vi.fn();

      await core.mobileHttpRequst('GET', {}, '/kame-house-admin/api/v1/admin/kamehouse-users/2', null, null, successCallback, vi.fn());

      expect(mock.util.fetch.loadFileWithTimeout).toHaveBeenCalledWith('/kame-house-admin/api/v1/admin/kamehouse-users', 8000);
      expect(successCallback).toHaveBeenCalledWith({ id: 2, name: 'vegeta' }, '200', '', {});
    });

    it('returns a server modification error for non allowed non GET requests', async () => {
      const { mock, core } = await loadMockBackend();
      const successCallback = vi.fn();
      const errorCallback = vi.fn();

      await core.mobileHttpRequst('POST', {}, '/kame-house-media/api/v1/media/players', null, { id: 1 }, successCallback, errorCallback);

      expect(mock.util.fetch.loadFileWithTimeout).not.toHaveBeenCalled();
      expect(errorCallback).toHaveBeenCalledWith({
        code: 503,
        message: "Gomen-Chai. Server modifications are not supported in this mock server. 'POST /kame-house-media/api/v1/media/players' unavailable"
      }, '503', '', {});
      expect(successCallback).not.toHaveBeenCalled();
    });

    it('returns the raw response body when it parses to null', async () => {
      const { mock, core } = await loadMockBackend();
      mock.util.fetch.loadFileWithTimeout.mockResolvedValue('null');
      const successCallback = vi.fn();

      await core.mobileHttpRequst('GET', {}, '/kame-house-media/api/v1/media/players', null, null, successCallback, vi.fn());

      expect(successCallback).toHaveBeenCalledWith('null', '200', '', {});
    });

    it('returns a 404 error when the mock response is a fetch error', async () => {
      const { mock, core } = await loadMockBackend();
      mock.util.fetch.loadFileWithTimeout.mockResolvedValue('Error executing fetch to /kame-house/media');
      const errorCallback = vi.fn();

      await core.mobileHttpRequst('GET', {}, '/kame-house-media/api/v1/media/players', null, null, vi.fn(), errorCallback);

      expect(errorCallback).toHaveBeenCalledWith('Error executing fetch to /kame-house/media', '404', '', {});
    });
  });

  describe('ZTV lock screen', () => {
    it('does nothing when the lock screen plugin is not available', async () => {
      const { mock, ext, core } = await loadModule();
      ext.config = serverConfig();
      mock.cordova = null;

      core.startZtvLockscreen();
      core.keepAliveZtvLockscreen();
      core.stopZtvLockscreen();

      expect(mock.logger.debug).toHaveBeenCalledWith('ZTV lock screen cordova plugin not available', null);
    });

    it('starts the lock screen with the selected server config', async () => {
      const { mock, ext, core } = await loadModule();
      ext.config = serverConfig();
      const ztv = {
        start: vi.fn((_config: any, success: any) => success()),
        isRunning: vi.fn((success: any) => success(true)),
        stop: vi.fn((success: any) => success())
      };
      mock.cordova = { plugin: { ztvLockscreen: ztv } };

      core.startZtvLockscreen();
      core.keepAliveZtvLockscreen();
      core.stopZtvLockscreen();

      expect(ztv.start).toHaveBeenCalledWith(expect.objectContaining({
        url: 'http://server-a',
        username: 'goku',
        password: 'kamehameha',
        skipSslCheck: false,
        host: 'localhost'
      }), expect.any(Function), expect.any(Function));
      expect(ztv.isRunning).toHaveBeenCalled();
      expect(ztv.stop).toHaveBeenCalled();
      expect(mock.logger.info).toHaveBeenCalledWith('ZTV lock screen applet started', null);
      expect(mock.logger.info).toHaveBeenCalledWith('ZTV lock screen applet stopped', null);
    });

    it('starts the lock screen when keep alive finds it stopped', async () => {
      const { mock, ext, core } = await loadModule();
      ext.config = serverConfig();
      const ztv = {
        start: vi.fn(),
        isRunning: vi.fn((success: any) => success(0)),
        stop: vi.fn()
      };
      mock.cordova = { plugin: { ztvLockscreen: ztv } };
      core.startZtvLockscreen = vi.fn();

      core.keepAliveZtvLockscreen();

      expect(mock.logger.info).toHaveBeenCalledWith('ZTV lock screen applet is not running. Starting it', null);
      expect(core.startZtvLockscreen).toHaveBeenCalled();
    });

    it('logs errors from the lock screen plugin', async () => {
      const { mock, ext, core } = await loadModule();
      ext.config = serverConfig();
      const ztv = {
        start: vi.fn((_config: any, _success: any, error: any) => error('start failed')),
        isRunning: vi.fn((_success: any, error: any) => error('status failed')),
        stop: vi.fn((_success: any, error: any) => error('stop failed'))
      };
      mock.cordova = { plugin: { ztvLockscreen: ztv } };

      core.startZtvLockscreen();
      core.keepAliveZtvLockscreen();
      core.stopZtvLockscreen();

      expect(mock.logger.error).toHaveBeenCalledWith('Error starting ZTV lock screen applet: start failed', 'Error starting ZTV lock screen applet: start failed');
      expect(mock.logger.error).toHaveBeenCalledWith('Error checking ZTV lock screen applet status: status failed', 'Error checking ZTV lock screen applet status: status failed');
      expect(mock.logger.error).toHaveBeenCalledWith('Error stopping ZTV lock screen applet: stop failed', 'Error stopping ZTV lock screen applet: stop failed');
    });

    it('does not start the lock screen without a selected server url', async () => {
      const { mock, ext, core } = await loadModule();
      ext.config = {};
      const ztv = { start: vi.fn(), isRunning: vi.fn(), stop: vi.fn() };
      mock.cordova = { plugin: { ztvLockscreen: ztv } };

      core.startZtvLockscreen();

      expect(mock.logger.error).toHaveBeenCalledWith(
        "Couldn't find a backend server to start the ZTV lock screen applet. Select a backend server first",
        "Couldn't find a backend server to start the ZTV lock screen applet. Select a backend server first"
      );
      expect(ztv.start).not.toHaveBeenCalled();
    });

    it('handles errors reading the lock screen plugin', async () => {
      const { mock, ext, core } = await loadModule();
      ext.config = serverConfig();
      mock.cordova = {};
      Object.defineProperty(mock.cordova, 'plugin', {
        get() { throw new Error('plugin error'); }
      });

      core.startZtvLockscreen();

      expect(mock.logger.debug).toHaveBeenCalledWith('ZTV lock screen cordova plugin not available', null);
    });
  });

  describe('build info', () => {
    it('loads the mobile build info', async () => {
      const { mock, ext, core } = await loadModule({
        elements: {
          'mobile-build-version-val': {},
          'mobile-build-date-val': {}
        }
      });
      mock.util.fetch.loadFile.mockResolvedValue('{"buildVersion":"1.0.0","buildDate":"2026-01-01"}');

      await core.setMobileBuildInfo();

      expect(mock.util.fetch.loadFile).toHaveBeenCalledWith('/kame-house/build-info.json');
      expect(mock.util.dom.setHtml).toHaveBeenCalledWith({}, '1.0.0');
      expect(mock.util.dom.setHtml).toHaveBeenCalledWith({}, '2026-01-01');
      expect(mock.logger.info).toHaveBeenCalledWith(
        'Loaded mobile buildInfo: {"buildVersion":"1.0.0","buildDate":"2026-01-01"}',
        null
      );
    });

    it('handles an empty mobile build info', async () => {
      const { mock, core } = await loadModule();
      mock.util.fetch.loadFile.mockResolvedValue('');

      await core.setMobileBuildInfo();

      expect(mock.logger.error).toHaveBeenCalledWith('Unable to load build-info.json', null);
    });

    it('handles mobile build info without version and date', async () => {
      const { mock, core } = await loadModule();
      mock.util.fetch.loadFile.mockResolvedValue('{"other":"value"}');

      await core.setMobileBuildInfo();

      expect(mock.util.dom.setHtml).not.toHaveBeenCalled();
    });

    it('loads the batcave build info', async () => {
      const { mock, core } = await loadModule({
        elements: {
          'batcave-build-version-val': {},
          'batcave-build-date-val': {}
        }
      });
      mock.util.fetch.loadFile.mockResolvedValue('{"buildVersion":"2.0.0","buildDate":"2026-02-02"}');

      await core.setBatcaveBuildInfo();

      expect(mock.util.fetch.loadFile).toHaveBeenCalledWith('/kame-house-batcave/build-info.json');
      expect(mock.util.dom.setHtml).toHaveBeenCalledWith({}, '2.0.0');
      expect(mock.util.dom.setHtml).toHaveBeenCalledWith({}, '2026-02-02');
    });

    it('handles an empty batcave build info', async () => {
      const { mock, core } = await loadModule();
      mock.util.fetch.loadFile.mockResolvedValue('');

      await core.setBatcaveBuildInfo();

      expect(mock.logger.error).toHaveBeenCalledWith('Unable to load batcave build-info.json', null);
    });

    it('handles batcave build info without version and date', async () => {
      const { mock, core } = await loadModule();
      mock.util.fetch.loadFile.mockResolvedValue('{"other":"value"}');

      await core.setBatcaveBuildInfo();

      expect(mock.util.dom.setHtml).not.toHaveBeenCalled();
    });

    it('handles errors loading the batcave build info', async () => {
      const { mock, core } = await loadModule();
      mock.util.fetch.loadFile.mockRejectedValue(new Error('network'));

      await core.setBatcaveBuildInfo();

      expect(mock.logger.error).toHaveBeenCalledWith('Error loading batcave build-info.json', null);
    });
  });

  describe('in app browser', () => {
    function createBrowserConfig(target: string) {
      return {
        getBackend: vi.fn(() => ({ servers: [{ name: 'ServerA', url: 'http://server-a' }] })),
        getInAppBrowserConfig: vi.fn(() => ({ target, options: 'opts' }))
      };
    }

    it('opens a browser in system target', async () => {
      const { mock, ext, core, cfg } = await loadModule();
      ext.config = serverConfig();
      cfg.getBackend = createBrowserConfig('_system').getBackend;
      cfg.getInAppBrowserConfig = createBrowserConfig('_system').getInAppBrowserConfig;
      const instance = { addEventListener: vi.fn(), show: vi.fn(), close: vi.fn() };
      mock.cordova = { InAppBrowser: { open: vi.fn(() => instance) } };

      core.openBrowser('ServerA');

      expect(mock.cordova.InAppBrowser.open).toHaveBeenCalledWith('http://server-a', '_system', 'opts');
      expect(mock.plugin.modal.basicModal.openAutoCloseable).toHaveBeenCalledWith(expect.anything(), 2000);
      expect(instance.addEventListener).not.toHaveBeenCalled();
    });

    it('opens an in app browser and registers its event listeners', async () => {
      const { mock, ext, core, cfg } = await loadModule();
      ext.config = serverConfig();
      const browserConfig = createBrowserConfig('_blank');
      cfg.getBackend = browserConfig.getBackend;
      cfg.getInAppBrowserConfig = browserConfig.getInAppBrowserConfig;
      const instance = { addEventListener: vi.fn(), show: vi.fn(), close: vi.fn() };
      mock.cordova = { InAppBrowser: { open: vi.fn(() => instance) } };
      let errorMessage = false;
      mock.plugin.modal.basicModal.isErrorMessage.mockImplementation(() => errorMessage);

      core.openBrowser('ServerA');

      expect(mock.plugin.modal.basicModal.setHtml).toHaveBeenCalledWith(expect.anything());
      expect(mock.plugin.modal.basicModal.setIsErrorMessage).toHaveBeenCalledWith(false);
      expect(mock.plugin.modal.basicModal.open).toHaveBeenCalled();
      expect(instance.addEventListener).toHaveBeenCalledTimes(5);

      const listeners: any = {};
      instance.addEventListener.mock.calls.forEach((call: any[]) => { listeners[call[0]] = call[1]; });

      listeners.loadstop({});
      expect(instance.show).toHaveBeenCalled();
      listeners.loadstart({});
      listeners.message({});
      listeners.loaderror({ error: 'failed' });
      expect(mock.logger.error).toHaveBeenCalled();
      expect(instance.close).toHaveBeenCalled();

      instance.close.mockClear();
      errorMessage = false;
      listeners.exit({});
      expect(instance.close).not.toHaveBeenCalled();
      expect(mock.plugin.modal.basicModal.close).toHaveBeenCalled();
      expect(mock.plugin.modal.basicModal.reset).toHaveBeenCalled();

      errorMessage = true;
      listeners.exit({});
      expect(mock.plugin.modal.basicModal.close).toHaveBeenCalledTimes(1);
    });

    it('overrides window.open with the in app browser', async () => {
      const { mock, core } = await loadModule();
      const open = vi.fn();
      mock.cordova = { InAppBrowser: { open } };
      window.open = vi.fn();

      core.overrideWindowOpen();

      expect(window.open).toBe(open);
    });
  });

  describe('cordova mocks', () => {
    it('initializes the cordova mocks when requested', async () => {
      const { mock } = await loadModule({ search: '?mockCordova=true' });

      expect(mock.logger.info).toHaveBeenCalledWith('Mocking cordova object', null);
      expect(mock.cordova.plugin.http).toBeDefined();
      expect(mock.cordova.plugin.ztvLockscreen).toBeDefined();
      expect(mock.cordova.InAppBrowser).toBeDefined();
    });

    it('sends mocked session status requests', async () => {
      const { mock, ext, core } = await loadModule({ search: '?mockCordova=true' });
      ext.config = serverConfig();
      const successCallback = vi.fn();

      core.mobileHttpRequst('GET', {}, '/kame-house-auth/api/v1/auth/session/status', null, null, successCallback, vi.fn());
      expect(successCallback).toHaveBeenCalledWith(
        expect.objectContaining({ username: 'seiya' }),
        200,
        null,
        { 'Content-Type': 'application/json' }
      );

      const grootCallback = vi.fn();
      core.mobileHttpRequst('GET', {}, '/kame-house-groot/api/v1/commons/session/status.php', null, null, grootCallback, vi.fn());
      expect(grootCallback).toHaveBeenCalledWith(
        expect.objectContaining({ username: 'seiya' }),
        200,
        null,
        { 'Content-Type': 'application/json' }
      );
    });

    it('sends mocked error responses for unknown cordova requests', async () => {
      const { mock, ext, core } = await loadModule({ search: '?mockCordova=true' });
      ext.config = serverConfig();
      const errorCallback = vi.fn();

      core.mobileHttpRequst('GET', {}, '/kame-house/unknown', null, null, vi.fn(), errorCallback);

      expect(errorCallback).toHaveBeenCalledWith(
        '{"code":999, "message":"mocked cordova http error response"}',
        999,
        null,
        undefined
      );
    });

    it('uses basic auth for mocked non mock localhost backend requests', async () => {
      const { mock, ext, core } = await loadModule({ search: '?mockCordova=true' });
      ext.config = serverConfig({ isLoggedIn: true });
      const errorCallback = vi.fn();

      await core.mobileHttpRequst('GET', {}, '/kame-house/unknown', { 'Content-Type': 'application/json' }, { id: 1 }, vi.fn(), errorCallback);

      expect(mock.logger.trace).toHaveBeenCalledWith('Called useBasicAuth on cordova mock', null);
      expect(errorCallback).toHaveBeenCalled();
    });

    it('exposes the mocked cordova http helper functions', async () => {
      const { mock } = await loadModule({ search: '?mockCordova=true' });

      mock.cordova.plugin.http.setDataSerializer('json');
      mock.cordova.plugin.http.setHeader('key', 'value');
      mock.cordova.plugin.http.setRequestTimeout(10);
      mock.cordova.plugin.http.setReadTimeout(10);

      expect(mock.logger.trace).toHaveBeenCalledWith('Called setDataSerializer on cordova mock with json', null);
      expect(mock.logger.trace).toHaveBeenCalledWith('Called setHeader on cordova mock with key:value', null);
      expect(mock.logger.trace).toHaveBeenCalledWith('Called setHeader on cordova mock with 10', null);
    });

    it('exposes the mocked in app browser instance functions', async () => {
      vi.useFakeTimers();
      const { mock } = await loadModule({ search: '?mockCordova=true' });

      const instance = mock.cordova.InAppBrowser.open('http://server-a', '_blank', 'opts');
      instance.addEventListener('loadstop', () => {});
      instance.show();
      instance.close();

      expect(mock.logger.trace).toHaveBeenCalledWith(
        'Called addEventListener on the InAppBrowserInstanceMock for event loadstop',
        null
      );
      expect(mock.logger.trace).toHaveBeenCalledWith('Called show on the InAppBrowserInstanceMock', null);
      expect(mock.logger.trace).toHaveBeenCalledWith('Called close on the InAppBrowserInstanceMock', null);
    });

    it('starts, keeps alive and stops the mocked lock screen', async () => {
      const { mock, ext, core } = await loadModule({ search: '?mockCordova=true' });
      ext.config = serverConfig();

      core.startZtvLockscreen();
      core.keepAliveZtvLockscreen();
      core.stopZtvLockscreen();

      expect(mock.logger.trace).toHaveBeenCalledWith(
        'Called start on ZTV lock screen mock with config: ' + JSON.stringify({
          url: 'http://server-a',
          username: 'goku',
          password: 'kamehameha',
          skipSslCheck: false,
          host: 'localhost'
        }),
        null
      );
    });

    it('opens the mocked in app browser', async () => {
      vi.useFakeTimers();
      const { mock, ext, core, cfg } = await loadModule({ search: '?mockCordova=true' });
      ext.config = serverConfig();
      const browserConfig = {
        getBackend: vi.fn(() => ({ servers: [{ name: 'ServerA', url: 'http://server-a' }] })),
        getInAppBrowserConfig: vi.fn(() => ({ target: '_system', options: 'opts' }))
      };
      cfg.getBackend = browserConfig.getBackend;
      cfg.getInAppBrowserConfig = browserConfig.getInAppBrowserConfig;
      const alertSpy = vi.fn();
      (global as any).alert = alertSpy;

      core.openBrowser('ServerA');
      vi.advanceTimersByTime(100);
      expect(alertSpy).toHaveBeenCalled();
      vi.advanceTimersByTime(3000);
      expect(mock.plugin.modal.basicModal.close).toHaveBeenCalled();
    });
  });
});
