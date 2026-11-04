/// <reference types="vitest/globals" />

import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { installKameHouseMock } from '../../helpers/kamehouse-mock';

let mock: any;
let player: any;
let getHttp: any;
let postHttp: any;
let deleteHttp: any;
let buttons: any[];
let cookies: any;
let mobileListeners: any;
let wsInstances: any[];

function setupDom() {
  document.body.innerHTML = `
    <div id="banner"></div>
    <select id="aspect-ratio-dropdown">
      <option value="" selected>Default</option>
      <option value="16:9">16:9</option>
      <option value="4:3">4:3</option>
    </select>
    <select id="subtitle-track-dropdown"></select>
    <select id="audio-track-dropdown"></select>
    <div id="media-title"></div>
    <div id="subtitle-delay-value"></div>
    <div id="current-time"></div>
    <div id="total-time"></div>
    <input id="time-slider" />
    <div id="current-volume"></div>
    <input id="volume-slider" />
    <table id="playlist-table"><tbody id="playlist-table-body"></tbody></table>
    <table id="playlist-browser-table"></table>
    <div id="playlist-hidden-section"></div>
    <div id="playlist-controls"></div>
    <div id="playlist-table-wrapper"></div>
    <div id="playlist-size-wrapper"><span id="playlist-size-val"></span></div>
    <span id="playlist-current-position-val"></span>
    <input id="vlc-player-show-playlist-checkbox" type="checkbox" checked />
    <input id="vlc-player-show-playlist-browser-checkbox" type="checkbox" checked />
    <input id="vlc-player-status-sync-checkbox" type="checkbox" checked />
    <input id="vlc-player-get-full-report-checkbox" type="checkbox" checked />
    <input id="vlc-player-update-stats-checkbox" type="checkbox" checked />
    <input id="playlist-filter-input" value="" />
    <div id="tab-playing" class="vlc-player-tab-content"></div>
    <div id="tab-playlist" class="vlc-player-tab-content"></div>
    <div id="tab-playlist-browser" class="vlc-player-tab-content"></div>
    <a id="tab-playing-link" class="vlc-player-tab-link"></a>
    <a id="tab-playlist-link" class="vlc-player-tab-link"></a>
    <a id="tab-playlist-browser-link" class="vlc-player-tab-link"></a>
    <button id="toggle-playlist-filenames-btn"></button>
  `;
}

function install() {
  setupDom();
  buttons = [];
  cookies = {};
  mobileListeners = null;
  wsInstances = [];
  getHttp = vi.fn();
  postHttp = vi.fn();
  deleteHttp = vi.fn();
  (globalThis as any).KameHouseWebSocket = class {
    statusUrl = vi.fn();
    pollUrl = vi.fn();
    topicUrl = vi.fn();
    onConnect: any = null;
    connect = vi.fn((cb: any) => { this.onConnect = cb; });
    disconnect = vi.fn();
    isConnected = vi.fn(() => true);
    constructor() { wsInstances.push(this); }
  };

  const m = installKameHouseMock({
    logger: {
      getGreenText: (t: any) => t,
      getCyanText: (t: any) => t,
      getYellowText: (t: any) => t
    },
    plugin: {
      debugger: {
        renderCustomDebugger: vi.fn((url: any, cb: any) => { if (cb) cb(); }),
        http: { get: getHttp, post: postHttp, delete: deleteHttp }
      },
      modal: {
        loadingWheelModal: { open: vi.fn(), close: vi.fn() },
        basicModal: {
          setHtml: vi.fn(),
          appendHtml: vi.fn(),
          open: vi.fn(),
          close: vi.fn(),
          openApiError: vi.fn()
        }
      },
      kameHouseCommandManager: {
        renderKameHouseCommandResult: vi.fn(),
        renderErrorExecutingCommand: vi.fn()
      }
    }
  });

  m.addExtension = vi.fn((name: string, ext: any) => { m.extension[name] = ext; });
  m.ready = vi.fn((cb: any) => { if (cb) cb(); });
  m.core.loadKameHouseWebSocket = vi.fn();
  m.core.sleep = vi.fn(() => new Promise(() => {}));
  m.core.scrollTop = vi.fn();
  m.core.offset = vi.fn(() => ({ top: 10 }));
  m.util.cookies.getCookie = vi.fn((id: string) => cookies[id] || '');
  m.util.cookies.setCookie = vi.fn((id: string, value: string) => { cookies[id] = value; });
  m.util.mobile.exec = vi.fn((web: any, mobile: any) => { if (typeof web === 'function') return web(); });
  m.util.mobile.setMobileEventListeners = vi.fn((onPause: any, onResume: any) => { mobileListeners = { onPause, onResume }; });
  m.util.module.waitForModules = vi.fn((modules: any, cb: any) => { if (typeof cb === 'function') return cb(); });
  m.util.file.getShortFilename = vi.fn((path: string) => path.split('/').pop());
  m.util.table.filterTableRows = vi.fn();
  m.util.time.convertSecondsToHsMsSs = vi.fn((value: any) => 'T' + value);
  m.util.cursor.setCursorWait = vi.fn();
  m.util.cursor.setCursorDefault = vi.fn();

  const dom = m.util.dom;
  const byId = (id: string) => document.getElementById(id);
  dom.empty = vi.fn((element: any) => { if (element) element.innerHTML = ''; });
  dom.append = vi.fn((parent: any, child: any) => { if (parent && child && parent.appendChild) parent.appendChild(child); });
  dom.prepend = vi.fn((parent: any, child: any) => { if (parent && child && parent.insertBefore) parent.insertBefore(child, parent.firstChild); });
  dom.detach = vi.fn((element: any) => { if (element && element.parentNode) element.parentNode.removeChild(element); });
  dom.getBr = vi.fn(() => document.createElement('br'));
  dom.getSpan = vi.fn((attr: any, html: any) => { const el = document.createElement('span'); if (html != null) el.innerHTML = String(html); return el; });
  dom.getP = vi.fn((attr: any, html: any) => { const el = document.createElement('p'); if (html != null) el.innerHTML = String(html); return el; });
  dom.getDiv = vi.fn((attr: any, html: any) => { const el = document.createElement('div'); if (attr && attr.id) el.id = attr.id; if (html != null) el.innerHTML = String(html); return el; });
  dom.getImg = vi.fn(() => document.createElement('img'));
  dom.getTbody = vi.fn((attr: any) => { const el = document.createElement('tbody'); if (attr && attr.id) el.id = attr.id; return el; });
  dom.getTr = vi.fn((attr: any, child: any) => { const el = document.createElement('tr'); if (attr && attr.id) el.id = attr.id; if (child) el.appendChild(child); return el; });
  dom.getTd = vi.fn((attr: any, child: any) => { const el = document.createElement('td'); if (child) { if (typeof child === 'string') el.innerHTML = child; else el.appendChild(child); } return el; });
  dom.getTrTd = vi.fn((text: any) => { const tr = document.createElement('tr'); const td = document.createElement('td'); td.textContent = String(text); tr.appendChild(td); return tr; });
  dom.getButton = vi.fn((opts: any) => {
    const el = document.createElement('button');
    if (opts && opts.attr) {
      Object.keys(opts.attr).forEach((key) => {
        if (key === 'class') el.className = opts.attr[key];
        else if (opts.attr[key] != null) el.setAttribute(key, opts.attr[key]);
      });
    }
    if (opts && opts.html != null) el.innerHTML = opts.html;
    (el as any).__opts = opts;
    buttons.push(el);
    return el;
  });
  dom.replaceWithById = vi.fn((id: string, newElement: any) => { const old = byId(id); if (old && old.parentNode) old.parentNode.replaceChild(newElement, old); });
  dom.replaceWith = vi.fn((element: any, newElement: any) => { if (element && element.parentNode) element.parentNode.replaceChild(newElement, element); });
  dom.replaceChild = vi.fn((parent: any, newChild: any, oldChild: any) => { if (parent && newChild && oldChild) parent.replaceChild(newChild, oldChild); });
  dom.setHtmlById = vi.fn((id: string, value: any) => { const el = byId(id); if (el) el.innerHTML = String(value); });
  dom.setValueById = vi.fn((id: string, value: any) => { const el = byId(id) as any; if (el) el.value = value; });
  dom.setAttributeById = vi.fn((id: string, name: string, value: any) => { const el = byId(id); if (el) el.setAttribute(name, value); });
  dom.setDisplay = vi.fn((element: any, value: any) => { if (element) element.style.display = value; });
  dom.setDisplayById = vi.fn((id: string, value: any) => { const el = byId(id); if (el) el.style.display = value; });
  dom.classListAdd = vi.fn((element: any, value: any) => { if (element && element.classList) element.classList.add(value); });
  dom.classListAddById = vi.fn((id: string, value: any) => { const el = byId(id); if (el) el.classList.add(value); });
  dom.classListRemove = vi.fn((element: any, value: any) => { if (element && element.classList) element.classList.remove(value); });
  dom.classListRemoveById = vi.fn((id: string, value: any) => { const el = byId(id); if (el) el.classList.remove(value); });
  dom.classListRemoveAll = vi.fn((element: any, prefix: any) => { if (element && element.classList) { Array.from(element.classList).forEach((c: any) => { if (c.startsWith(prefix)) element.classList.remove(c); }); } });
  dom.classListRemoveAllById = vi.fn((id: string, prefix: any) => { const el = byId(id) as any; if (el) dom.classListRemoveAll(el, prefix); });
  dom.setValue = vi.fn((element: any, value: any) => { if (element) element.value = value; });
  dom.setId = vi.fn((element: any, id: any) => { if (element) element.id = id; });
  dom.setText = vi.fn((element: any, value: any) => { if (element) element.textContent = value; });
  dom.setHtml = vi.fn((element: any, value: any) => { if (element) element.innerHTML = value; });
  dom.setStyle = vi.fn((element: any, name: any, value: any) => { if (element) element.style[name] = value; });
  dom.setBackgroundImage = vi.fn();
  dom.setClick = vi.fn((element: any, data: any, cb: any) => { if (element) element.addEventListener('click', () => cb({}, data)); });
  dom.setClickById = vi.fn((id: string, data: any, cb: any) => { const el = byId(id); if (el) el.addEventListener('click', () => cb({}, data)); });
  return m;
}

async function loadPlayer() {
  await import('@/js/vlc-player/vlc-player');
  player = mock.extension.vlcPlayer;
  return player;
}

beforeEach(async () => {
  vi.resetModules();
  mock = install();
  await loadPlayer();
});

afterEach(() => {
  vi.useRealTimers();
  vi.clearAllTimers();
});

describe('vlc-player', () => {
  it('creates the vlc player extension', () => {
    expect(player).toBeDefined();
    expect(player.getHostname()).toBe('localhost');
    expect(player.getPlaylist()).toBeDefined();
    expect(player.getRestClient()).toBeDefined();
    expect(player.getDebugger()).toBeDefined();
    expect(mock.addExtension).toHaveBeenCalledWith('vlcPlayer', player);
  });

  it('loads the player and manages the state from cookies', () => {
    vi.useFakeTimers();
    cookies['kh-vlc-player-show-playlist-checkbox'] = 'false';
    cookies['kh-vlc-player-show-playlist-browser-checkbox'] = 'false';
    cookies['kh-vlc-player-current-tab'] = 'tab-playlist';

    player.load();
    expect(mock.logger.info).toHaveBeenCalledWith('Started initializing VLC Player', null);
    expect(mock.core.loadKameHouseWebSocket).toHaveBeenCalled();
    expect((document.getElementById('vlc-player-show-playlist-checkbox') as HTMLInputElement).checked).toBe(false);
    expect((document.getElementById('vlc-player-show-playlist-browser-checkbox') as HTMLInputElement).checked).toBe(false);
    expect((document.getElementById('tab-playlist') as HTMLElement).style.display).toBe('block');
    expect(mobileListeners).not.toBeNull();
    expect(mock.util.module.setModuleLoaded).toHaveBeenCalledWith('vlcPlayer');

    vi.advanceTimersByTime(18000);
    expect(mock.plugin.debugger.renderCustomDebugger).toHaveBeenCalled();
    expect(mock.core.loadKameHouseWebSocket).toHaveBeenCalled();
  });

  it('loads the player in the mobile app and starts the lockscreen', () => {
    vi.useFakeTimers();
    mock.extension.mobile = { core: { startZtvLockscreen: vi.fn(), keepAliveZtvLockscreen: vi.fn() } };
    mock.util.mobile.exec = vi.fn((web: any, mobile: any) => { if (typeof mobile === 'function') return mobile(); });

    player.load();
    expect(mock.extension.mobile.core.startZtvLockscreen).toHaveBeenCalled();
    expect(mock.util.module.setModuleLoaded).toHaveBeenCalledWith('vlcPlayer');
  });

  it('checks if synchronization is enabled', () => {
    const checkbox = document.getElementById('vlc-player-status-sync-checkbox') as HTMLInputElement;
    checkbox.checked = true;
    expect(player.isSyncEnabled()).toBe(true);
    checkbox.checked = false;
    expect(player.isSyncEnabled()).toBe(false);
    checkbox.remove();
    expect(player.isSyncEnabled()).toBe(true);
  });

  it('opens tabs and updates the playlist visibility', () => {
    vi.useFakeTimers();
    player.openTab('tab-playlist');
    expect(cookies['kh-vlc-player-current-tab']).toBe('tab-playlist');
    expect(document.getElementById('tab-playlist-link')!.classList.contains('active')).toBe(true);
    expect((document.getElementById('tab-playlist') as HTMLElement).style.display).toBe('block');
    vi.advanceTimersByTime(0);
    expect((document.getElementById('playlist-table') as HTMLElement).style.display).toBe('table');

    player.openTab('tab-playlist-browser');
    vi.advanceTimersByTime(0);
    expect((document.getElementById('playlist-table') as HTMLElement).style.display).toBe('none');
    expect((document.getElementById('playlist-browser-table') as HTMLElement)?.style.display).toBe('table');

    player.openTab('tab-playing');
    vi.advanceTimersByTime(0);
    expect((document.getElementById('playlist-table') as HTMLElement).style.display).toBe('none');
  });

  it('executes vlc rc commands and plays files', () => {
    player.loadStateFromApiRound = vi.fn();
    player.playFile('/media/file.mp4');
    expect(mock.plugin.modal.loadingWheelModal.open).toHaveBeenCalled();
    const playCall = postHttp.mock.calls[postHttp.mock.calls.length - 1];
    expect(playCall[3]).toEqual({ file: '/media/file.mp4' });
    playCall[4]();
    expect(player.loadStateFromApiRound).toHaveBeenCalled();

    player.playFile('/media/file2.mp4');
    postHttp.mock.calls[postHttp.mock.calls.length - 1][5]();
    expect(player.loadStateFromApiRound).toHaveBeenCalledTimes(2);

    player.execVlcRcCommand('volume', 0);
    expect(postHttp.mock.calls[postHttp.mock.calls.length - 1][3]).toEqual({ name: 'volume', val: 0 });
    player.execVlcRcCommand('volume', null);
    expect(postHttp.mock.calls[postHttp.mock.calls.length - 1][3]).toEqual({ name: 'volume', val: null });
    player.execVlcRcCommand('volume', '10');
    expect(postHttp.mock.calls[postHttp.mock.calls.length - 1][3]).toEqual({ name: 'volume', val: '10' });

    player.close();
    const closeCall = deleteHttp.mock.calls[deleteHttp.mock.calls.length - 1];
    closeCall[4]();
    closeCall[5]();
    expect(player.loadStateFromApiRound).toHaveBeenCalled();
  });

  it('updates subtitle, audio, aspect ratio, seek and volume', () => {
    const rest = player.getRestClient();
    const post = vi.spyOn(rest, 'post');
    const lastBody = () => post.mock.calls[post.mock.calls.length - 1][2];

    player.setVlcRcStatus({ subtitleDelay: 100 });
    player.updateSubtitleDelay(50);
    expect(lastBody()).toEqual({ name: 'subdelay', val: 150 });
    player.setVlcRcStatus({});
    player.updateSubtitleDelay('25');
    expect(lastBody()).toEqual({ name: 'subdelay', val: 25 });

    const subtitle = document.getElementById('subtitle-track-dropdown') as HTMLSelectElement;
    subtitle.innerHTML = '<option value="">Select</option><option value="Disable">Disable</option><option value="2">Track 2</option>';
    subtitle.value = '';
    player.updateSubtitleTrack();
    subtitle.value = 'Disable';
    player.updateSubtitleTrack();
    expect(lastBody()).toEqual({ name: 'subtitle_track', val: -1 });
    subtitle.value = '2';
    player.updateSubtitleTrack();
    expect(lastBody()).toEqual({ name: 'subtitle_track', val: '2' });

    const audio = document.getElementById('audio-track-dropdown') as HTMLSelectElement;
    audio.innerHTML = '<option value="1">Track 1</option>';
    audio.value = '1';
    player.updateAudioTrack();
    expect(lastBody()).toEqual({ name: 'audio_track', val: '1' });

    const aspect = document.getElementById('aspect-ratio-dropdown') as HTMLSelectElement;
    aspect.value = '';
    player.updateAspectRatio();
    aspect.value = '16:9';
    player.updateAspectRatio();
    expect(lastBody()).toEqual({ name: 'aspectratio', val: '16:9' });

    player.seek(42);
    expect(lastBody()).toEqual({ name: 'seek', val: 42 });
    expect((document.getElementById('time-slider') as HTMLInputElement).value).toBe('42');
    player.setVolume(128);
    expect(lastBody()).toEqual({ name: 'volume', val: 128 });
    expect((document.getElementById('volume-slider') as HTMLInputElement).value).toBe('128');
  });

  it('tracks filename changes', () => {
    const update = vi.spyOn(player, 'updateView');
    player.setVlcRcStatus({ information: { meta: { filename: 'goku.mp4' } } });
    player.processFilenameChange();
    expect(mock.logger.info).toHaveBeenCalledWith('Playing file: goku.mp4', null);
    player.processFilenameChange();

    player.setVlcRcStatus({ information: {} });
    player.processFilenameChange();
    player.setVlcRcStatus({ information: { meta: {} } });
    player.processFilenameChange();
    player.setVlcRcStatus({});
    player.processFilenameChange();
    expect(update).not.toHaveBeenCalled();
  });

  it('delegates playlist operations', () => {
    const playlist = player.getPlaylist();
    const setUpdated = vi.spyOn(playlist, 'setUpdatedPlaylist');
    const reload = vi.spyOn(playlist, 'reload');
    const render = vi.spyOn(playlist, 'renderPlaylist');
    const scroll = vi.spyOn(playlist, 'scrollToCurrentlyPlaying');

    player.setUpdatedPlaylist([]);
    player.reloadPlaylist();
    player.renderPlaylist();
    player.scrollToCurrentlyPlaying();
    (document.getElementById('playlist-filter-input') as HTMLInputElement).value = 'goku';
    player.filterPlaylistRows();

    expect(setUpdated).toHaveBeenCalledWith([]);
    expect(reload).toHaveBeenCalled();
    expect(render).toHaveBeenCalled();
    expect(scroll).toHaveBeenCalled();
    expect(mock.util.table.filterTableRows).toHaveBeenCalledWith('goku', 'playlist-table-body', null, null);
  });

  it('updates and resets the player view', () => {
    vi.useFakeTimers();
    player.load();
    const status = {
      fullscreen: true,
      repeat: true,
      loop: true,
      random: true,
      state: 'stopped',
      volume: 0,
      time: 30,
      length: 120,
      subtitleDelay: 5,
      aspectRatio: '16:9',
      currentPlId: 5,
      information: {
        meta: { filename: 'goku_song.MP4', title: 'Goku Song' },
        subtitles: [
          { number: '2', language: 'Spanish', description: 'Spanish' },
          { number: '1', language: '', description: '' }
        ],
        audio: [{ number: '1', language: 'English', description: 'English' }]
      }
    };
    player.setVlcRcStatus(status);
    player.updateView();
    expect(document.getElementById('media-title')!.innerHTML).toBe('Goku Song');
    expect(document.getElementById('subtitle-delay-value')!.innerHTML).toBe('5');
    expect((document.getElementById('volume-slider') as HTMLInputElement).value).toBe('0');
    expect(document.getElementById('subtitle-track-dropdown')!.children.length).toBe(4);

    player.updateView();
    status.volume = 128;
    status.information.subtitles = [{ number: '3', language: '', description: 'Commentary' }];
    player.updateView();
    expect(document.getElementById('subtitle-track-dropdown')!.children.length).toBe(3);

    player.setVlcRcStatus({});
    player.updateView();
    expect(document.getElementById('media-title')!.innerHTML).toBe('No Media Loaded');
    player.resetView();
    expect(document.getElementById('current-time')!.innerHTML).toBe('--:--:--');
  });

  it('builds and executes server management actions', () => {
    const exec = vi.spyOn(player.getRestClient(), 'post');
    player.confirmSuspendServer();
    player.confirmRebootServer();
    const suspendButton = buttons.find((b) => b.innerHTML === '');
    expect(suspendButton).toBeDefined();
    const suspendClick = buttons[buttons.length - 2].__opts.click;
    const rebootClick = buttons[buttons.length - 1].__opts.click;
    suspendClick({}, {});
    rebootClick({}, {});

    expect(mock.plugin.modal.basicModal.open).toHaveBeenCalled();
    expect(exec).toHaveBeenCalledWith('/kame-house-admin/api/v1/admin/power-management/suspend', expect.anything(), { delay: 0 }, expect.any(Function), expect.any(Function));
    expect(exec).toHaveBeenCalledWith('/kame-house-admin/api/v1/admin/power-management/reboot', null, null, expect.any(Function), expect.any(Function));
    postHttp.mock.calls[postHttp.mock.calls.length - 2][4]();
    postHttp.mock.calls[postHttp.mock.calls.length - 1][5]();
    expect(mock.logger.info).toHaveBeenCalledWith('Server suspended', null);
    expect(mock.logger.error).toHaveBeenCalledWith('Error rebooting server', null);
  });

  it('sends screen and desktop control requests', () => {
    player.loadStateFromApiRound = vi.fn();
    player.unlockScreen();
    player.mouseSingleClick();
    player.mouseDoubleClick();
    player.keyPress('ENTER');
    player.keyPress('ENTER', 5);
    player.startKameHouseDesktop();
    player.stopKameHouseDesktop();
    player.getVlcStats();

    expect(mock.logger.debug).toHaveBeenCalledWith('keyPresses not set. Using default value of 1', null);
    expect(postHttp).toHaveBeenCalled();
    expect(deleteHttp).toHaveBeenCalled();
    expect(getHttp).toHaveBeenCalled();
  });

  it('renders the playlist and toggles filenames', () => {
    const playlist = player.getPlaylist();
    playlist.init();
    const list = [
      { id: 1, filename: '/media/goku.mp4' },
      { id: 2, filename: '/media/vegeta.mp4' }
    ];
    playlist.setUpdatedPlaylist(list);
    playlist.reload();
    expect(cookies['kh-vlc-player-show-playlist-checkbox']).toBe('true');
    expect((document.getElementById('playlist-size-val') as HTMLElement).innerHTML).toBe('2');

    player.setVlcRcStatus({ currentPlId: 5 });
    playlist.setUpdatedPlaylist(list);
    playlist.reload();
    expect((document.getElementById('playlist-current-position-val') as HTMLElement).innerHTML).toBe('2');

    const checkbox = document.getElementById('vlc-player-show-playlist-checkbox') as HTMLInputElement;
    checkbox.checked = false;
    playlist.renderPlaylist();
    expect(cookies['kh-vlc-player-show-playlist-checkbox']).toBe('false');

    checkbox.checked = true;
    playlist.renderPlaylist();
    player.toggleExpandPlaylistFilenames();
    player.toggleExpandPlaylistFilenames();

    checkbox.checked = false;
    player.toggleExpandPlaylistFilenames();

    playlist.setUpdatedPlaylist([]);
    playlist.reload();
    expect((document.getElementById('playlist-size-val') as HTMLElement).innerHTML).toBe('0');

    const playButtons = buttons.filter((b) => b.__opts && b.__opts.data && b.__opts.data.id);
    expect(playButtons.length).toBeGreaterThan(0);
    playButtons[0].__opts.click({}, { id: 1 });
    expect(postHttp).toHaveBeenCalled();
  });

  it('scrolls to the currently playing playlist item', () => {
    const playlist = player.getPlaylist();
    playlist.setUpdatedPlaylist([{ id: 4, filename: '/media/a.mp4' }]);
    playlist.reload();
    player.setVlcRcStatus({ currentPlId: 4 });
    playlist.scrollToCurrentlyPlaying();
    expect(mock.core.scrollTop).toHaveBeenCalled();
  });

  it('executes rest client requests with custom and default callbacks', () => {
    const rest = player.getRestClient();
    rest.get('/api', null, null, true, vi.fn(), vi.fn());
    const getCall = getHttp.mock.calls[getHttp.mock.calls.length - 1];
    expect(getCall[0].timeout).toBe(10);
    getCall[4]('body', 200, 'OK', {});
    getCall[5]('body', 404, 'Not Found', {});

    rest.get('/api2', null, null, false, null, null);
    getHttp.mock.calls[getHttp.mock.calls.length - 1][4]('body', 200, 'OK', {});
    getHttp.mock.calls[getHttp.mock.calls.length - 1][5]('body', 500, 'Error', {});
    expect(mock.plugin.modal.basicModal.openApiError).toHaveBeenCalled();

    rest.post('/api', null, null, vi.fn(), vi.fn());
    const postCall = postHttp.mock.calls[postHttp.mock.calls.length - 1];
    postCall[4]('body', 200, 'OK', {});
    postCall[5]('body', 503, 'Unavailable', {});
    expect(mock.plugin.modal.loadingWheelModal.close).toHaveBeenCalled();

    rest.delete('/api', null, null, vi.fn(), vi.fn());
    deleteHttp.mock.calls[deleteHttp.mock.calls.length - 1][4]('body', 200, 'OK', {});
  });

  it('loads the player state from the api and handles errors', () => {
    const debuggerObject = player.getDebugger();
    debuggerObject.getVlcRcStatusFromApi(true);
    debuggerObject.getPlaylistFromApi(true);
    const getCalls = getHttp.mock.calls;
    const statusCall = getCalls[getCalls.length - 2];
    const playlistCall = getCalls[getCalls.length - 1];

    statusCall[4]({ information: { meta: { filename: 'goku.mp4' } } }, 200, 'OK', {});
    playlistCall[4]([{ id: 1, filename: '/media/goku.mp4' }], 200, 'OK', {});
    statusCall[5]('error', 500, 'Error', {});
    playlistCall[5]('error', 500, 'Error', {});
    expect(mock.logger.error).toHaveBeenCalled();
  });

  it('gets vlc stats with and without checkboxes', () => {
    player.getVlcStats();
    const statsCall = getHttp.mock.calls[getHttp.mock.calls.length - 1];
    expect(statsCall[1]).toContain('fullReport=true');
    statsCall[4]('<pre>stats</pre>', 200, 'OK', {});
    statsCall[5]('error', 500, 'Error', {});
    expect(mock.plugin.kameHouseCommandManager.renderKameHouseCommandResult).toHaveBeenCalled();
    expect(mock.plugin.kameHouseCommandManager.renderErrorExecutingCommand).toHaveBeenCalled();

    (document.getElementById('vlc-player-get-full-report-checkbox') as HTMLElement).remove();
    (document.getElementById('vlc-player-update-stats-checkbox') as HTMLElement).remove();
    player.getVlcStats();
    expect(getHttp.mock.calls[getHttp.mock.calls.length - 1][1]).toContain('fullReport=false');
  });

  it('stops and restarts the synchronization loops from mobile events', () => {
    vi.useFakeTimers();
    mock.extension.mobile = { core: { startZtvLockscreen: vi.fn(), keepAliveZtvLockscreen: vi.fn() } };
    player.load();
    expect(mobileListeners).not.toBeNull();
    mobileListeners.onPause();
    mobileListeners.onResume();
    vi.advanceTimersByTime(20000);
    expect(mock.logger.info).toHaveBeenCalled();
  });
});
