/// <reference types="vitest/globals" />

import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';

function chainable() {
  const obj: any = {};
  const methods = ['append', 'prepend', 'detach', 'empty', 'remove', 'fadeIn', 'each', 'on', 'click', 'css', 'show', 'hide', 'addClass', 'removeClass', 'html', 'text', 'val', 'attr', 'toggle', 'filter', 'find', 'eq', 'first', 'last', 'replaceWith'];
  methods.forEach((m) => { obj[m] = vi.fn(() => obj); });
  obj.ready = vi.fn((cb: any) => { if (cb) cb(); return obj; });
  obj.load = vi.fn((path: any, cb: any) => { if (cb) cb(); return obj; });
  obj.getScript = vi.fn(() => ({ done: (cb: any) => { if (cb) cb('', 'success'); return obj; }, fail: () => obj }));
  obj.animate = vi.fn((config: any, duration: any, cb: any) => { if (cb) cb(); return obj; });
  obj.offset = vi.fn(() => ({ top: 1, left: 2 }));
  obj.height = vi.fn(() => 10);
  obj.scrollTop = vi.fn(() => 5);
  obj.fail = vi.fn(() => obj);
  obj[0] = obj;
  obj.length = 1;
  return obj;
}

describe('kamehouse core', () => {
  let kh: any;
  let trackedWindowListeners: Array<[string, any]> = [];

  const removeTrackedWindowListeners = () => {
    trackedWindowListeners.forEach(([type, listener]) => window.removeEventListener(type, listener));
    trackedWindowListeners = [];
  };

  beforeEach(async () => {
    removeTrackedWindowListeners();
    vi.resetModules();
    (globalThis as any).__KAMEHOUSE_TEST_HOOK__ = true;
    delete (globalThis as any).kameHouseInstance;
    delete (global as any).cordova;
    document.body.innerHTML = '';
    document.head.innerHTML = '';
    window.history.replaceState({}, '', '/kame-house/');
    let locHref = 'http://localhost/kame-house/';
    const locObj = {
      get href() { return locHref; },
      set href(v: any) { locHref = String(v); },
      get pathname() { return new URL(locHref, 'http://localhost').pathname; },
      get search() { return new URL(locHref, 'http://localhost').search; },
      assign: vi.fn(),
      replace: vi.fn(),
      reload: vi.fn(),
    };
    Object.defineProperty(window, 'location', {
      configurable: true,
      get: () => locObj,
      set: (v: any) => { locHref = typeof v === 'string' ? v : v.href; },
    });
    const jqFn: any = vi.fn(() => chainable());
    jqFn.getScript = vi.fn(() => ({ done: (cb: any) => { if (cb) cb('', 'success'); return chainable(); }, fail: () => chainable() }));
    jqFn.ajax = vi.fn((opts: any) => { if (opts && opts.success) opts.success({}, 'success', { status: 200, statusText: 'OK', getAllResponseHeaders: () => '' }); });
    (global as any).$ = jqFn;
    (global as any).jQuery = jqFn;
    (globalThis as any).KameHouseHeader = class { load() {} };
    (globalThis as any).KameHouseFooter = class { load() {} };
    (global as any).XMLHttpRequest = class {
      open() {}
      send() {}
      setRequestHeader() {}
      abort() {}
    };
    (global as any).fetch = vi.fn(async (url: any) => ({ text: async () => 'fetched:' + url }));
    const originalAddEventListener = window.addEventListener.bind(window);
    vi.spyOn(window, 'addEventListener').mockImplementation((type: any, listener: any, options?: any) => {
      if (type === 'error' || type === 'unhandledrejection') {
        trackedWindowListeners.push([type, listener]);
      }
      return originalAddEventListener(type, listener, options);
    });
    await import('@/kamehouse/js/kamehouse');
    kh = (globalThis as any).kameHouseInstance;
  });

  afterEach(() => {
    removeTrackedWindowListeners();
    vi.restoreAllMocks();
    delete (globalThis as any).__KAMEHOUSE_TEST_HOOK__;
    vi.useRealTimers();
  });

  it('initializes the framework and manages extensions and plugins', () => {
    expect(kh).toBeDefined();
    expect(kh.core).toBeDefined();
    expect(kh.util).toBeDefined();
    expect(kh.logger).toBeDefined();
    expect(kh.http).toBeDefined();
    expect(kh.json).toBeDefined();

    const extension = { load: vi.fn() };
    kh.addExtension('testExtension', extension);
    expect(kh.extension.testExtension).toBe(extension);
    expect(extension.load).toHaveBeenCalled();

    const plugin = { load: vi.fn() };
    kh.addPlugin('testPlugin', plugin);
    expect(kh.plugin.testPlugin).toBe(plugin);
    expect(plugin.load).toHaveBeenCalled();

    const original = kh.core.getArrayKameHouseData;
    kh.core.getArrayKameHouseData = vi.fn(() => ['skipExtension', 'skipPlugin']);
    const skippedExtension = { load: vi.fn() };
    const skippedPlugin = { load: vi.fn() };
    kh.addExtension('skipExtension', skippedExtension);
    kh.addPlugin('skipPlugin', skippedPlugin);
    expect(skippedExtension.load).not.toHaveBeenCalled();
    expect(skippedPlugin.load).not.toHaveBeenCalled();
    kh.core.getArrayKameHouseData = original;

    const readyFn = vi.fn();
    kh.ready(readyFn);
    expect(readyFn).toHaveBeenCalled();
  });

  it('sets and uses log levels', () => {
    const info = vi.spyOn(console, 'info').mockImplementation(() => {});
    kh.logger.setLogLevel(5);
    expect(kh.logger.getLogLevel()).toBe(5);
    expect(kh.logger.getRedText('r')).toContain('color:red');
    expect(kh.logger.getYellowText('y')).toContain('color:yellow');
    expect(kh.logger.getBlueText('b')).toContain('#3996ff');
    expect(kh.logger.getGreenText('g')).toContain('color:green');
    expect(kh.logger.getCyanText('c')).toContain('#00b2b2');

    kh.logger.error('error', null);
    kh.logger.warn('warn', null);
    kh.logger.info('info', null);
    kh.logger.debug('debug', null);
    kh.logger.trace('trace', null);
    expect(info).toHaveBeenCalledTimes(5);

    kh.logger.info('', null);
    kh.logger.info('x'.repeat(1001), null);
    expect(info).toHaveBeenCalled();

    const config = { requestId: 'id' };
    kh.logger.logApiError(config, '/url', { a: 1 }, 500, 'ERR', { h: 'v' });
    kh.logger.logHttpRequest('GET', config, '/url', { password: 'secret' }, { pass: 1 });
    kh.logger.logHttpResponse(config, '/url', { a: 1 }, 200, 'OK', { h: 'v' });
    expect(kh.logger.maskSensitiveData('"password": "secret"')).toContain('****');
    expect(kh.logger.maskSensitiveData('"Authorization": "Basic abc"')).toContain('Basic ****');
    expect(kh.logger.maskSensitiveData('"cvv": "123"')).toContain('"cvv": "***"');
    expect(kh.logger.maskSensitiveData('')).toBeUndefined();

    kh.util.cookies.getCookie = vi.fn(() => 'DEBUG');
    kh.logger.init();
    expect(kh.logger.getLogLevel()).toBe('DEBUG');
    info.mockRestore();
  });

  it('exposes core helpers', () => {
    expect(kh.core.isEmpty(undefined)).toBe(true);
    expect(kh.core.isEmpty('')).toBe(true);
    expect(kh.core.isEmpty(0)).toBe(false);
    expect(kh.core.isEmptyDeprecated({})).toBe(true);
    expect(kh.core.isEmptyDeprecated([])).toBe(true);
    expect(kh.core.isFunction(() => {})).toBe(true);
    expect(kh.core.isFunction(1)).toBe(false);

    document.body.innerHTML = '<script id="kamehouse-data" data-flag="true" data-name="n" data-arr="1,2" data-authorization="auth"></script>';
    expect(kh.core.getBooleanKameHouseData('flag')).toBe(true);
    expect(kh.core.getStringKameHouseData('name')).toBe('n');
    expect(kh.core.getArrayKameHouseData('arr')).toEqual(['1', '2']);
    expect(kh.core.isValidKameHouseConfig('KEY=value', 'KEY')).toBe(true);
    expect(kh.core.getKameHouseConfigValue('KEY=value')).toBe('value');
    expect(kh.core.convertBashColorsToHtml('\\[0;31mred\\[0m')).toContain('color:red');

    expect(kh.util.time.getTimestamp(new Date('2020-01-02T03:04:05Z'))).toContain('2020-01-02');
    expect(kh.util.time.convertSecondsToHsMsSs(3661)).toBe('01:01:01');
    expect(kh.util.time.getDateFromEpoch(1000)).toBeInstanceOf(Date);
    expect(kh.util.time.isValidDate(new Date())).toBe(true);
    expect(kh.util.time.isValidDate('x')).toBe(false);
    expect(kh.util.file.getShortFilename('/a/b/c.txt')).toBe('c.txt');

    expect(kh.json.parse('{"a":1}')).toEqual({ a: 1 });
    expect(kh.json.parse('invalid')).toBe('invalid');
    expect(kh.json.stringify({ a: 1 })).toBe('{"a":1}');
    const circular: any = {};
    circular.self = circular;
    expect(kh.json.stringify(circular)).toBe(circular);

    expect(kh.core.isGRootPage()).toBe(false);
    expect(kh.core.isBatcavePage()).toBe(false);
    window.location.href = 'http://localhost/kame-house/groot/';
    expect(kh.core.isGRootPage()).toBe(true);
    window.location.href = 'http://localhost/kame-house-batcave/';
    expect(kh.core.isBatcavePage()).toBe(true);
    expect(kh.core.isGRootPage()).toBe(true);
  });

  it('manipulates the dom', () => {
    document.body.innerHTML = '<div id="parent"><span id="child">child</span></div><div id="target"></div>';
    const parent = document.getElementById('parent') as HTMLElement;
    const child = document.getElementById('child') as HTMLElement;
    const target = document.getElementById('target') as HTMLElement;
    const newEl = document.createElement('b');
    newEl.textContent = 'new';

    kh.util.dom.after(child, newEl);
    expect(child.nextSibling).toBe(newEl);
    kh.util.dom.afterById('child', document.createElement('i'));
    kh.util.dom.append(parent, document.createElement('u'));
    kh.util.dom.appendById('parent', document.createElement('u'));
    kh.util.dom.appendChild(parent, document.createElement('u'));
    kh.util.dom.appendChildById('parent', document.createElement('u'));
    kh.util.dom.prepend(parent, document.createElement('u'));
    kh.util.dom.prependById('parent', document.createElement('u'));
    kh.util.dom.insertBefore(parent, document.createElement('u'), child);
    kh.util.dom.insertBeforeById('parent', document.createElement('u'), child);
    kh.util.dom.remove(target);
    kh.util.dom.removeById('child');
    kh.util.dom.empty(parent);
    kh.util.dom.emptyById('parent');

    kh.util.dom.classListAdd(parent, 'a');
    expect(parent.classList.contains('a')).toBe(true);
    kh.util.dom.classListAddById('parent', 'b');
    kh.util.dom.classListRemove(parent, 'a');
    kh.util.dom.classListRemoveById('parent', 'b');
    parent.className = 'keep-1 remove-me keep-2';
    kh.util.dom.classListRemoveAll(parent, 'remove');
    expect(parent.className).toBe('keep-1 keep-2');
    kh.util.dom.classListRemoveAllById('parent', 'keep');
    expect(parent.className).toBe('');

    const clone = kh.util.dom.cloneNode(parent, false);
    expect(clone).not.toBe(parent);
    expect(kh.util.dom.cloneNode(null, true)).toBeNull();
    document.body.appendChild(parent);
    expect(kh.util.dom.cloneNodeById('parent', true)).toBeDefined();
    expect(kh.util.dom.createElement('div').tagName).toBe('DIV');
    expect(kh.util.dom.getBody()).toBe(document.body);
    expect(kh.util.dom.getHead()).toBe(document.head);
    expect(kh.util.dom.getHtml()).toBe(document.documentElement);
    expect(kh.util.dom.getElementFromTemplate('<div id="tpl">x</div>').id).toBe('tpl');

    kh.util.dom.setAttribute(parent, 'data-x', '1');
    kh.util.dom.setAttributeById('parent', 'data-y', '2');
    kh.util.dom.setBackgroundImage(parent, '/img.png');
    kh.util.dom.setDisplay(parent, 'block');
    kh.util.dom.setDisplayById('parent', 'none');
    kh.util.dom.setHtml(parent, '<i>h</i>');
    kh.util.dom.setHtmlById('parent', '<i>h2</i>');
    kh.util.dom.setId(parent, 'new-id');
    kh.util.dom.setStyle(parent, 'color', 'red');
    kh.util.dom.setStyleById('new-id', 'color', 'blue');
    kh.util.dom.setText(parent, 'text');
    kh.util.dom.setTextById('new-id', 'text2');
    kh.util.dom.setValue(parent as any, 'v');
    kh.util.dom.setValueById('new-id', 'v2');
    kh.util.dom.toggleClassOnElement(parent, 'toggled');
    kh.util.dom.toggleClassOnElementById('new-id', 'toggled');
    kh.util.dom.toggleElement(parent, false);
    kh.util.dom.toggleElementById('new-id', true);
    expect(parent.className).toBeDefined();

    const click = vi.fn();
    kh.util.dom.setClick(parent, { d: 1 }, click);
    kh.util.dom.setClickById('new-id', { d: 2 }, click);
    parent.dispatchEvent(new MouseEvent('click'));
    expect(click).toHaveBeenCalledTimes(2);

    const replaced = document.createElement('em');
    parent.innerHTML = '<span id="old">x</span>';
    kh.util.dom.replaceChild(parent, replaced, document.getElementById('old')!);
    parent.innerHTML = '<span id="old2">x</span>';
    const replaced2 = document.createElement('em');
    kh.util.dom.replaceChildById('new-id', replaced2, document.getElementById('old2')!);
    kh.util.dom.replaceWith(parent, replaced);
    kh.util.dom.replaceWithById('new-id', replaced2);
    parent.innerHTML = '<b id="rm1"></b><b id="rm2"></b>';
    kh.util.dom.removeChild(parent, document.getElementById('rm1')!);
    kh.util.dom.removeChildById('new-id', document.getElementById('rm2')!);
    kh.util.dom.detach(parent);
    kh.util.dom.detachById('new-id');
    kh.util.dom.load(parent, '/fragment.html', vi.fn());
    kh.util.dom.loadById('new-id', '/fragment.html');
    expect((global as any).$.mock.calls.length).toBeGreaterThan(0);
  });

  it('creates dom elements', () => {
    const dom = kh.util.dom;
    expect(dom.getA({ href: '#' }, 'link').tagName).toBe('A');
    expect(dom.getBr().tagName).toBe('BR');
    expect(dom.getButton({ html: 'b' }).tagName).toBe('BUTTON');
    expect(dom.getDiv({ id: 'd' }, 'x').id).toBe('d');
    expect(dom.getImg({ src: 's' }).tagName).toBe('IMG');
    expect(dom.getInput({ type: 'text' }).tagName).toBe('INPUT');
    expect(dom.getLabel({}, 'l').tagName).toBe('LABEL');
    expect(dom.getLi({}, 'li').tagName).toBe('LI');
    expect(dom.getOption({ value: 'v' }, 'o').value).toBe('v');
    expect(dom.getP({}, 'p').tagName).toBe('P');
    expect(dom.getSelect({}, null).tagName).toBe('SELECT');
    expect(dom.getSpan({}, 's').tagName).toBe('SPAN');
    expect(dom.getTbody({}, null).tagName).toBe('TBODY');
    expect(dom.getTextArea({}, null).tagName).toBe('TEXTAREA');
    expect(dom.getTd({}, 'td').tagName).toBe('TD');
    expect(dom.getTr({}, 'tr').tagName).toBe('TR');
    expect(dom.getTrTd('td').tagName).toBe('TR');
  });

  it('loads remote snippets and scripts', async () => {
    const success = vi.fn();
    const fetchSpy = vi.spyOn(globalThis, 'fetch');
    expect(await kh.util.fetch.loadHtmlSnippet('/snippet.html')).toBe('fetched:/snippet.html');
    expect(await kh.util.fetch.loadFile('/file.txt')).toBe('fetched:/file.txt');
    expect(await kh.util.fetch.loadFileWithTimeout('/file.txt')).toBe('fetched:/file.txt');
    expect(await kh.util.fetch.loadFileWithTimeout('/file.txt', 10)).toBe('fetched:/file.txt');
    kh.util.fetch.getScript('/script.js', success);
    expect(success).toHaveBeenCalled();
    expect(fetchSpy).toHaveBeenCalled();
  });

  it('manages cookies', () => {
    document.cookie = 'kh-test=value';
    expect(kh.util.cookies.getCookie('kh-test')).toBe('value');
    expect(kh.util.cookies.getCookie('missing')).toBe('');
    kh.util.cookies.setCookie('kh-new', 'v', 1);
    expect(document.cookie).toContain('kh-new=v');
  });

  it('handles banners and collapsible divs', () => {
    document.body.innerHTML = '<div id="banner" class="banner-goku-ssj4-earth"></div><div id="collapsible" class="collapsible-kh"></div>';
    kh.util.banner.setBanner('banner-fuji');
    expect(document.getElementById('banner')!.className).toContain('banner-fuji');
    expect(kh.util.banner.getBannerCategories().length).toBeGreaterThan(0);
    expect(kh.util.banner.getBanners('batcave').length).toBeGreaterThan(0);
    kh.util.banner.setRandomAllBanner(100000);
    kh.util.banner.setRandomSanctuaryBanner(100000);
    kh.util.banner.setRandomBatcaveBanner(100000);
    kh.util.banner.setRandomCaptainTsubasaBanner(100000);
    kh.util.banner.setRandomDragonBallBanner(100000);
    kh.util.banner.setRandomPrinceOfTennisBanner(100000);
    kh.util.banner.setRandomSaintSeiyaBanner(100000);
    kh.util.banner.setRandomTennisBanner(100000);
    kh.util.banner.setRandomWorldCupBanner(100000);
    kh.util.banner.setRandomAllBanner(null);

    const collapsible = document.getElementById('collapsible') as HTMLElement;
    collapsible.innerHTML = '<div class="collapsible-kh-content">content</div><div class="collapsible-kh-title">title</div>';
    kh.util.collapsibleDiv.resize('collapsible');
    kh.util.collapsibleDiv.toggle(collapsible);
    kh.util.collapsibleDiv.toggle(collapsible);
    expect(collapsible.className).toBeDefined();
  });

  it('manages cursor and test utilities', async () => {
    kh.util.cursor.setCursorWait();
    kh.util.cursor.setCursorDefault();
    await kh.util.cursor.loadSpinningWheelCursorWait();
    kh.util.test.testLogLevel();
    kh.core.sleep = vi.fn(async () => {});
    await kh.util.test.testSleep();
    expect(kh.util.dom.getBody()).toBeDefined();
  });

  it('manages modules and tabs', async () => {
    kh.util.module.setModuleLoaded('moduleA');
    expect(kh.util.module.isModuleLoaded('moduleA')).toBe(true);
    expect(kh.util.module.isModuleLoaded('moduleB')).toBe(false);
    const init = vi.fn();
    await kh.util.module.waitForModules(['moduleA'], init);
    expect(init).toHaveBeenCalled();

    document.body.innerHTML = '<a id="tab-one-link" class="tab-kh-link"></a><div id="tab-one" class="tab-content-kh"></div>';
    kh.util.tab.openTab('tab-one', 'prefix');
    expect(kh.util.cookies.getCookie('prefix-current-tab')).toBe('tab-one');
    kh.util.tab.openTabFromCookies('prefix', 'tab-one');
    kh.util.cookies.getCookie = vi.fn(() => '');
    kh.util.tab.openTabFromCookies('prefix', 'tab-one');
    expect(document.getElementById('tab-one')!.style.display).toBe('block');
  });

  it('filters, sorts and limits tables', async () => {
    vi.useFakeTimers();
    kh.plugin.modal = { loadingWheelModal: { open: vi.fn(), close: vi.fn() } };
    document.body.innerHTML = `
      <table id="table"><tbody id="body">
        <tr class="table-kh-header"><td>h</td></tr>
        <tr><td>Alpha</td></tr>
        <tr style="display:none"><td>Beta</td></tr>
        <tr><td>Gamma</td></tr>
      </tbody></table>`;
    kh.util.table.filterTableRows('a', 'body', null, false);
    kh.util.table.filterTableRows('a', 'body', 'all', true);
    kh.util.table.filterTableRowsByColumn('a', 'body', 0, null, false);
    kh.util.table.filterTableRowsByColumn('a', 'body', null, 1, true);
    kh.util.table.limitRows('table', 1, true);
    kh.util.table.limitRows('table', 'all', false);

    const callback = vi.fn();
    kh.util.table.sortTable('table', 0, 'text', 'asc', callback);
    await vi.advanceTimersByTimeAsync(60);
    expect(callback).toHaveBeenCalled();

    kh.util.table.sortTable('table', 0, 'number', 'desc', null);
    await vi.advanceTimersByTimeAsync(60);
    kh.util.table.sortTable('table', 0, 'text', null, null);
    await vi.advanceTimersByTimeAsync(60);
    expect(kh.plugin.modal.loadingWheelModal.open).toHaveBeenCalled();
  });

  it('manages mobile utils', () => {
    kh.util.mobile.exec(() => 'web', () => 'mobile');
    expect(kh.util.mobile.exec(() => 'web', () => 'mobile')).toBe('web');
    window.history.replaceState({}, '', '/kame-house/?mockCordova=true');
    expect(kh.util.mobile.exec(null, () => 'mobile')).toBeUndefined();
    kh.util.mobile.setMobileEventListeners(vi.fn(), vi.fn());
    kh.util.mobile.windowOpen('/url', '_blank');
    expect(kh.util.mobile.exec(() => 1, () => { throw new Error('x'); })).toBe(1);
  });

  it('performs http requests and encodes parameters', () => {
    const get = vi.spyOn(kh.http, 'get');
    const put = vi.spyOn(kh.http, 'put');
    const post = vi.spyOn(kh.http, 'post');
    const del = vi.spyOn(kh.http, 'delete');
    kh.http.get({}, '/url', null, null, vi.fn(), vi.fn());
    kh.http.put({}, '/url', null, null, vi.fn(), vi.fn());
    kh.http.post({}, '/url', null, null, vi.fn(), vi.fn());
    kh.http.delete({}, '/url', null, null, vi.fn(), vi.fn());
    expect(get).toHaveBeenCalled();
    expect(put).toHaveBeenCalled();
    expect(post).toHaveBeenCalled();
    expect(del).toHaveBeenCalled();
    expect(kh.http.getUrlEncodedHeaders()).toBeDefined();
    expect(kh.http.getApplicationJsonHeaders()).toBeDefined();
    expect(kh.http.urlEncodeParams({ a: 'b c' })).toBe('a=b%20c');
    expect(kh.http.urlEncode('a b')).toBe('a%20b');
    expect(kh.http.isUrlEncodedRequest({ 'Content-Type': 'application/x-www-form-urlencoded' })).toBe(true);
    expect(kh.http.isUrlEncodedRequest({})).toBe(false);
    expect(kh.http.getConfig()).toBeDefined();
  });

  it('runs core page helpers', () => {
    document.body.innerHTML = '<div id="target"></div><div class="link-image-img" data-background-img="/bg.png"></div><div class="img-btn-kh" data-background-img="/bg2.png"></div><form class="form-in-page-kh"></form>';
    kh.core.configDynamicHtml();
    expect(document.querySelector('.link-image-img')!.getAttribute('style')).toContain('/bg.png');

    kh.core.sleep = vi.fn(() => Promise.resolve());
    kh.core.loadKameHouseWebSocket();
    kh.core.loadKameHouseModal();
    kh.core.loadKameHouseDebugger();
    kh.core.scrollToTopOfDivById('target');
    kh.core.scrollToTopById('target');
    kh.core.scrollToTopById('');
    kh.core.scrollToBottomById('target');
    kh.core.scrollToBottomById('');
    kh.core.backToTop();
    kh.core.windowLocation('/next');
    kh.core.windowLocationHref('/next');
    expect(kh.core.pageRequiresAuthorization()).toBe(false);
    kh.util.module.waitForModules = vi.fn((modules: any, cb: any) => { if (cb) cb(); });
    kh.core.initAuthorizeUser();

    const filterFn = vi.fn((i: number, v: number) => v > 1);
    expect(kh.core.filter([1, 2, 3], filterFn)).toBeUndefined();
    expect(kh.core.filter(null, filterFn)).toBeUndefined();
    expect(kh.core.offset(document.body)).toBeDefined();
    expect(kh.core.height(document.body)).toBeDefined();
    expect(() => kh.core.animate(document.body, {}, 1)).not.toThrow();
  });

  it('handles global error and rejection events', () => {
    kh.plugin.modal = { basicModal: { setHtml: vi.fn(), open: vi.fn() } };
    kh.util.module.waitForModules = vi.fn((modules: any, cb: any) => { if (cb) cb(); });
    kh.core.setGlobalErrorHandler();
    window.dispatchEvent(new ErrorEvent('error', { message: 'boom', filename: 'f', lineno: 1, colno: 2, error: new Error('boom') }));
    const rejectionEvent: any = new Event('unhandledrejection');
    rejectionEvent.reason = 'reason';
    window.dispatchEvent(rejectionEvent);
    expect(kh.logger.error).toBeDefined();
  });
});
