/// <reference types="vitest/globals" />

import { describe, it, expect, vi, beforeEach } from 'vitest';
import { installKameHouseMock, createDocumentMock } from '../../../../helpers/kamehouse-mock';

function deferred() {
  const d: any = {};
  d.promise = new Promise((resolve, reject) => { d.resolve = resolve; d.reject = reject; });
  return d;
}

describe('angular-1 dragonball-user-service', () => {
  let serviceFactory: any;
  let http: any;

  beforeEach(() => {
    vi.resetModules();
    serviceFactory = null;
    http = { get: vi.fn(), post: vi.fn(), put: vi.fn(), delete: vi.fn() };
  });

  function install() {
    const app: any = {
      controller: vi.fn(() => app),
      service: vi.fn((name: any, deps: any) => { serviceFactory = deps[deps.length - 1]; return app; }),
      factory: vi.fn(() => app)
    };
    (global as any).angular = { module: vi.fn(() => app), element: vi.fn() };
    return installKameHouseMock({ http }, {
      window: { location: { search: '' } },
      document: createDocumentMock()
    });
  }

  async function createService() {
    await import('@/js/test-module/angular-1/service/dragonball-user-service');
    const $q = { defer: () => deferred() };
    return serviceFactory($q);
  }

  it('fetches all dragonball users', async () => {
    install();
    const service = await createService();

    const promise = service.fetchAllDragonBallUsers();
    expect(http.get).toHaveBeenCalledWith(expect.anything(), '/kame-house-testmodule/api/v1/test-module/dragonball/users', null, null, expect.any(Function), expect.any(Function));
    http.get.mock.calls[0][4]([{ id: 1 }], 200, '', {});
    await expect(promise).resolves.toEqual([{ id: 1 }]);
  });

  it('rejects when fetching all dragonball users fails', async () => {
    install();
    const service = await createService();

    const promise = service.fetchAllDragonBallUsers();
    http.get.mock.calls[0][5]('error', 500, '', {});
    await expect(promise).rejects.toBe('error');
  });

  it('creates a dragonball user', async () => {
    install();
    const service = await createService();

    const user = { username: 'goku' };
    const promise = service.createDragonBallUser(user);
    expect(http.post).toHaveBeenCalledWith(expect.anything(), '/kame-house-testmodule/api/v1/test-module/dragonball/users', expect.anything(), user, expect.any(Function), expect.any(Function));
    http.post.mock.calls[0][4]({ id: 2 }, 201, '', {});
    await expect(promise).resolves.toEqual({ id: 2 });
  });

  it('rejects when creating a dragonball user fails', async () => {
    install();
    const service = await createService();

    const promise = service.createDragonBallUser({});
    http.post.mock.calls[0][5]('error', 500, '', {});
    await expect(promise).rejects.toBe('error');
  });

  it('updates a dragonball user', async () => {
    install();
    const service = await createService();

    const user = { username: 'vegeta' };
    const promise = service.updateDragonBallUser(user, 7);
    expect(http.put).toHaveBeenCalledWith(expect.anything(), '/kame-house-testmodule/api/v1/test-module/dragonball/users/7', expect.anything(), user, expect.any(Function), expect.any(Function));
    http.put.mock.calls[0][4]({ id: 7 }, 200, '', {});
    await expect(promise).resolves.toEqual({ id: 7 });
  });

  it('rejects when updating a dragonball user fails', async () => {
    install();
    const service = await createService();

    const promise = service.updateDragonBallUser({}, 7);
    http.put.mock.calls[0][5]('error', 500, '', {});
    await expect(promise).rejects.toBe('error');
  });

  it('deletes a dragonball user', async () => {
    install();
    const service = await createService();

    const promise = service.deleteDragonBallUser(9);
    expect(http.delete).toHaveBeenCalledWith(expect.anything(), '/kame-house-testmodule/api/v1/test-module/dragonball/users/9', null, null, expect.any(Function), expect.any(Function));
    http.delete.mock.calls[0][4]('', 204, '', {});
    await expect(promise).resolves.toBe('');
  });

  it('rejects when deleting a dragonball user fails', async () => {
    install();
    const service = await createService();

    const promise = service.deleteDragonBallUser(9);
    http.delete.mock.calls[0][5]('error', 500, '', {});
    await expect(promise).rejects.toBe('error');
  });
});
