/// <reference types="vitest/globals" />

import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';

describe('error', () => {
  beforeEach(() => {
    vi.resetModules();
    vi.spyOn(console, 'log').mockImplementation(() => {});
  });

  afterEach(() => {
    vi.restoreAllMocks();
  });

  function installGlobals(options: any = {}) {
    const head = { append: vi.fn() };
    const chain: any = {};
    chain.done = vi.fn((cb: any) => { if (options.scriptSuccess) cb(); return chain; });
    chain.fail = vi.fn((cb: any) => { if (options.scriptFailure) cb(); return chain; });

    const jqElement: any = {
      append: vi.fn(),
      load: vi.fn((path: any, cb: any) => { if (cb) cb(); }),
      ready: vi.fn((cb: any) => { if (cb) cb(); return jqElement; })
    };
    const jq: any = vi.fn(() => jqElement);
    jq.getScript = vi.fn(() => chain);

    if (options.noJquery) {
      delete (global as any).$;
      delete (global as any).jQuery;
    } else {
      (global as any).$ = jq;
      (global as any).jQuery = jq;
    }
    (global as any).window = { location: { search: options.search || '', pathname: '/kame-house/error/error.html' } };
    const elements: any = {};
    ['kamehouse-error-header', 'kamehouse-error-footer', 'error-header-login-status-btn', 'error-header-login-status-text'].forEach((id) => {
      elements[id] = {
        className: 'header-nav',
        classList: { add: vi.fn(), remove: vi.fn() },
        remove: vi.fn()
      };
    });
    (global as any).document = {
      readyState: 'complete',
      addEventListener: vi.fn(),
      querySelectorAll: vi.fn(() => []),
      getElementById: vi.fn((id: string) => elements[id] || null),
      getElementsByTagName: vi.fn((tag: string) => tag === 'head' ? [head] : []),
      createElement: vi.fn(() => ({ style: {}, innerHTML: '', appendChild: vi.fn() }))
    };
    return { jq, jqElement, head, elements };
  }

  it('loads the error page and the kamehouse js', async () => {
    const { jq } = installGlobals({ scriptSuccess: true });
    await import('@/error/js/error');

    expect(jq.getScript).toHaveBeenCalledWith('/kame-house/kamehouse/js/kamehouse.js');
    expect(jq).toHaveBeenCalled();
  });

  it('keeps the error page header when kamehouse js fails to load', async () => {
    const { elements } = installGlobals({ scriptFailure: true });
    await import('@/error/js/error');

    expect(elements['error-header-login-status-btn'].classList.remove).toHaveBeenCalledWith('rotate-4');
    expect(elements['error-header-login-status-text'].remove).toHaveBeenCalled();
  });

  it('skips loading kamehouse js when the url parameter is set', async () => {
    const { jq } = installGlobals({ search: '?skipKameHouseJs=true' });
    await import('@/error/js/error');

    expect(jq.getScript).not.toHaveBeenCalled();
  });

  it('falls back to an empty jquery when jquery is not available', async () => {
    installGlobals({ noJquery: true, search: '' });

    await expect(import('@/error/js/error')).rejects.toThrow();

    expect(console.log).toHaveBeenCalledWith(expect.stringContaining('Error setting jquery on kamehouse error page'));
  });
});
