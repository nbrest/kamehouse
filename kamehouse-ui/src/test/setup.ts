/// <reference types="vitest/globals" />
import { vi } from 'vitest';

(global as any).kameHouse = {
  logger: { warn: vi.fn(), error: vi.fn(), info: vi.fn(), debug: vi.fn(), trace: vi.fn(), logHttpResponse: vi.fn(), logApiError: vi.fn(), getRedText: (t:any)=>t, isLevelTrace:()=>false },
  util: { 
    mobile: { exec: (d:any,m:any)=>d&&d(), isMobile:()=>false, isIphone:()=>false, isAndroid:()=>false },
    module: { isModuleLoaded: ()=>false, waitForModules: ()=>{} },
    dom: { empty: vi.fn(), append: vi.fn(), getBr: ()=>({}), getSpan: ()=>({}), getImg: ()=>({}), getP: ()=>({}), getButton: ()=>({}), getSelect: ()=>({}), getDiv: ()=>({}), getTable: ()=>({}), getTr: ()=>({}), getTd: ()=>({}), getInput: ()=>({}), getTextArea: ()=>({}), getLabel: ()=>({}), getUl: ()=>({}), getLi: ()=>({}), getOption: ()=>({}), getLink: ()=>({}), getStrong: ()=>({}), getH1: ()=>({}), getH2: ()=>({}), getH3: ()=>({}), setId: vi.fn(), setValue: vi.fn(), setInnerHtml: vi.fn(), setClass: vi.fn(), setText: vi.fn(), classListAdd: vi.fn(), classListRemove: vi.fn(), classListRemoveAll: vi.fn(), classListContains: vi.fn(), getElement: ()=>({}), getElementsByClassName: ()=>[], getElementsByTagName: ()=>[], cloneNode: (n:any)=>n, replaceChild: vi.fn(), appendToElement: vi.fn(), prepend: vi.fn(), getParent: ()=>({}) },
    collapsibleDiv: { resize: vi.fn(), createCollapsibleDiv: vi.fn() },
    banner: { setRandomAllBanner: vi.fn(), setRandomSanctuaryBanner: vi.fn(), setRandomBatcaveBanner: vi.fn(), setRandomDragonBallBanner: vi.fn(), setRandomCaptainTsubasaBanner: vi.fn(), setRandomPrinceOfTennisBanner: vi.fn(), setRandomSaintSeiyaBanner: vi.fn(), setRandomTennisBanner: vi.fn(), setRandomWorldCupBanner: vi.fn() },
    time: { getDateTime: ()=>"", getTimeStamp: ()=>"", getCurrentDateString: ()=>"", getDays: ()=>[], getMonthNames: ()=>[], getWeekDayNames: ()=>[] },
    string: { replaceAll: (s:any)=>s, isEmpty: (v:any)=>!v, startsWith: ()=>false, endsWith: ()=>false },
    cookies: { getCookieValue: ()=>"", setCookie: ()=>{}, deleteCookie: ()=>{} },
    table: { sortTable: vi.fn(), getTableBodyRows: ()=>[], getCellValue: ()=>"", emptyTable: vi.fn() },
    tab: { createTabs: vi.fn() },
    file: { downloadTextFile: vi.fn() },
    session: { isUserLoggedIn: ()=>false },
    websocket: { getWebSocket: ()=>({ isConnected:()=>false, connect:vi.fn(), disconnect:vi.fn(), sendMessage:vi.fn(), poll:vi.fn() }) },
    fetch: { doGet: vi.fn(), doPost: vi.fn(), doPut: vi.fn(), doDelete: vi.fn() }
  },
  core: { 
    isEmpty: (val: any) => val === undefined || val === null || val === "", 
    isFunction: (f: any) => typeof f === 'function',
    initAuthorizeUser: ()=>{},
    configDynamicHtml: ()=>{},
    loadSession: ()=>{},
    getCookieValue: ()=>"",
    setCookie: ()=>{},
    deleteCookie: ()=>{}
  },
  ready: (cb: any) => { try { if(typeof cb==='function') cb(); } catch(e) {} },
  addPlugin: vi.fn(),
  extension: {},
  plugin: {}
};
(global as any).$ = vi.fn(() => ({ ready: vi.fn((cb:any)=>cb&&cb()), on: vi.fn(), click: vi.fn(), each: vi.fn() }));
(global as any).jQuery = (global as any).$;
(global as any).angular = { module: vi.fn(()=>({ controller: vi.fn(()=>({})), service: vi.fn(()=>({})), factory: vi.fn(()=>({})) })), element: vi.fn() };
(global as any).SockJS = vi.fn();
(global as any).Stomp = { over: vi.fn(()=>({ connect: vi.fn(), disconnect: vi.fn(), send: vi.fn(), subscribe: vi.fn() })) };
(global as any).cordova = { plugins: {} };
