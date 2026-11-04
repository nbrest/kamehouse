/// <reference types="vitest/globals" />

import { describe, it, expect, vi, beforeEach } from 'vitest';
import { installKameHouseMock, createDocumentMock } from '../../../../helpers/kamehouse-mock';

function flush() {
  return new Promise((resolve) => setTimeout(resolve, 0));
}

describe('angular-1 dragonball-user-controller', () => {
  let controllerFactory: any;
  let scope: any;
  let location: any;
  let context: any;
  let backButtonOptions: any;

  beforeEach(() => {
    vi.resetModules();
    controllerFactory = null;
    backButtonOptions = null;
    scope = { myForm: { $setPristine: vi.fn() } };
    location = { path: vi.fn() };
  });

  function install(service: any) {
    const app: any = {
      controller: vi.fn((name: any, deps: any) => { controllerFactory = deps[deps.length - 1]; return app; }),
      service: vi.fn(() => app),
      factory: vi.fn(() => app)
    };
    (global as any).angular = {
      module: vi.fn(() => app),
      copy: vi.fn((value: any) => JSON.parse(JSON.stringify(value))),
      element: vi.fn()
    };
    (global as any).history = { back: vi.fn() };
    const mock = installKameHouseMock({
      plugin: {
        modal: {
          basicModal: {
            setHtml: vi.fn(),
            appendHtml: vi.fn(),
            open: vi.fn(),
            close: vi.fn()
          }
        }
      },
      util: {
        dom: {
          getButton: vi.fn((options: any) => { backButtonOptions = options; return 'back-button'; })
        }
      }
    }, {
      window: { location: { search: '' } },
      document: createDocumentMock()
    });
    mock.service = service;
    return mock;
  }

  async function createController(service: any) {
    const mock = install(service);
    await import('@/js/test-module/angular-1/controller/dragonball-user-controller');
    context = {};
    controllerFactory.call(context, scope, mock.service, location);
    await flush();
    return { context, mock };
  }

  function makeService(overrides: any = {}) {
    return {
      fetchAllDragonBallUsers: vi.fn(() => Promise.resolve([])),
      createDragonBallUser: vi.fn(() => Promise.resolve({})),
      updateDragonBallUser: vi.fn(() => Promise.resolve({})),
      deleteDragonBallUser: vi.fn(() => Promise.resolve({})),
      ...overrides
    };
  }

  it('registers the controller and fetches all users', async () => {
    const users = [{ id: 1, username: 'goku' }];
    const service = makeService({ fetchAllDragonBallUsers: vi.fn(() => Promise.resolve(users)) });
    await createController(service);

    expect((global as any).angular.module).toHaveBeenCalledWith('myApp');
    expect(context.users).toEqual(users);
    expect(context.user).toEqual({ id: null, username: '', email: '', age: 0, powerLevel: 0, stamina: 0 });
  });

  it('handles an error fetching users', async () => {
    const errResponse = { data: { message: 'bad request' }, status: 500 };
    const service = makeService({ fetchAllDragonBallUsers: vi.fn(() => Promise.reject(errResponse)) });
    const { mock } = await createController(service);

    expect(mock.logger.error).toHaveBeenCalledWith('Error while fetching DragonBallUsers : bad request', null);
    expect(mock.plugin.modal.basicModal.setHtml).toHaveBeenCalledWith('Error while fetching DragonBallUsers : bad request');
    expect(mock.plugin.modal.basicModal.open).toHaveBeenCalled();
    expect(location.path).toHaveBeenCalledWith('/500');
  });

  it('handles an error response without a message', async () => {
    const errResponse = { data: null, status: 400 };
    const service = makeService({ fetchAllDragonBallUsers: vi.fn(() => Promise.reject(errResponse)) });
    const { mock } = await createController(service);

    expect(mock.logger.error).toHaveBeenCalledWith('Error while fetching DragonBallUsers', null);
    expect(location.path).toHaveBeenCalledWith('/400');
  });

  it('does not redirect when the error has no status', async () => {
    const errResponse = { data: null, status: null };
    const service = makeService({ fetchAllDragonBallUsers: vi.fn(() => Promise.reject(errResponse)) });
    await createController(service);

    expect(location.path).not.toHaveBeenCalled();
  });

  it('creates a user on submit when there is no id', async () => {
    const service = makeService();
    await createController(service);

    context.user.username = 'goku';
    const submittedUser = context.user;
    context.submit();
    await flush();

    expect(service.createDragonBallUser).toHaveBeenCalledWith(submittedUser);
    expect(context.user).toEqual({ id: null, username: '', email: '', age: 0, powerLevel: 0, stamina: 0 });
    expect(scope.myForm.$setPristine).toHaveBeenCalled();
    expect(service.fetchAllDragonBallUsers).toHaveBeenCalledTimes(2);
  });

  it('updates a user on submit when there is an id', async () => {
    const service = makeService();
    await createController(service);

    context.user = { id: 3, username: 'vegeta' };
    const submittedUser = context.user;
    context.submit();
    await flush();

    expect(service.updateDragonBallUser).toHaveBeenCalledWith(submittedUser, 3);
    expect(service.createDragonBallUser).not.toHaveBeenCalled();
  });

  it('handles an error creating a user', async () => {
    const service = makeService({ createDragonBallUser: vi.fn(() => Promise.reject({ data: { message: 'nope' }, status: 409 })) });
    const { mock } = await createController(service);

    context.submit();
    await flush();

    expect(mock.logger.error).toHaveBeenCalledWith('Error while creating DragonBallUser : nope', null);
    expect(location.path).toHaveBeenCalledWith('/409');
  });

  it('handles an error updating a user', async () => {
    const service = makeService({ updateDragonBallUser: vi.fn(() => Promise.reject({ data: { message: 'conflict' }, status: 409 })) });
    const { mock } = await createController(service);

    context.user = { id: 4, username: 'vegeta' };
    context.submit();
    await flush();

    expect(mock.logger.error).toHaveBeenCalledWith('Error while updating DragonBallUser : conflict', null);
    expect(location.path).toHaveBeenCalledWith('/409');
  });

  it('edits an existing user', async () => {
    const service = makeService();
    await createController(service);

    context.users = [{ id: 1, username: 'goku' }, { id: 2, username: 'gohan' }];
    context.edit(2);

    expect(context.user).toEqual({ id: 2, username: 'gohan' });
  });

  it('does nothing when editing a missing user', async () => {
    const service = makeService();
    await createController(service);

    context.users = [{ id: 1, username: 'goku' }];
    const current = context.user;
    context.edit(99);

    expect(context.user).toBe(current);
  });

  it('removes the user currently being edited and resets the form', async () => {
    const service = makeService();
    await createController(service);

    context.user = { id: 5, username: 'trunks' };
    context.remove(5);
    await flush();

    expect(scope.myForm.$setPristine).toHaveBeenCalled();
    expect(service.deleteDragonBallUser).toHaveBeenCalledWith(5);
  });

  it('removes a user that is not currently being edited', async () => {
    const service = makeService();
    await createController(service);

    context.user = { id: 6, username: 'piccolo' };
    context.remove(7);
    await flush();

    expect(scope.myForm.$setPristine).not.toHaveBeenCalled();
    expect(service.deleteDragonBallUser).toHaveBeenCalledWith(7);
  });

  it('handles an error deleting a user', async () => {
    const service = makeService({ deleteDragonBallUser: vi.fn(() => Promise.reject({ data: { message: 'not found' }, status: 404 })) });
    const { mock } = await createController(service);

    context.remove(8);
    await flush();

    expect(mock.logger.error).toHaveBeenCalledWith('Error while deleting DragonBallUser : not found', null);
    expect(location.path).toHaveBeenCalledWith('/404');
  });

  it('creates a working back button', async () => {
    const errResponse = { data: { message: 'bad' }, status: 500 };
    const service = makeService({ fetchAllDragonBallUsers: vi.fn(() => Promise.reject(errResponse)) });
    const { mock } = await createController(service);

    expect(backButtonOptions.attr.class).toBe('btn go-back-btn-kh');
    backButtonOptions.click();
    expect((global as any).history.back).toHaveBeenCalled();
    expect(mock.plugin.modal.basicModal.close).toHaveBeenCalled();
  });
});
