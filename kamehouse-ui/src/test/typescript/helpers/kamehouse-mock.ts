import { vi } from 'vitest';

export type AnyObj = any;

function isPlainObject(value: any): boolean {
  return value !== null && typeof value === 'object' && !Array.isArray(value);
}

function merge(target: any, source: any): any {
  if (!isPlainObject(source)) {
    return target;
  }
  Object.keys(source).forEach((key) => {
    const sourceValue = source[key];
    if (isPlainObject(sourceValue) && isPlainObject(target[key])) {
      merge(target[key], sourceValue);
    } else {
      target[key] = sourceValue;
    }
  });
  return target;
}

/**
 * Creates a flexible kameHouse mock. Extensions and plugins are registered in
 * real maps and their load() methods are invoked, mirroring the real framework.
 */
export function createKameHouseMock(overrides: AnyObj = {}): AnyObj {
  const extension: AnyObj = {};
  const plugin: AnyObj = {};

  const mock: AnyObj = {
    logger: {
      info: vi.fn(),
      warn: vi.fn(),
      error: vi.fn(),
      debug: vi.fn(),
      trace: vi.fn(),
      logHttpResponse: vi.fn(),
      logApiError: vi.fn(),
      getRedText: (t: any) => t,
      isLevelTrace: () => false,
      isLevelDebug: () => false
    },
    core: {
      isEmpty: (val: any) => val === undefined || val === null || val === '',
      isFunction: (f: any) => typeof f === 'function',
      getArrayKameHouseData: () => [],
      initAuthorizeUser: vi.fn(),
      configDynamicHtml: vi.fn(),
      loadSession: vi.fn(),
      backToTop: vi.fn(),
      sleep: vi.fn(async () => {}),
      getArrayKameHouseDataString: () => ''
    },
    util: {
      mobile: {
        exec: vi.fn((cb: any) => { try { if (typeof cb === 'function') cb(); } catch (e) {} }),
        windowLocation: vi.fn(),
        isMobile: () => false,
        isIphone: () => false,
        isAndroid: () => false
      },
      module: {
        isModuleLoaded: () => false,
        setModuleLoaded: vi.fn(),
        waitForModules: vi.fn((modules: any, cb: any) => { try { if (typeof cb === 'function') cb(); } catch (e) {} })
      },
      dom: {
        empty: vi.fn(),
        append: vi.fn(),
        appendById: vi.fn(),
        appendToElement: vi.fn(),
        prepend: vi.fn(),
        getBr: vi.fn(() => ({})),
        getSpan: vi.fn(() => ({})),
        getImg: vi.fn(() => ({})),
        getP: vi.fn(() => ({})),
        getButton: vi.fn(() => ({})),
        getSelect: vi.fn(() => ({})),
        getDiv: vi.fn(() => ({})),
        getTable: vi.fn(() => ({})),
        getTr: vi.fn(() => ({})),
        getTd: vi.fn(() => ({})),
        getInput: vi.fn(() => ({})),
        getTextArea: vi.fn(() => ({})),
        getLabel: vi.fn(() => ({})),
        getUl: vi.fn(() => ({})),
        getLi: vi.fn(() => ({})),
        getOption: vi.fn(() => ({})),
        getLink: vi.fn(() => ({})),
        getStrong: vi.fn(() => ({})),
        getH1: vi.fn(() => ({})),
        getH2: vi.fn(() => ({})),
        getH3: vi.fn(() => ({})),
        getHead: vi.fn(() => ({})),
        getBody: vi.fn(() => ({})),
        setId: vi.fn(),
        setValue: vi.fn(),
        setHtmlById: vi.fn(),
        setValueById: vi.fn(),
        setInnerHtml: vi.fn(),
        setClass: vi.fn(),
        setText: vi.fn(),
        setDisplay: vi.fn(),
        setAttribute: vi.fn(),
        setClick: vi.fn((element: any, eventName: any, handler: any) => {
          try { if (typeof handler === 'function') handler({ preventDefault: vi.fn() }); } catch (e) {}
        }),
        setClickById: vi.fn((id: any, eventName: any, handler: any) => {
          try { if (typeof handler === 'function') handler({ preventDefault: vi.fn() }); } catch (e) {}
        }),
        getElementFromTemplate: vi.fn((template: any) => {
          const templateChildren = { length: 0, item: vi.fn() };
          return {
            style: {},
            querySelector: vi.fn(() => ({})),
            getElementsByClassName: vi.fn(() => templateChildren)
          };
        }),
        getTrTd: vi.fn(() => ({})),
        getTd: vi.fn(() => ({})),
        getTr: vi.fn(() => ({})),
        classListAdd: vi.fn(),
        classListAddById: vi.fn(),
        toggleClass: vi.fn(),
        classListRemove: vi.fn(),
        classListRemoveById: vi.fn(),
        classListRemoveAll: vi.fn(),
        classListContains: vi.fn(),
        getElement: () => ({}),
        getElementsByClassName: () => [],
        getElementsByTagName: () => [],
        cloneNode: (n: any) => n,
        removeById: vi.fn(),
        replaceChild: vi.fn(),
        getParent: () => ({}),
        loadById: vi.fn((id: any, url: any, cb: any) => { try { if (typeof cb === 'function') cb(); } catch (e) {} }),
        getObjectById: vi.fn(() => ({})),
        getValueById: vi.fn(() => ''),
        getValue: vi.fn(() => ''),
        isVisible: vi.fn(() => true)
      },
      collapsibleDiv: { resize: vi.fn(), createCollapsibleDiv: vi.fn() },
      banner: {
        setRandomAllBanner: vi.fn(),
        setRandomSanctuaryBanner: vi.fn(),
        setRandomBatcaveBanner: vi.fn(),
        setRandomDragonBallBanner: vi.fn(),
        setRandomCaptainTsubasaBanner: vi.fn(),
        setRandomPrinceOfTennisBanner: vi.fn(),
        setRandomSaintSeiyaBanner: vi.fn(),
        setRandomTennisBanner: vi.fn(),
        setRandomWorldCupBanner: vi.fn()
      },
      time: {
        getDateTime: () => '',
        getTimeStamp: () => '',
        getTimestamp: () => '',
        getCurrentDateString: () => '',
        getDays: () => [],
        getMonthNames: () => [],
        getWeekDayNames: () => []
      },
      string: {
        replaceAll: (s: any) => s,
        isEmpty: (v: any) => !v,
        isNotEmpty: (v: any) => !!v,
        startsWith: () => false,
        endsWith: () => false
      },
      cookies: {
        getCookie: vi.fn(() => ''),
        getCookieValue: vi.fn(() => ''),
        setCookie: vi.fn(),
        deleteCookie: vi.fn()
      },
      table: { sortTable: vi.fn(), getTableBodyRows: () => [], getCellValue: () => '', emptyTable: vi.fn() },
      tab: { createTabs: vi.fn() },
      file: { downloadTextFile: vi.fn() },
      session: { isUserLoggedIn: () => false },
      cursor: { loadSpinningWheelCursorWait: vi.fn(), removeSpinningWheelCursorWait: vi.fn() },
      websocket: { getWebSocket: () => ({ isConnected: () => false, connect: vi.fn(), disconnect: vi.fn(), sendMessage: vi.fn(), poll: vi.fn() }) },
      fetch: {
        doGet: vi.fn(),
        doPost: vi.fn(),
        doPut: vi.fn(),
        doDelete: vi.fn(),
        loadHtmlSnippet: vi.fn(async () => '<div></div>'),
        loadFile: vi.fn(async () => ''),
        getScript: vi.fn()
      }
    },
    http: {
      getConfig: vi.fn(() => ({})),
      getUrlEncodedHeaders: vi.fn(() => ({ 'Content-Type': 'application/x-www-form-urlencoded' })),
      getApplicationJsonHeaders: vi.fn(() => ({ 'Content-Type': 'application/json' })),
      doGet: vi.fn(),
      doPost: vi.fn(),
      doPut: vi.fn(),
      doDelete: vi.fn()
    },
    json: {
      stringify: (value: any) => JSON.stringify(value),
      parse: (value: any) => JSON.parse(value)
    },
    extension,
    plugin,
    addExtension: vi.fn((name: string, ext: any) => {
      extension[name] = ext;
      if (ext && typeof ext.load === 'function') {
        ext.load();
      }
    }),
    addPlugin: vi.fn((name: string, ext: any) => {
      plugin[name] = ext;
      if (ext && typeof ext.load === 'function') {
        ext.load();
      }
    }),
    ready: vi.fn((cb: any) => { try { if (typeof cb === 'function') cb(); } catch (e) {} }),
    session: {},
    cordova: null
  };

  return merge(mock, overrides);
}

/**
 * Installs the kameHouse mock plus common browser globals.
 */
export function installKameHouseMock(overrides: AnyObj = {}, options: AnyObj = {}): AnyObj {
  const mock = createKameHouseMock(overrides);
  (global as any).kameHouse = mock;

  const jqueryObj: AnyObj = options.jqueryObj || createJqueryObject();
  const jquery = vi.fn((selector: any) => {
    if (typeof selector === 'function') {
      try { selector(); } catch (e) {}
    }
    return jqueryObj;
  });
  (global as any).$ = jquery;
  (global as any).jQuery = jquery;

  if (!(global as any).window || (global as any).window === global) {
    (global as any).window = global;
  }
  if (options.window) {
    (global as any).window = options.window;
  }
  if (options.document) {
    (global as any).document = options.document;
  }
  return mock;
}

/**
 * Creates a chainable jQuery-like object with common methods.
 */
export function createJqueryObject(): AnyObj {
  const obj: AnyObj = {};
  obj.ready = vi.fn((cb: any) => { try { if (typeof cb === 'function') cb(); } catch (e) {} return obj; });
  obj.on = vi.fn(() => obj);
  obj.off = vi.fn(() => obj);
  obj.click = vi.fn((fn: any) => { try { if (typeof fn === 'function') fn(); } catch (e) {} return obj; });
  obj.each = vi.fn((fn: any) => {
    try { if (typeof fn === 'function') { for (let i = 0; i < 3; i++) fn.call(obj, i, obj); } } catch (e) {}
    return obj;
  });
  obj.find = vi.fn(() => obj);
  obj.parent = vi.fn(() => obj);
  obj.children = vi.fn(() => obj);
  obj.attr = vi.fn(() => '');
  obj.prop = vi.fn(() => obj);
  obj.html = vi.fn(() => obj);
  obj.text = vi.fn(() => '');
  obj.val = vi.fn(() => '');
  obj.css = vi.fn(() => obj);
  obj.show = vi.fn(() => obj);
  obj.hide = vi.fn(() => obj);
  obj.append = vi.fn(() => obj);
  obj.prepend = vi.fn(() => obj);
  obj.remove = vi.fn(() => obj);
  obj.addClass = vi.fn(() => obj);
  obj.removeClass = vi.fn(() => obj);
  obj.toggleClass = vi.fn(() => obj);
  obj.hasClass = vi.fn(() => false);
  obj.data = vi.fn(() => '');
  obj.trigger = vi.fn(() => obj);
  obj.scrollTop = vi.fn(() => 0);
  obj.length = 1;
  obj[0] = obj;
  return obj;
}

/**
 * Creates a mock document with configurable elements.
 */
export function createDocumentMock(elements: AnyObj = {}): AnyObj {
  const defaultElement: AnyObj = {
    style: {},
    innerHTML: '',
    innerText: '',
    textContent: '',
    value: '',
    checked: false,
    classList: { add: vi.fn(), remove: vi.fn(), contains: vi.fn(() => false), toggle: vi.fn() },
    appendChild: vi.fn(),
    removeChild: vi.fn(),
    insertBefore: vi.fn(),
    setAttribute: vi.fn(),
    getAttribute: vi.fn(() => null),
    removeAttribute: vi.fn(),
    addEventListener: vi.fn(),
    removeEventListener: vi.fn(),
    querySelector: vi.fn(() => defaultElement),
    querySelectorAll: vi.fn(() => []),
    contentWindow: { postMessage: vi.fn() },
    children: [],
    parentNode: null,
    focus: vi.fn(),
    click: vi.fn(),
    remove: vi.fn(),
    load: vi.fn((path: any, cb: any) => { try { if (typeof cb === 'function') cb(); } catch (e) {} })
  };
  return {
    readyState: 'complete',
    addEventListener: vi.fn(),
    removeEventListener: vi.fn(),
    querySelector: vi.fn(() => defaultElement),
    querySelectorAll: vi.fn(() => []),
    getElementById: vi.fn((id: string) => elements[id] || defaultElement),
    getElementsByClassName: vi.fn(() => []),
    getElementsByTagName: vi.fn(() => []),
    getElementsByName: vi.fn(() => []),
    createElement: vi.fn(() => ({ ...defaultElement })),
    createTextNode: vi.fn(() => ({})),
    body: defaultElement,
    head: defaultElement,
    documentElement: defaultElement,
    cookie: '',
    defaultElement
  };
}
