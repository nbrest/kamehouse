/// <reference types="vitest/globals" />

import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { installKameHouseMock, createDocumentMock } from '../../../helpers/kamehouse-mock';

describe('test-module websocket-index', () => {
  let instances: any[];
  let clickHandlers: any;
  let formAddEventListener: any;

  beforeEach(() => {
    vi.resetModules();
    vi.useFakeTimers();
    instances = [];
    clickHandlers = {};
    formAddEventListener = vi.fn();
    const MockKameHouseWebSocket = class {
      statusUrl = vi.fn();
      topicUrl = vi.fn();
      pollUrl = vi.fn();
      connect = vi.fn((cb: any) => { this.connectCb = cb; });
      isConnected = vi.fn(() => true);
      disconnect = vi.fn();
      poll = vi.fn();
      connectCb: any;
      constructor() { instances.push(this); }
    };
    (globalThis as any).KameHouseWebSocket = MockKameHouseWebSocket;
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  function install() {
    return installKameHouseMock({
      core: { loadKameHouseWebSocket: vi.fn() },
      logger: { setLogLevel: vi.fn() },
      plugin: {
        modal: {
          loadingWheelModal: { open: vi.fn(), close: vi.fn() },
          basicModal: { open: vi.fn() }
        }
      },
      util: {
        time: { getDateFromEpoch: vi.fn(() => new Date('2025-01-02T03:04:05Z')) },
        dom: {
          setClickById: vi.fn((id: any, event: any, handler: any) => { clickHandlers[id] = handler; }),
          emptyById: vi.fn()
        }
      }
    }, {
      window: { location: { search: '' } },
      document: createDocumentMock({
        'first-name': { value: 'Goku' },
        'last-name': { value: 'Son' }
      })
    });
  }

  async function importWebsocket() {
    const mock = install();
    mock.document = (global as any).document;
    await import('@/js/test-module/websocket/websocket-index');
    return { mock, ws: instances[0] };
  }

  it('loads the websocket extension and configures it', async () => {
    const { mock, ws } = await importWebsocket();

    expect(mock.logger.info).toHaveBeenCalledWith('Started initializing TestWebSocket', null);
    expect(mock.util.banner.setRandomAllBanner).toHaveBeenCalledWith(null);
    expect(mock.core.loadKameHouseWebSocket).toHaveBeenCalled();
    expect(mock.logger.setLogLevel).toHaveBeenCalledWith(4);
    expect(ws).toBeDefined();
    expect(ws.statusUrl).toHaveBeenCalledWith('/kame-house-testmodule/api/ws/test-module/websocket');
    expect(ws.topicUrl).toHaveBeenCalledWith('/topic/test-module/websocket-out');
    expect(ws.pollUrl).toHaveBeenCalledWith('/app/test-module/websocket-in');
    expect(clickHandlers['connect']).toBeTypeOf('function');
    expect(clickHandlers['disconnect']).toBeTypeOf('function');
    expect(clickHandlers['send']).toBeTypeOf('function');
  });

  it('connects successfully', async () => {
    const { mock, ws } = await importWebsocket();

    clickHandlers['connect']();
    expect(mock.plugin.modal.loadingWheelModal.open).toHaveBeenCalledWith('Connecting websocket...');
    expect(ws.connect).toHaveBeenCalled();

    ws.isConnected.mockReturnValue(true);
    vi.advanceTimersByTime(4000);

    expect(mock.plugin.modal.loadingWheelModal.close).toHaveBeenCalled();
    expect(mock.util.dom.classListAddById).toHaveBeenCalledWith('connect', 'hidden-kh');
    expect(mock.util.dom.classListRemoveById).toHaveBeenCalledWith('connected', 'hidden-kh');
    expect(mock.logger.debug).toHaveBeenCalledWith('Connected WebSocket', null);
  });

  it('reports a failed connection', async () => {
    const { mock, ws } = await importWebsocket();

    clickHandlers['connect']();
    ws.isConnected.mockReturnValue(false);
    vi.advanceTimersByTime(4000);

    expect(mock.plugin.modal.loadingWheelModal.close).toHaveBeenCalled();
    expect(mock.logger.error).toHaveBeenCalledWith('Error connecting websocket', 'Error connecting websocket');
    expect(mock.plugin.modal.basicModal.open).toHaveBeenCalledWith('Error connecting websocket');
  });

  it('disconnects the websocket', async () => {
    const { mock, ws } = await importWebsocket();

    clickHandlers['disconnect']();

    expect(ws.disconnect).toHaveBeenCalled();
    expect(mock.util.dom.classListRemoveById).toHaveBeenCalledWith('connect', 'hidden-kh');
    expect(mock.util.dom.classListAddById).toHaveBeenCalledWith('connected', 'hidden-kh');
    expect(mock.logger.debug).toHaveBeenCalledWith('Disconnected WebSocket', null);
  });

  it('sends a websocket request', async () => {
    const { ws } = await importWebsocket();

    clickHandlers['send']();

    expect(ws.poll).toHaveBeenCalledWith(JSON.stringify({ firstName: 'Goku', lastName: 'Son' }));
  });

  it('shows a websocket response', async () => {
    const { mock, ws } = await importWebsocket();

    clickHandlers['connect']();
    ws.connectCb({ body: JSON.stringify({ date: 123, message: 'hello' }) });

    expect(mock.logger.trace).toHaveBeenCalledWith(expect.stringContaining('hello'), null);
    expect(mock.util.time.getDateFromEpoch).toHaveBeenCalledWith(123);
    expect(mock.util.dom.appendById).toHaveBeenCalledWith('websocket-responses', expect.anything());
  });
});
