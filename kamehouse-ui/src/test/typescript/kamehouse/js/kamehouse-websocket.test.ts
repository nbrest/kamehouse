/// <reference types="vitest/globals" />

import { describe, it, expect, vi, beforeEach } from 'vitest';
import { installKameHouseMock, createDocumentMock } from '../../helpers/kamehouse-mock';
import '@/kamehouse/js/kamehouse-websocket';

function makeClient(options: any = {}) {
  const client: any = {
    connected: options.connected !== undefined ? options.connected : true,
    subscriptions: options.subscriptions || {},
    debug: vi.fn(),
    unsubscribe: vi.fn(),
    disconnect: options.disconnect || vi.fn(),
    send: options.send || vi.fn(),
    subscribe: options.subscribe || vi.fn(),
    connect: options.connect || vi.fn()
  };
  return client;
}

describe('kamehouse-websocket', () => {
  beforeEach(() => {
    vi.resetModules();
  });

  function setup(overrides: any = {}) {
    return installKameHouseMock(overrides, {
      window: { location: { search: '', protocol: 'http:', host: 'localhost' } },
      document: createDocumentMock()
    });
  }

  function newWebSocket() {
    return new (globalThis as any).KameHouseWebSocket();
  }

  function configureUrls(ws: any) {
    ws.statusUrl('/status');
    ws.topicUrl('/topic');
    ws.pollUrl('/poll');
  }

  describe('configuration', () => {
    it('sets urls on desktop', () => {
      const mock = setup();
      const ws = newWebSocket();

      configureUrls(ws);

      expect(mock.util.mobile.exec).toHaveBeenCalled();
      expect(ws.isConnected(null)).toBe(false);
    });

    it('sets the status url on mobile when the mobile module is loaded', () => {
      const getUrl = vi.fn(() => 'https://server');
      const mock = setup({
        extension: { mobile: { core: { getSelectedBackendServerUrl: getUrl } } },
        util: {
          mobile: { exec: (desktop: any, mobile: any) => mobile() },
          module: { isModuleLoaded: () => true, waitForModules: vi.fn() }
        }
      });
      const ws = newWebSocket();

      ws.statusUrl('/status');

      expect(getUrl).toHaveBeenCalled();
      expect(mock.util.module.waitForModules).not.toHaveBeenCalled();
    });

    it('waits for the mobile module before setting the status url', () => {
      const getUrl = vi.fn(() => 'https://server');
      const mock = setup({
        extension: { mobile: { core: { getSelectedBackendServerUrl: getUrl } } },
        util: {
          mobile: { exec: (desktop: any, mobile: any) => mobile() },
          module: {
            isModuleLoaded: () => false,
            waitForModules: vi.fn((modules: any, cb: any) => cb())
          }
        }
      });
      const ws = newWebSocket();

      ws.statusUrl('/status');

      expect(mock.util.module.waitForModules).toHaveBeenCalled();
      expect(getUrl).toHaveBeenCalled();
    });
  });

  describe('isConnected', () => {
    it('returns the connected state of a client', () => {
      setup();
      const ws = newWebSocket();

      expect(ws.isConnected({ connected: true })).toBe(true);
      expect(ws.isConnected({ connected: false })).toBe(false);
    });

    it('logs an error when checking the client fails', () => {
      const mock = setup();
      const ws = newWebSocket();
      const client: any = {};
      Object.defineProperty(client, 'connected', { get: () => { throw new Error('boom'); } });

      ws.isConnected(client);

      expect(mock.logger.error).toHaveBeenCalledWith(expect.stringContaining('Error in websocket-kamehouse'), null);
    });
  });

  describe('connect', () => {
    it('warns when already connected and validates missing urls', () => {
      const mock = setup();
      const ws = newWebSocket();

      ws.connect(() => {});

      expect(mock.logger.error).toHaveBeenCalledWith(expect.stringContaining('statusUrl or topicUrl are not set'), expect.anything());
    });

    it('rejects a non function callback', () => {
      const mock = setup();
      const ws = newWebSocket();
      configureUrls(ws);

      ws.connect('not-a-function');

      expect(mock.logger.error).toHaveBeenCalledWith(expect.stringContaining('not a valid function'), expect.anything());
    });

    it('connects and subscribes to the topic', () => {
      const mock = setup();
      let topicHandler: any;
      let successHandler: any;
      const client = makeClient({
        subscribe: vi.fn((topic: any, cb: any) => { topicHandler = cb; }),
        connect: vi.fn((headers: any, success: any) => { successHandler = success; })
      });
      (global as any).SockJS = vi.fn(() => ({}));
      (global as any).Stomp = { over: vi.fn(() => client) };
      const ws = newWebSocket();
      configureUrls(ws);
      const callback = vi.fn();

      ws.connect(callback);
      successHandler('frame');
      topicHandler({ body: 'hello' });

      expect((global as any).SockJS).toHaveBeenCalledWith(expect.stringContaining('/status'));
      expect(client.subscribe).toHaveBeenCalledWith('/topic', expect.any(Function));
      expect(callback).toHaveBeenCalledWith({ body: 'hello' });
      expect(mock.logger.warn).not.toHaveBeenCalled();
    });

    it('warns when connecting an already connected websocket', () => {
      const mock = setup();
      const client = makeClient({ connect: vi.fn((h: any, s: any) => s('frame')) });
      (global as any).SockJS = vi.fn(() => ({}));
      (global as any).Stomp = { over: vi.fn(() => client) };
      const ws = newWebSocket();
      configureUrls(ws);

      ws.connect(() => {});
      ws.connect(() => {});

      expect(mock.logger.warn).toHaveBeenCalledWith('WebSocket is already connected!', null);
    });

    it('logs an error thrown inside the topic callback', () => {
      const mock = setup();
      let topicHandler: any;
      const client = makeClient({
        subscribe: vi.fn((topic: any, cb: any) => { topicHandler = cb; }),
        connect: vi.fn((h: any, s: any) => s('frame'))
      });
      (global as any).SockJS = vi.fn(() => ({}));
      (global as any).Stomp = { over: vi.fn(() => client) };
      const ws = newWebSocket();
      configureUrls(ws);

      ws.connect(() => { throw new Error('callback error'); });
      topicHandler({});

      expect(mock.logger.error).toHaveBeenCalledWith(expect.stringContaining('Error during stompClient.subscribe() callback'), null);
    });

    it('logs an error thrown inside the connect callback', () => {
      const mock = setup();
      const client = makeClient({
        subscribe: vi.fn(() => { throw new Error('subscribe error'); }),
        connect: vi.fn((h: any, s: any) => s('frame'))
      });
      (global as any).SockJS = vi.fn(() => ({}));
      (global as any).Stomp = { over: vi.fn(() => client) };
      const ws = newWebSocket();
      configureUrls(ws);

      ws.connect(() => {});

      expect(mock.logger.error).toHaveBeenCalledWith(expect.stringContaining('Error during stompClient.connect() callback'), null);
    });

    it('logs an error when the connection fails', () => {
      const mock = setup();
      const client = makeClient({ connect: vi.fn((h: any, s: any, e: any) => e()) });
      (global as any).SockJS = vi.fn(() => ({}));
      (global as any).Stomp = { over: vi.fn(() => client) };
      const ws = newWebSocket();
      configureUrls(ws);

      ws.connect(() => {});

      expect(mock.logger.error).toHaveBeenCalledWith('Error during stompClient.connect()', expect.anything());
    });

    it('logs an error when the socket cannot be created', () => {
      const mock = setup();
      (global as any).SockJS = vi.fn(() => { throw new Error('socket error'); });
      const ws = newWebSocket();
      configureUrls(ws);

      ws.connect(() => {});

      expect(mock.logger.error).toHaveBeenCalledWith(expect.stringContaining('Error connecting websocket'), null);
    });
  });

  describe('disconnect', () => {
    it('logs an error when there is no client', () => {
      const mock = setup();
      const ws = newWebSocket();

      ws.disconnect(null);

      expect(mock.logger.error).toHaveBeenCalledWith(expect.stringContaining('stompClient is not set'), expect.anything());
    });

    it('unsubscribes and disconnects a client', () => {
      const mock = setup();
      const ws = newWebSocket();
      const client = makeClient({ connected: false, subscriptions: { sub1: {} } });

      ws.disconnect(client);

      expect(mock.logger.debug).toHaveBeenCalledWith('WebSocket is not connected. No need to disconnect but attempting anyway.', null);
      expect(client.unsubscribe).toHaveBeenCalledWith('sub1');
      expect(client.disconnect).toHaveBeenCalled();
    });

    it('disconnects a client without subscriptions', () => {
      setup();
      const ws = newWebSocket();
      const client = makeClient({ connected: true, subscriptions: {} });

      ws.disconnect(client);

      expect(client.unsubscribe).not.toHaveBeenCalled();
      expect(client.disconnect).toHaveBeenCalled();
    });

    it('logs an error when disconnecting fails', () => {
      const mock = setup();
      const ws = newWebSocket();
      const client = makeClient({ connected: true, disconnect: vi.fn(() => { throw new Error('disconnect'); }) });

      ws.disconnect(client);

      expect(mock.logger.error).toHaveBeenCalledWith(expect.stringContaining('Error disconnecting web socket'), null);
    });
  });

  describe('poll', () => {
    it('logs an error when the poll url is not set', () => {
      const mock = setup();
      const ws = newWebSocket();

      ws.poll({}, {});

      expect(mock.logger.error).toHaveBeenCalledWith(expect.stringContaining('pollUrl is not set'), expect.anything());
    });

    it('warns when not connected and sends the poll', () => {
      const mock = setup();
      const client = makeClient({ connect: vi.fn((h: any, s: any) => s('frame')) });
      (global as any).SockJS = vi.fn(() => ({}));
      (global as any).Stomp = { over: vi.fn(() => client) };
      const ws = newWebSocket();
      configureUrls(ws);
      ws.connect(() => {});
      client.connected = false;

      ws.poll(null, null);

      expect(mock.logger.warn).toHaveBeenCalledWith('WebSocket is not connected. Can\'t poll', null);
      expect(client.send).toHaveBeenCalledWith('/poll', {}, {});
    });

    it('logs an error when sending the poll fails', () => {
      const mock = setup();
      const client = makeClient({
        connect: vi.fn((h: any, s: any) => s('frame')),
        send: vi.fn(() => { throw new Error('send'); })
      });
      (global as any).SockJS = vi.fn(() => ({}));
      (global as any).Stomp = { over: vi.fn(() => client) };
      const ws = newWebSocket();
      configureUrls(ws);
      ws.connect(() => {});

      ws.poll({ data: 1 }, { header: 'x' });

      expect(mock.logger.error).toHaveBeenCalledWith(expect.stringContaining('Error polling the websocket'), null);
    });
  });

  describe('debug mode', () => {
    it('enables and disables stomp debug mode', () => {
      setup();
      const client = makeClient({ connect: vi.fn((h: any, s: any) => s('frame')) });
      const debugFn = vi.fn();
      client.debug = debugFn;
      (global as any).SockJS = vi.fn(() => ({}));
      (global as any).Stomp = { over: vi.fn(() => client) };
      const ws = newWebSocket();
      configureUrls(ws);
      ws.connect(() => {});

      ws.enableStompDebugMode();
      expect(client.debug).toBe(debugFn);

      ws.disableStompDebugMode();
      expect(client.debug).toBeNull();
    });
  });

  describe('previous connections', () => {
    it('disconnects previous connections before connecting again', () => {
      setup();
      const first = makeClient({ connect: vi.fn((h: any, s: any) => s('frame')), connected: true });
      const second = makeClient({ connect: vi.fn((h: any, s: any) => s('frame')), connected: true });
      const clients = [first, second];
      (global as any).SockJS = vi.fn(() => ({}));
      (global as any).Stomp = { over: vi.fn(() => clients.shift() || second) };
      const ws = newWebSocket();
      configureUrls(ws);

      ws.connect(() => {});
      ws.connect(() => {});

      expect(first.disconnect).toHaveBeenCalled();
    });
  });
});
