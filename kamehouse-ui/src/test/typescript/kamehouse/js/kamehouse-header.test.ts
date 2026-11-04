/// <reference types="vitest/globals" />

import { describe, it, expect, vi, beforeEach } from 'vitest';
import { installKameHouseMock, createDocumentMock } from '../../helpers/kamehouse-mock';
import '@/kamehouse/js/kamehouse-header';

describe('kamehouse-header', () => {
  beforeEach(() => {
    vi.resetModules();
  });

  function createHeader(overrides: any = {}, options: any = {}) {
    const mock = installKameHouseMock(overrides, {
      window: { location: { search: '', pathname: '/kame-house/' } },
      document: createDocumentMock(),
      ...options
    });
    return { mock, header: new (globalThis as any).KameHouseHeader() };
  }

  function navItems() {
    const ids = ['nav-home', 'nav-about', 'nav-admin'];
    return ids.map((id) => ({ id, getAttribute: vi.fn(() => id), classList: { add: vi.fn(), remove: vi.fn() } }));
  }

  it('loads the header and sets the active home tab', async () => {
    const items = navItems();
    const doc: any = createDocumentMock();
    doc.querySelectorAll = vi.fn(() => items);
    const { mock, header } = createHeader({ loginStatus: {} }, { document: doc });

    header.load();

    expect(mock.logger.info).toHaveBeenCalledWith('Loading header', null);
    expect(mock.util.dom.prepend).toHaveBeenCalled();
    expect(mock.util.dom.loadById).toHaveBeenCalledWith('kamehouse-header-container', expect.any(String), expect.any(Function));
    expect(mock.util.dom.classListAdd).toHaveBeenCalledWith(items[0], 'active');
    expect(mock.util.module.setModuleLoaded).toHaveBeenCalledWith('kameHouseHeader');
  });

  it('sets the active tab for a matching page', async () => {
    const items = navItems();
    const doc: any = createDocumentMock();
    doc.querySelectorAll = vi.fn(() => items);
    const { mock, header } = createHeader({}, {
      document: doc,
      window: { location: { search: '', pathname: '/kame-house/about' } }
    });

    header.load();

    expect(mock.util.dom.classListAdd).toHaveBeenCalledWith(items[1], 'active');
  });

  it('renders the login button for an anonymous user', async () => {
    const doc: any = createDocumentMock();
    doc.querySelectorAll = vi.fn(() => []);
    const { mock, header } = createHeader({ session: { username: 'anonymousUser' } }, { document: doc });

    header.load();

    expect(mock.util.dom.empty).toHaveBeenCalledWith(expect.anything());
    expect(mock.util.dom.append).toHaveBeenCalledWith(expect.anything(), expect.anything());
  });

  it('renders the login button for an empty username', async () => {
    const doc: any = createDocumentMock();
    doc.querySelectorAll = vi.fn(() => []);
    const { mock, header } = createHeader({ session: { username: '   ' } }, { document: doc });

    header.load();

    expect(mock.util.dom.append).toHaveBeenCalled();
  });

  it('renders the username and logout button for a logged in user', async () => {
    const doc: any = createDocumentMock();
    doc.querySelectorAll = vi.fn(() => []);
    const { mock, header } = createHeader({ session: { username: 'goku' } }, { document: doc });

    header.load();

    expect(mock.util.dom.getSpan).toHaveBeenCalled();
    expect(mock.util.dom.append).toHaveBeenCalled();
  });

  it('toggles the responsive header nav on and off', () => {
    const menu = { className: 'header-nav', classList: { add: vi.fn(), remove: vi.fn() } };
    const doc: any = createDocumentMock({ 'header-menu': menu });
    const { mock, header } = createHeader({}, { document: doc });

    header.toggleHeaderNav();
    expect(mock.util.dom.classListAdd).toHaveBeenCalledWith(menu, 'responsive');

    menu.className = '';
    header.toggleHeaderNav();
    expect(mock.util.dom.classListRemove).toHaveBeenCalledWith(menu, 'responsive');
  });

  it('toggles debug mode when the debug div exists', () => {
    const doc: any = createDocumentMock({ 'debug-mode': {} });
    const { mock, header } = createHeader({
      plugin: { debugger: { toggleDebugMode: vi.fn() } }
    }, { document: doc });

    header.toggleDebugMode();

    expect(mock.plugin.debugger.toggleDebugMode).toHaveBeenCalled();
  });

  it('shows a modal when there is no debug mode in the page', () => {
    const doc: any = createDocumentMock();
    doc.getElementById = vi.fn(() => null);
    const { mock, header } = createHeader({
      plugin: { modal: { basicModal: { openAutoCloseable: vi.fn() } } }
    }, { document: doc });

    header.toggleDebugMode();

    expect(mock.plugin.modal.basicModal.openAutoCloseable).toHaveBeenCalledWith('No debug mode in this page', 1000);
  });
});
