/// <reference types="vitest/globals" />

import { describe, it, expect, vi, beforeEach } from 'vitest';
import { installKameHouseMock, createDocumentMock } from '../../helpers/kamehouse-mock';

describe('kamehouse-groot', () => {
  let get: any;
  let buttons: any[];
  let navItems: any[];

  beforeEach(() => {
    vi.resetModules();
    get = vi.fn();
    buttons = [];
    navItems = [
      { id: 'nav-groot-home', getAttribute: vi.fn(function(this: any, name: string) { return this.id; }) },
      { id: 'nav-server-manager', getAttribute: vi.fn(function(this: any, name: string) { return this.id; }) },
      { id: 'nav-kamehouse-shell', getAttribute: vi.fn(function(this: any, name: string) { return this.id; }) }
    ];
  });

  function install(pathname = '/kame-house/groot/', href = 'http://localhost/kame-house/groot/') {
    const doc: any = createDocumentMock({
      'kamehouse-splashscreen-img': { setAttribute: vi.fn() },
      'groot-header-login-status': {},
      'login-status': {}
    });
    doc.querySelectorAll = vi.fn((selector: string) => selector === '#groot-menu button' ? navItems : []);
    return installKameHouseMock({
      extension: { groot: {} },
      core: { completeAuthorizeUser: vi.fn() },
      http: { get },
      plugin: { debugger: { http: { get } } },
      util: {
        dom: {
          getButton: vi.fn((options: any) => { buttons.push(options); return {}; }),
          getSpan: vi.fn((attrs: any, text: any) => ({ attrs, text })),
          getImg: vi.fn((attrs: any) => ({ attrs })),
          loadById: vi.fn((id: any, url: any, cb: any) => cb())
        }
      }
    }, {
      window: { location: { search: '', pathname, href } },
      document: doc
    });
  }

  async function importGroot(pathname?: string, href?: string) {
    const mock = install(pathname, href);
    await import('@/kamehouse-groot/js/kamehouse-groot');
    return { mock, groot: mock.extension.groot, header: mock.extension.groot.header };
  }

  it('loads groot, updates the splashscreen and renders the menu', async () => {
    const { mock, groot } = await importGroot();

    expect(groot).toBeDefined();
    expect(mock.logger.info).toHaveBeenCalledWith('Started initializing groot', null);
    expect(mock.logger.info).toHaveBeenCalledWith('Finished initializing groot', null);
    expect(mock.logger.debug).toHaveBeenCalledWith('Updating splashscreen', null);
    expect(mock.extension.groot.header).toBeDefined();
    expect(mock.util.dom.loadById).toHaveBeenCalledWith('groot-menu-wrapper', '/kame-house/kamehouse-groot/html/kamehouse-groot-menu.html', expect.any(Function));
  });

  it('detects groot pages', async () => {
    const { groot } = await importGroot('/kame-house/groot/', 'http://localhost/kame-house/groot/');
    expect(groot.isGrootPage()).toBe(true);

    vi.resetModules();
    const { groot: other } = await importGroot('/kame-house/', 'http://localhost/kame-house/');
    expect(other.isGrootPage()).toBe(false);
  });

  it('skips the splashscreen update when not on a groot page', async () => {
    const { mock } = await importGroot('/kame-house/', 'http://localhost/kame-house/');
    expect(mock.logger.debug).not.toHaveBeenCalledWith('Updating splashscreen', null);
  });

  it('loads the groot session and updates the header and footer', async () => {
    const { mock } = await importGroot();
    expect(get).toHaveBeenCalledTimes(1);
    const call = get.mock.calls[0];
    expect(call[1]).toBe('/kame-house-groot/api/v1/comons/session/status.php'.replace('comons', 'commons'));
    expect(call[0].timeout).toBe(15);

    call[4]({ username: 'goku', server: 'server1' }, 200, '', {});

    expect(mock.logger.info).toHaveBeenCalledWith('Loading groot session', null);
    expect(mock.extension.groot.session).toEqual({ username: 'goku', server: 'server1' });
    expect(mock.util.module.setModuleLoaded).toHaveBeenCalledWith('kameHouseGrootSession');
    expect(mock.core.completeAuthorizeUser).toHaveBeenCalledWith(200, { username: 'goku', server: 'server1' });
    expect(mock.util.dom.setHtmlById).toHaveBeenCalledWith('footer-server-name', 'server1');
    expect(buttons.length).toBe(1);
  });

  it('handles a failed session request', async () => {
    const { mock } = await importGroot();
    get.mock.calls[0][5]('error', 500, 'Server Error', {});

    expect(mock.logger.error).toHaveBeenCalledWith('Error retrieving current groot session information.', 'Error retrieving current groot session information.');
    expect(mock.extension.groot.session).toEqual({});
    expect(mock.util.module.setModuleLoaded).toHaveBeenCalledWith('kameHouseGrootSession');
    expect(mock.core.completeAuthorizeUser).toHaveBeenCalledWith(500, 'error');
  });

  it('shows the login button for anonymous users', async () => {
    const { mock } = await importGroot();
    get.mock.calls[0][4]({ username: 'anonymousUser' }, 200, '', {});

    expect(buttons.length).toBe(1);
    buttons[0].click({}, null);
    expect(mock.util.mobile.windowLocation).toHaveBeenCalledWith('/kame-house/groot/login.html', '/kame-house-mobile/settings.html');
  });

  it('shows the login button for a blank username', async () => {
    const { mock } = await importGroot();
    get.mock.calls[0][4]({ username: '   ' }, 200, '', {});
    expect(buttons.length).toBe(1);
  });

  it('shows the logout button for logged in users', async () => {
    const { mock } = await importGroot();
    get.mock.calls[0][4]({ username: 'vegeta', server: '' }, 200, '', {});

    expect(buttons.length).toBe(1);
    buttons[0].click({}, null);
    expect(mock.util.mobile.windowLocation).toHaveBeenCalledWith('/kame-house-groot/api/v1/auth/logout.php', '/kame-house-mobile/settings.html');
    expect(mock.util.dom.setHtmlById).not.toHaveBeenCalledWith('footer-server-name', expect.anything());
  });

  it('marks the home menu tab active', async () => {
    const { mock } = await importGroot('/kame-house/groot/index.html', 'http://localhost/kame-house/groot/index.html');
    expect(mock.util.dom.classListAdd).toHaveBeenCalledWith(navItems[0], 'active');
  });

  it('marks the server manager menu tab active', async () => {
    const { mock } = await importGroot('/kame-house/groot/admin/server-manager/index.html', 'http://localhost/kame-house/groot/admin/server-manager/index.html');
    expect(mock.util.dom.classListAdd).toHaveBeenCalledWith(navItems[1], 'active');
  });

  it('marks the kamehouse shell menu tab active', async () => {
    const { mock } = await importGroot('/kame-house/groot/admin/kamehouse-shell/index.html', 'http://localhost/kame-house/groot/admin/kamehouse-shell/index.html');
    expect(mock.util.dom.classListAdd).toHaveBeenCalledWith(navItems[2], 'active');
  });

  it('updates the kamehouse header for groot pages', async () => {
    const { mock } = await importGroot();
    expect(mock.util.dom.getImg).toHaveBeenCalled();
    expect(mock.util.dom.append).toHaveBeenCalledWith('#branding', expect.anything());
  });
});
