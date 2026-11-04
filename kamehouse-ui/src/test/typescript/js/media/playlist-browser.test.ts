/// <reference types="vitest/globals" />

import { describe, it, expect, vi, beforeEach } from 'vitest';
import { installKameHouseMock, createDocumentMock } from '../../helpers/kamehouse-mock';

describe('media playlist-browser', () => {
  let get: any;
  let buttons: any[];
  let elements: any;

  beforeEach(() => {
    vi.resetModules();
    buttons = [];
    get = vi.fn();
    elements = {
      'playlist-browser-filter-input': { value: 'goku' },
      'playlist-category-dropdown': { options: [{ value: 'dbz\\saiyajin' }], selectedIndex: 0 },
      'playlist-dropdown': { value: '/media/dbz/goku.m3u', options: [] },
      'playlist-browser-table': { firstElementChild: { textContent: 'goku.mp4' } },
      'playlist-browser-table-body': { firstElementChild: { textContent: 'goku.mp4' } },
      'vlc-player-show-playlist-browser-checkbox': { checked: true }
    };
  });

  function install() {
    return installKameHouseMock({
      extension: {
        vlcPlayer: { playFile: vi.fn(), openTab: vi.fn(), reloadPlaylist: vi.fn() }
      },
      http: { get },
      plugin: { debugger: { http: { get } } },
      util: {
        dom: {
          getButton: vi.fn((options: any) => { buttons.push(options); return options; }),
          getTbody: vi.fn(() => ({ firstElementChild: { textContent: 'goku.mp4' } })),
          replaceWithById: vi.fn(),
          detach: vi.fn()
        },
        file: { getShortFilename: vi.fn((path: string) => path.split('/').pop()) },
        cursor: { setCursorWait: vi.fn(), setCursorDefault: vi.fn() },
        table: { filterTableRows: vi.fn() }
      }
    }, {
      window: { location: { search: '' } },
      document: createDocumentMock(elements)
    });
  }

  async function importBrowser() {
    const mock = install();
    await import('@/js/media/playlist-browser');
    return { mock, browser: mock.extension.playlistBrowser };
  }

  const playlists = [
    { category: 'dbz\\saiyajin', name: 'goku_song.m3u', path: '/media/dbz/goku_song.m3u' },
    { category: 'dbz\\saiyajin', name: 'vegeta.m3u', path: '/media/dbz/vegeta.m3u' },
    { category: 'onepiece/section', name: 'luffy.m3u', path: '/media/onepiece/luffy.m3u' }
  ];

  it('loads the playlist browser and populates categories', async () => {
    const { mock, browser } = await importBrowser();

    expect(browser).toBeDefined();
    expect(mock.logger.info).toHaveBeenCalledWith('Started initializing playlist browser', null);
    expect(mock.util.dom.replaceWithById).toHaveBeenCalledWith('toggle-playlist-browser-filenames-btn', buttons[1]);
    expect(mock.util.module.setModuleLoaded).toHaveBeenCalledWith('playlistBrowser');
    expect(get).toHaveBeenCalledWith(expect.anything(), '/kame-house-media/api/v1/media/playlists', null, null, expect.any(Function), expect.any(Function));
  });

  it('handles populated playlist categories', async () => {
    const { mock } = await importBrowser();

    get.mock.calls[0][4](playlists, 200, '', {});

    expect(mock.logger.debug).toHaveBeenCalledWith(expect.stringContaining('Playlist categories:'), null);
    expect(mock.util.dom.append).toHaveBeenCalled();
  });

  it('logs an error populating playlist categories', async () => {
    const { mock } = await importBrowser();
    get.mock.calls[0][5]('error', 500, '', {});
    expect(mock.logger.error).toHaveBeenCalledWith('Error populating playlist categories', null);
  });

  it('populates the playlists for a selected category', async () => {
    const { mock, browser } = await importBrowser();
    get.mock.calls[0][4](playlists, 200, '', {});
    mock.util.dom.append.mockClear();

    browser.populatePlaylists();

    expect(mock.logger.debug).toHaveBeenCalledWith(expect.stringContaining('Selected Playlist Category:'), null);
    expect(mock.util.dom.empty).toHaveBeenCalled();
    expect(mock.util.dom.append).toHaveBeenCalledTimes(3);
  });

  it('loads the selected playlist content', async () => {
    const { mock, browser } = await importBrowser();
    get.mock.calls[0][4](playlists, 200, '', {});
    get.mockClear();

    browser.loadPlaylistContent();

    expect(get).toHaveBeenCalledTimes(1);
    const call = get.mock.calls[0];
    expect(call[1]).toBe('/kame-house-media/api/v1/media/playlist');
    expect(call[3]).toEqual({ path: '/media/dbz/goku.m3u' });
    expect(mock.util.cursor.setCursorWait).toHaveBeenCalled();

    call[4]({ files: ['/media/dbz/goku.mp4', '/media/dbz/gohan.mp4'] }, 200, '', {});
    expect(mock.util.cursor.setCursorDefault).toHaveBeenCalled();
    expect(mock.util.dom.append).toHaveBeenCalled();
  });

  it('handles an error getting the playlist content', async () => {
    const { mock, browser } = await importBrowser();
    get.mock.calls[0][4](playlists, 200, '', {});
    get.mockClear();

    browser.loadPlaylistContent();
    get.mock.calls[0][5]('error', 500, '', {});
    expect(mock.logger.error).toHaveBeenCalledWith('Error getting playlist content', null);
    expect(mock.util.cursor.setCursorDefault).toHaveBeenCalled();
  });

  it('shows an empty playlist', async () => {
    const { mock, browser } = await importBrowser();
    get.mock.calls[0][4](playlists, 200, '', {});
    get.mockClear();

    browser.loadPlaylistContent();
    get.mock.calls[0][4]({ files: [] }, 200, '', {});

    expect(mock.util.dom.append).toHaveBeenCalledWith(expect.anything(), expect.anything());
    expect(mock.util.dom.setHtmlById).toHaveBeenCalledWith('playlist-browser-size', '0');
  });

  it('renders the hidden playlist when the checkbox is disabled', async () => {
    const { mock, browser } = await importBrowser();
    get.mock.calls[0][4](playlists, 200, '', {});
    get.mockClear();
    elements['vlc-player-show-playlist-browser-checkbox'].checked = false;

    browser.loadPlaylistContent();
    get.mock.calls[0][4]({ files: ['/media/dbz/goku.mp4'] }, 200, '', {});

    expect(mock.util.cookies.setCookie).toHaveBeenCalledWith('kh-vlc-player-show-playlist-browser-checkbox', 'false', null);
    expect(mock.util.dom.classListAddById).toHaveBeenCalledWith('playlist-browser-controls', 'hidden-kh');
  });

  it('filters the playlist rows', async () => {
    const { mock, browser } = await importBrowser();
    browser.filterPlaylistRows();
    expect(mock.util.table.filterTableRows).toHaveBeenCalledWith('goku', 'playlist-browser-table-body', null, null);
  });

  it('plays the selected playlist', async () => {
    const { mock, browser } = await importBrowser();
    browser.playSelectedPlaylist();

    expect(mock.extension.vlcPlayer.playFile).toHaveBeenCalledWith('/media/dbz/goku.m3u');
    expect(mock.extension.vlcPlayer.openTab).toHaveBeenCalledWith('tab-playing');
    expect(mock.extension.vlcPlayer.reloadPlaylist).toHaveBeenCalled();
  });

  it('toggles playlist filenames between short names and absolute paths', async () => {
    const { mock, browser } = await importBrowser();
    get.mock.calls[0][4](playlists, 200, '', {});
    get.mockClear();
    browser.loadPlaylistContent();
    get.mock.calls[0][4]({ files: ['/media/dbz/goku.mp4'] }, 200, '', {});
    mock.util.dom.detach.mockClear();
    mock.util.dom.replaceWithById.mockClear();

    buttons[1].click({}, null);

    expect(mock.util.dom.detach).toHaveBeenCalled();
    expect(mock.util.dom.replaceWithById).toHaveBeenCalledWith('toggle-playlist-browser-filenames-btn', buttons[0]);
    expect(mock.util.table.filterTableRows).toHaveBeenCalled();
  });

  it('toggles back to filenames when absolute paths are shown', async () => {
    const { mock, browser } = await importBrowser();
    get.mock.calls[0][4](playlists, 200, '', {});
    get.mockClear();
    browser.loadPlaylistContent();
    get.mock.calls[0][4]({ files: ['/media/dbz/goku.mp4'] }, 200, '', {});
    elements['playlist-browser-table-body'].firstElementChild.textContent = 'different';
    mock.util.dom.detach.mockClear();
    mock.util.dom.replaceWithById.mockClear();

    buttons[1].click({}, null);

    expect(mock.util.dom.detach).toHaveBeenCalled();
    expect(mock.util.dom.replaceWithById).toHaveBeenCalledWith('toggle-playlist-browser-filenames-btn', buttons[1]);
  });

  it('does not toggle before a playlist is loaded', async () => {
    const { mock, browser } = await importBrowser();
    mock.util.dom.detach.mockClear();
    buttons[1].click({}, null);
    expect(mock.util.dom.detach).not.toHaveBeenCalled();
  });
});
