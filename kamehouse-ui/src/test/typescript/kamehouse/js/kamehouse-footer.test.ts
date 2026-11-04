/// <reference types="vitest/globals" />

import { describe, it, expect, vi, beforeEach } from 'vitest';
import { installKameHouseMock, createDocumentMock } from '../../helpers/kamehouse-mock';
import '@/kamehouse/js/kamehouse-footer';

function flush() {
  return new Promise((resolve) => setTimeout(resolve, 0));
}

describe('kamehouse-footer', () => {
  beforeEach(() => {
    vi.resetModules();
  });

  function createFooter(overrides: any = {}, options: any = {}) {
    const mock = installKameHouseMock(overrides, {
      window: { location: { search: '', pathname: '/kame-house/' } },
      document: createDocumentMock(),
      ...options
    });
    return { mock, footer: new (globalThis as any).KameHouseFooter() };
  }

  it('loads the footer with build and session info', async () => {
    const { mock, footer } = createFooter({
      core: { isGRootPage: () => false, isBatcavePage: () => false },
      session: { server: 'server-1' },
      util: {
        fetch: { loadFile: vi.fn(async () => JSON.stringify({ buildVersion: '1.0', buildDate: 'today' })) }
      }
    });

    footer.load();
    await flush();

    expect(mock.util.dom.append).toHaveBeenCalled();
    expect(mock.util.dom.loadById).toHaveBeenCalledWith('kamehouse-footer-container', expect.any(String), expect.any(Function));
    expect(mock.core.configDynamicHtml).toHaveBeenCalled();
    expect(mock.util.dom.setHtmlById).toHaveBeenCalledWith('footer-server-name', 'server-1');
    expect(mock.util.dom.setHtmlById).toHaveBeenCalledWith('footer-build-version', '1.0');
    expect(mock.util.dom.setHtmlById).toHaveBeenCalledWith('footer-build-date', 'today');
    expect(mock.util.module.setModuleLoaded).toHaveBeenCalledWith('kameHouseFooter');
  });

  it('skips session info on groot pages', async () => {
    const { mock, footer } = createFooter({
      core: { isGRootPage: () => true, isBatcavePage: () => false },
      session: { server: 'server-1' },
      util: { fetch: { loadFile: vi.fn(async () => '{}') } }
    });

    footer.load();
    await flush();

    expect(mock.util.dom.setHtmlById).not.toHaveBeenCalledWith('footer-server-name', 'server-1');
  });

  it('does not set the server name when the session server is empty', async () => {
    const { mock, footer } = createFooter({
      core: { isGRootPage: () => false, isBatcavePage: () => false },
      session: {},
      util: { fetch: { loadFile: vi.fn(async () => '{}') } }
    });

    footer.load();
    await flush();

    expect(mock.util.dom.setHtmlById).not.toHaveBeenCalledWith('footer-server-name', expect.anything());
  });

  it('logs an error when the build info is empty', async () => {
    const { mock, footer } = createFooter({
      core: { isGRootPage: () => false, isBatcavePage: () => false },
      session: {},
      util: { fetch: { loadFile: vi.fn(async () => '') } }
    });

    footer.load();
    await flush();

    expect(mock.logger.error).toHaveBeenCalledWith('Unable to load build-info.json', null);
  });

  it('skips missing build info fields', async () => {
    const { mock, footer } = createFooter({
      core: { isGRootPage: () => false, isBatcavePage: () => false },
      session: {},
      util: { fetch: { loadFile: vi.fn(async () => '{}') } }
    });

    footer.load();
    await flush();

    expect(mock.util.dom.setHtmlById).not.toHaveBeenCalledWith('footer-build-version', expect.anything());
    expect(mock.util.dom.setHtmlById).not.toHaveBeenCalledWith('footer-build-date', expect.anything());
  });

  it('loads the batcave build info', async () => {
    const { mock, footer } = createFooter({
      core: { isGRootPage: () => false, isBatcavePage: () => true },
      session: {},
      util: {
        fetch: { loadFile: vi.fn(async () => JSON.stringify({ buildVersion: '2.0', buildDate: 'yesterday' })) }
      }
    });

    footer.load();
    await flush();

    expect(mock.util.dom.setHtmlById).toHaveBeenCalledWith('footer-build-version', '2.0');
    expect(mock.util.dom.setHtmlById).toHaveBeenCalledWith('footer-build-date', 'yesterday');
  });

  it('handles empty batcave build info', async () => {
    const { mock, footer } = createFooter({
      core: { isGRootPage: () => false, isBatcavePage: () => true },
      session: {},
      util: { fetch: { loadFile: vi.fn(async () => '') } }
    });

    footer.load();
    await flush();

    expect(mock.logger.error).toHaveBeenCalledWith('Unable to load batcave build-info.json', null);
  });

  it('skips missing batcave build info fields', async () => {
    const { mock, footer } = createFooter({
      core: { isGRootPage: () => false, isBatcavePage: () => true },
      session: {},
      util: { fetch: { loadFile: vi.fn(async () => '{}') } }
    });

    footer.load();
    await flush();

    expect(mock.util.dom.setHtmlById).not.toHaveBeenCalledWith('footer-build-version', expect.anything());
  });

  it('handles errors loading the batcave build info', async () => {
    const { mock, footer } = createFooter({
      core: { isGRootPage: () => false, isBatcavePage: () => true },
      session: {},
      util: { fetch: { loadFile: vi.fn(async () => { throw new Error('network'); }) } }
    });

    footer.load();
    await flush();

    expect(mock.logger.error).toHaveBeenCalledWith('Error loading batcave build-info.json', null);
  });

  it('removes the footer module on mobile', async () => {
    const { mock, footer } = createFooter({
      core: { isGRootPage: () => false, isBatcavePage: () => false },
      session: {},
      util: {
        mobile: { exec: (desktop: any, mobile: any) => mobile() },
        fetch: { loadFile: vi.fn(async () => '{}') }
      }
    });

    footer.load();
    await flush();

    expect(mock.util.dom.removeById).toHaveBeenCalledWith('footer-module');
  });
});
