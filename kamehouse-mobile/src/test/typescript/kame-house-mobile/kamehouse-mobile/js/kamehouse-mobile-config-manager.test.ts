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

function createFileSystem(mode: 'success' | 'error' = 'success') {
  const fileWriter = { write: vi.fn() };
  const fileEntry: any = {
    name: 'kamehouse-mobile-config.json',
    createWriter: vi.fn((success: any) => {
      if (mode === 'success') {
        success(fileWriter);
      }
    }),
    file: vi.fn((success: any) => {
      if (mode === 'success') {
        success({ name: 'config' });
      }
    }),
    remove: vi.fn((success: any) => {
      if (mode === 'success') {
        success();
      }
    })
  };
  const fs = {
    root: {
      getFile: vi.fn((_name: string, _options: any, success: any, error: any) => {
        if (mode === 'success') {
          success(fileEntry);
        } else {
          error(new Error('file error'));
        }
      })
    }
  };
  return { fs, fileEntry, fileWriter };
}

class FakeFileReader {
  result: any = 'encrypted-content';
  onloadend: any = null;
  readAsText() {
    if (this.onloadend) {
      this.onloadend();
    }
  }
}

describe('kamehouse-mobile-config-manager', () => {
  beforeEach(() => {
    vi.resetModules();
    window.history.pushState({}, '', '/');
    (global as any).FileReader = FakeFileReader;
    (global as any).CryptoJS = {
      AES: {
        encrypt: vi.fn(() => ({ toString: () => 'encrypted-content' })),
        decrypt: vi.fn(() => ({ toString: () => '{"backend":{"selected":"ServerA","servers":[]}}' }))
      },
      enc: { Utf8: 'utf8' }
    };
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  async function loadConfigManager(options: any = {}) {
    const documentMock = options.document || createDocumentMock(options.elements || {});
    const mock = installKameHouseMock({
      ...(options.overrides || {}),
      util: {
        ...((options.overrides || {}).util || {}),
        module: {
          waitForModules: options.waitForModules || vi.fn(),
          isModuleLoaded: () => false,
          setModuleLoaded: vi.fn()
        }
      },
      plugin: {
        modal: createModal(),
        debugger: createDebugger(),
        ...((options.overrides || {}).plugin || {})
      }
    }, { document: documentMock });
    await import('@/kame-house-mobile/kamehouse-mobile/js/kamehouse-mobile');
    return {
      mock,
      ext: mock.extension.mobile,
      core: mock.extension.mobile.core,
      cfg: mock.extension.mobile.configManager,
      documentMock
    };
  }

  function setupConfiguredServer(ext: any, overrides: any = {}) {
    ext.config = {
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

  describe('init', () => {
    it('initializes the config, loads the encryption key and reads the config file', async () => {
      const { fs } = createFileSystem();
      (window as any).requestFileSystem = vi.fn((_type: any, _size: any, success: any) => success(fs));
      const { mock, ext, cfg } = await loadConfigManager();
      mock.util.module.waitForModules.mockReset();
      let captured: any;
      mock.util.module.waitForModules.mockImplementation((_modules: any, cb: any) => { captured = cb; });
      mock.util.fetch.loadFile.mockImplementation(async (path: string) => {
        if (path.includes('backend.json')) {
          return '{"selected":"DefaultServer","servers":[{"name":"DefaultServer","url":"http://default"}]}';
        }
        return 'encryption-key';
      });

      cfg.init();
      await captured();

      expect(mock.logger.info).toHaveBeenCalledWith('Initializing mobile config manager', null);
      expect(mock.util.fetch.loadFile).toHaveBeenCalledWith('/kame-house-mobile/json/config/backend.json');
      expect(mock.util.fetch.loadFile).toHaveBeenCalledWith('/kame-house-mobile/encryption.key');
      expect(ext.config.backend.selected).toBe('ServerA');
      expect(mock.util.module.setModuleLoaded).toHaveBeenCalledWith('kameHouseMobile');
    });

    it('regenerates the config file when the read config is invalid', async () => {
      const { fs } = createFileSystem();
      (window as any).requestFileSystem = vi.fn((_type: any, _size: any, success: any) => success(fs));
      (global as any).CryptoJS.AES.decrypt = vi.fn(() => ({ toString: () => '{}' }));
      const { mock, ext, cfg } = await loadConfigManager();
      cfg.reGenerateMobileConfigFile = vi.fn();
      mock.util.module.waitForModules.mockReset();
      let captured: any;
      mock.util.module.waitForModules.mockImplementation((_modules: any, cb: any) => { captured = cb; });
      mock.util.fetch.loadFile.mockImplementation(async (path: string) => {
        if (path.includes('backend.json')) {
          return '{"selected":"DefaultServer","servers":[]}';
        }
        return 'encryption-key';
      });

      cfg.init();
      await captured();

      expect(mock.logger.warn).toHaveBeenCalledWith('Mobile config file read from file is invalid. Re generating it', null);
      expect(cfg.reGenerateMobileConfigFile).toHaveBeenCalledWith(false);
      expect(ext.config.backend.selected).toBe('DefaultServer');
    });

    it('handles errors parsing the config file content', async () => {
      const { fs } = createFileSystem();
      (window as any).requestFileSystem = vi.fn((_type: any, _size: any, success: any) => success(fs));
      (global as any).CryptoJS.AES.decrypt = vi.fn(() => { throw new Error('decrypt error'); });
      const { mock, cfg } = await loadConfigManager();
      cfg.reGenerateMobileConfigFile = vi.fn();
      mock.util.module.waitForModules.mockReset();
      let captured: any;
      mock.util.module.waitForModules.mockImplementation((_modules: any, cb: any) => { captured = cb; });
      mock.util.fetch.loadFile.mockResolvedValue('{"selected":"DefaultServer","servers":[]}');

      cfg.init();
      await captured();

      expect(mock.logger.error).toHaveBeenCalledWith(
        'Error parsing file content as json. Error ' + JSON.stringify(new Error('decrypt error'), null, null),
        null
      );
      expect(cfg.reGenerateMobileConfigFile).toHaveBeenCalledWith(false);
    });

    it('creates a new config file when reading fails', async () => {
      (window as any).requestFileSystem = vi.fn((_type: any, _size: any, _success: any, error: any) => error('read error'));
      const { mock, cfg } = await loadConfigManager();
      mock.util.module.waitForModules.mockReset();
      let captured: any;
      mock.util.module.waitForModules.mockImplementation((_modules: any, cb: any) => { captured = cb; });
      mock.util.fetch.loadFile.mockResolvedValue('{"selected":"DefaultServer","servers":[]}');

      cfg.init();
      await captured();

      expect(mock.logger.error).toHaveBeenCalledWith(
        'Error reading file kamehouse-mobile-config.json. Error: "read error"',
        null
      );
      expect(mock.util.module.setModuleLoaded).toHaveBeenCalledWith('kameHouseMobile');
    });

    it('handles exceptions requesting the file system while reading', async () => {
      (window as any).requestFileSystem = vi.fn(() => { throw new Error('request error'); });
      const { mock, cfg } = await loadConfigManager();
      mock.util.module.waitForModules.mockReset();
      let captured: any;
      mock.util.module.waitForModules.mockImplementation((_modules: any, cb: any) => { captured = cb; });
      mock.util.fetch.loadFile.mockResolvedValue('{"selected":"DefaultServer","servers":[]}');

      cfg.init();
      await captured();

      expect(mock.logger.error).toHaveBeenCalledWith(
        'Error reading file kamehouse-mobile-config.json. Error: ' + JSON.stringify(new Error('request error'), null, null),
        null
      );
      expect(mock.util.module.setModuleLoaded).toHaveBeenCalledWith('kameHouseMobile');
    });
  });

  describe('reGenerateMobileConfigFile', () => {
    it('deletes, recreates and rewrites the config file', async () => {
      const { fs, fileWriter } = createFileSystem();
      (window as any).requestFileSystem = vi.fn((_type: any, _size: any, success: any) => success(fs));
      const { mock, ext, cfg } = await loadConfigManager();
      setupConfiguredServer(ext);

      cfg.reGenerateMobileConfigFile(true);

      expect(fileWriter.write).toHaveBeenCalled();
      expect(mock.plugin.modal.basicModal.openAutoCloseable).toHaveBeenCalledWith('Settings saved', 1000);
      expect(mock.logger.info).toHaveBeenCalledWith('File kamehouse-mobile-config.json deleted successfully', null);
      expect(mock.logger.info).toHaveBeenCalledWith('File kamehouse-mobile-config.json recreated successfully', null);
    });

    it('handles errors encrypting the config file content', async () => {
      const { fs } = createFileSystem();
      (window as any).requestFileSystem = vi.fn((_type: any, _size: any, success: any) => success(fs));
      const { mock, ext, cfg } = await loadConfigManager();
      setupConfiguredServer(ext);
      (global as any).CryptoJS.AES.encrypt = vi.fn(() => { throw new Error('encrypt error'); });

      cfg.reGenerateMobileConfigFile(false);

      expect(mock.logger.error).toHaveBeenCalledWith(
        'Error writing config file kamehouse-mobile-config.json. Error: ' + JSON.stringify(new Error('encrypt error'), null, null),
        null
      );
    });

    it('shows an error modal when the file operations fail', async () => {
      const { fs } = createFileSystem('error');
      (window as any).requestFileSystem = vi.fn((_type: any, _size: any, success: any) => success(fs));
      const { mock, ext, cfg } = await loadConfigManager();
      setupConfiguredServer(ext);

      cfg.reGenerateMobileConfigFile(true);

      expect(mock.logger.error).toHaveBeenCalledWith(
        'Error deleting file kamehouse-mobile-config.json. Error: ' + JSON.stringify(new Error('file error'), null, null),
        null
      );
      expect(mock.plugin.modal.basicModal.openAutoCloseable).toHaveBeenCalledWith('Error saving settings', 1000);
    });

    it('skips regeneration while a regeneration is in progress', async () => {
      (window as any).requestFileSystem = vi.fn(() => {});
      const { mock, ext, cfg } = await loadConfigManager();
      setupConfiguredServer(ext);

      cfg.reGenerateMobileConfigFile(false);
      cfg.reGenerateMobileConfigFile(false);

      expect(mock.logger.warn).toHaveBeenCalledWith('A regenerate file is already in progress, skipping this call', null);
    });

    it('handles exceptions requesting the file system', async () => {
      (window as any).requestFileSystem = vi.fn(() => { throw new Error('request error'); });
      const { mock, ext, cfg } = await loadConfigManager();
      setupConfiguredServer(ext);

      cfg.reGenerateMobileConfigFile(true);
      cfg.reGenerateMobileConfigFile(false);

      expect(mock.logger.error).toHaveBeenCalledWith(
        'Error regenerating file kamehouse-mobile-config.json. Error: ' + JSON.stringify(new Error('request error'), null, null),
        null
      );
      expect(mock.plugin.modal.basicModal.openAutoCloseable).toHaveBeenCalledWith('Error saving settings', 1000);
    });
  });

  describe('update config', () => {
    function updateElements() {
      return {
        'backend-server-dropdown': {
          options: [{ selected: true, textContent: 'ServerA' }, { selected: false, textContent: 'ServerB' }]
        },
        'backend-server-input': { value: 'http://updated' },
        'backend-skip-ssl-check-checkbox': { checked: true },
        'backend-username-input': { value: 'vegeta' },
        'backend-password-input': { value: 'final-flash' }
      };
    }

    it('updates the entire mobile config from the view', async () => {
      const { ext, cfg } = await loadConfigManager({ elements: updateElements() });
      setupConfiguredServer(ext);
      cfg.reGenerateMobileConfigFile = vi.fn();

      cfg.updateMobileConfigFromView();

      expect(ext.config.backend.selected).toBe('ServerA');
      expect(ext.config.backend.servers[0].url).toBe('http://updated');
      expect(ext.config.backend.servers[0].skipSslCheck).toBe(true);
      expect(ext.config.backend.servers[0].username).toBe('vegeta');
      expect(ext.config.backend.servers[0].password).toBe('final-flash');
      expect(cfg.reGenerateMobileConfigFile).toHaveBeenCalledWith(false);
    });

    it('sets the backend view from the dropdown', async () => {
      const elements: any = updateElements();
      elements['backend-server-dropdown'].value = 'http://dropdown';
      const { mock, ext, cfg, documentMock } = await loadConfigManager({ elements });
      setupConfiguredServer(ext);
      cfg.reGenerateMobileConfigFile = vi.fn();
      mock.util.dom.setValue = vi.fn((input: any, value: any) => { input.value = value; });

      cfg.setBackendViewFromDropdown();

      expect(mock.util.dom.setValue).toHaveBeenCalledWith(elements['backend-server-input'], 'http://dropdown');
      expect(cfg.reGenerateMobileConfigFile).toHaveBeenCalledWith(false);
      expect(documentMock.getElementById).toHaveBeenCalledWith('backend-server-dropdown');
    });
  });

  describe('backend view', () => {
    function dropdown(options: any[]) {
      return { options };
    }

    function setupRefreshState(ext: any, options: any = {}) {
      setupConfiguredServer(ext, {
        isLoggedIn: options.loggedIn === undefined ? false : options.loggedIn,
        isUrlEditable: options.editable === undefined ? true : options.editable
      });
    }

    it('initializes the backend server dropdown from the config', async () => {
      const dropdownElement = dropdown([]);
      const { mock, ext, cfg } = await loadConfigManager({
        elements: { 'backend-server-dropdown': dropdownElement }
      });
      setupConfiguredServer(ext);

      cfg.initBackendOptionsFromConfig();

      expect(mock.util.dom.getOption).toHaveBeenCalledWith(
        { value: 'http://server-a' },
        'ServerA'
      );
      expect(mock.util.dom.append).toHaveBeenCalledWith(dropdownElement, expect.anything());
    });

    it('logs an error when backend servers are not available', async () => {
      const { mock, ext, cfg } = await loadConfigManager();
      ext.config = {};

      cfg.initBackendOptionsFromConfig();

      expect(mock.logger.error).toHaveBeenCalledWith(
        'Unable to get backend servers from config to populate dropdown',
        null
      );
    });

    it('refreshes the backend view for an editable, logged in server', async () => {
      const dropdownElement = dropdown([
        { textContent: 'ServerB', selected: false },
        { textContent: 'ServerA', selected: false }
      ]);
      const input = { value: '', readOnly: true };
      const elements = {
        'backend-server-dropdown': dropdownElement,
        'backend-server-input': input,
        'backend-skip-ssl-check-checkbox': { checked: false },
        'backend-username-input': {},
        'backend-password-input': {}
      };
      const { mock, ext, cfg } = await loadConfigManager({ elements });
      setupRefreshState(ext, { loggedIn: true, editable: true });

      cfg.refreshBackendServerViewFromConfig();

      expect(dropdownElement.options[1].selected).toBe(true);
      expect(dropdownElement.options[0].selected).toBe(false);
      expect(input.value).toBe('http://server-a');
      expect(input.readOnly).toBe(false);
      expect(mock.util.dom.classListAddById).toHaveBeenCalledWith('backend-username-password', 'hidden-kh');
      expect(mock.util.dom.classListRemoveById).toHaveBeenCalledWith('backend-logout-btn', 'hidden-kh');
    });

    it('refreshes the backend view for a non editable, logged out server', async () => {
      const dropdownElement = dropdown([
        { textContent: 'ServerA', selected: false },
        { textContent: 'ServerB', selected: false }
      ]);
      const input = { value: 'existing', readOnly: false };
      const elements = {
        'backend-server-dropdown': dropdownElement,
        'backend-server-input': input,
        'backend-skip-ssl-check-checkbox': { checked: true },
        'backend-username-input': {},
        'backend-password-input': {}
      };
      const { mock, ext, cfg } = await loadConfigManager({ elements });
      setupRefreshState(ext, { loggedIn: false, editable: false });

      cfg.refreshBackendServerViewFromConfig();

      expect(dropdownElement.options[0].selected).toBe(true);
      expect(dropdownElement.options[1].selected).toBe(false);
      expect(input.readOnly).toBe(true);
      expect(mock.util.dom.classListRemoveById).toHaveBeenCalledWith('backend-username-password', 'hidden-kh');
      expect(mock.util.dom.classListAddById).toHaveBeenCalledWith('backend-logout-btn', 'hidden-kh');
    });
  });

  describe('reset defaults', () => {
    it('opens the reset confirmation modal and resets defaults', async () => {
      const elements = {
        'backend-server-dropdown': { options: [] },
        'backend-server-input': { value: '' },
        'backend-skip-ssl-check-checkbox': { checked: false },
        'backend-username-input': {},
        'backend-password-input': {}
      };
      const { mock, ext, cfg } = await loadConfigManager({ elements });
      setupConfiguredServer(ext);
      cfg.reGenerateMobileConfigFile = vi.fn();
      cfg.refreshBackendServerViewFromConfig = vi.fn();

      cfg.confirmResetDefaults();

      expect(mock.plugin.modal.basicModal.setHtml).toHaveBeenCalled();
      expect(mock.plugin.modal.basicModal.open).toHaveBeenCalled();
      expect(mock.util.dom.getButton).toHaveBeenCalled();
      const buttonConfig = mock.util.dom.getButton.mock.calls[0][0];
      buttonConfig.click();

      expect(mock.plugin.modal.basicModal.close).toHaveBeenCalled();
      expect(ext.config.backend).toBeNull();
      expect(cfg.reGenerateMobileConfigFile).toHaveBeenCalledWith(false);
      expect(cfg.refreshBackendServerViewFromConfig).toHaveBeenCalled();
      expect(mock.plugin.modal.basicModal.openAutoCloseable).toHaveBeenCalledWith('Settings reset to defaults', 1000);
    });
  });

  describe('file management', () => {
    it('runs the file creation, write, read and delete operations', async () => {
      vi.useFakeTimers();
      const { fs, fileWriter } = createFileSystem();
      (window as any).requestFileSystem = vi.fn((_type: any, _size: any, success: any) => success(fs));
      const { mock, ext, cfg } = await loadConfigManager();
      setupConfiguredServer(ext);

      cfg.testFileManagement();
      vi.advanceTimersByTime(1000);
      expect(mock.logger.info).toHaveBeenCalledWith('Creating file kamehouse-mobile-config.json', null);
      vi.advanceTimersByTime(9000);
      expect(fileWriter.write).toHaveBeenCalled();
      vi.advanceTimersByTime(5000);
      expect(mock.logger.info).toHaveBeenCalledWith('Reading file kamehouse-mobile-config.json', null);
      vi.advanceTimersByTime(5000);
      expect(mock.logger.info).toHaveBeenCalledWith('Deleting file kamehouse-mobile-config.json', null);
    });

    it('handles errors encrypting the file content during file management', async () => {
      vi.useFakeTimers();
      const { fs } = createFileSystem();
      (window as any).requestFileSystem = vi.fn((_type: any, _size: any, success: any) => success(fs));
      const { mock, ext, cfg } = await loadConfigManager();
      setupConfiguredServer(ext);
      (global as any).CryptoJS.AES.encrypt = vi.fn(() => { throw new Error('encrypt error'); });

      cfg.testFileManagement();
      vi.advanceTimersByTime(10000);

      expect(mock.logger.error).toHaveBeenCalledWith(
        'Error writing config file kamehouse-mobile-config.json. Error: ' + JSON.stringify(new Error('encrypt error'), null, null),
        null
      );
    });

    it('handles file operation errors', async () => {
      vi.useFakeTimers();
      const { fs } = createFileSystem('error');
      (window as any).requestFileSystem = vi.fn((_type: any, _size: any, success: any) => success(fs));
      const { mock, ext, cfg } = await loadConfigManager();
      setupConfiguredServer(ext);

      cfg.testFileManagement();
      vi.advanceTimersByTime(20000);

      expect(mock.logger.error).toHaveBeenCalledWith(
        'Error creating file kamehouse-mobile-config.json. Error: ' + JSON.stringify(new Error('file error'), null, null),
        null
      );
      expect(mock.logger.error).toHaveBeenCalledWith(
        'Error writing file kamehouse-mobile-config.json. Error: ' + JSON.stringify(new Error('file error'), null, null),
        null
      );
      expect(mock.logger.error).toHaveBeenCalledWith(
        'Error reading file kamehouse-mobile-config.json. Error: ' + JSON.stringify(new Error('file error'), null, null),
        null
      );
      expect(mock.logger.error).toHaveBeenCalledWith(
        'Error deleting file kamehouse-mobile-config.json. Error: ' + JSON.stringify(new Error('file error'), null, null),
        null
      );
    });

    it('handles exceptions requesting the file system for file operations', async () => {
      vi.useFakeTimers();
      (window as any).requestFileSystem = vi.fn(() => { throw new Error('request error'); });
      const { mock, ext, cfg } = await loadConfigManager();
      setupConfiguredServer(ext);

      cfg.testFileManagement();
      vi.advanceTimersByTime(20000);

      expect(mock.logger.error).toHaveBeenCalledWith(
        'Error creating file kamehouse-mobile-config.json. Error: ' + JSON.stringify(new Error('request error'), null, null),
        null
      );
      expect(mock.logger.error).toHaveBeenCalledWith(
        'Error writing file kamehouse-mobile-config.json. Error: ' + JSON.stringify(new Error('request error'), null, null),
        null
      );
      expect(mock.logger.error).toHaveBeenCalledWith(
        'Error reading file kamehouse-mobile-config.json. Error: ' + JSON.stringify(new Error('request error'), null, null),
        null
      );
      expect(mock.logger.error).toHaveBeenCalledWith(
        'Error deleting file kamehouse-mobile-config.json. Error: ' + JSON.stringify(new Error('request error'), null, null),
        null
      );
    });
  });
});
