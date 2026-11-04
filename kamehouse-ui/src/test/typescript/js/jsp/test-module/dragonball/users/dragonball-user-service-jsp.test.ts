/// <reference types="vitest/globals" />

import { describe, it, expect, vi, beforeEach } from 'vitest';
import { installKameHouseMock, createDocumentMock } from '../../../../../helpers/kamehouse-mock';

function flush() {
  return new Promise((resolve) => setTimeout(resolve, 0));
}

describe('dragonball-user-service-jsp', () => {
  let get: any;
  let post: any;
  let put: any;
  let del: any;
  let buttons: any[];

  beforeEach(() => {
    vi.resetModules();
    buttons = [];
    get = vi.fn();
    post = vi.fn();
    put = vi.fn();
    del = vi.fn();
  });

  function install(search = '?username=goku') {
    const elements: any = {
      'dragonball-users-tbody': {},
      'input-id': { value: '' },
      'input-username': { value: 'goku' },
      'input-email': { value: 'goku@kh.com' },
      'input-age': { value: '30' },
      'input-powerLevel': { value: '9000' },
      'input-stamina': { value: '100' }
    };
    return installKameHouseMock({
      core: { windowLocation: vi.fn() },
      plugin: {
        debugger: { http: { get, post, put, delete: del } },
        modal: { basicModal: { open: vi.fn() } }
      },
      util: {
        dom: {
          getButton: vi.fn((options: any) => { buttons.push(options); return {}; })
        }
      }
    }, {
      window: { location: { search } },
      document: createDocumentMock(elements)
    });
  }

  async function importService(search?: string) {
    const mock = install(search);
    await import('@/js/jsp/test-module/dragonball/users/dragonball-user-service-jsp');
    return { mock, service: mock.extension.dragonBallUserServiceJsp };
  }

  it('loads the service and marks the module as loaded', async () => {
    const { mock, service } = await importService();

    expect(service).toBeDefined();
    expect(mock.logger.info).toHaveBeenCalledWith('Loading DragonBallUserServiceJsp', null);
    expect(mock.util.module.waitForModules).toHaveBeenCalledWith(['kameHouseModal', 'kameHouseDebugger'], expect.any(Function));
    expect(mock.util.module.setModuleLoaded).toHaveBeenCalledWith('dragonBallUserServiceJsp');
  });

  it('gets a dragonball user and populates the edit form', async () => {
    const { mock, service } = await importService('?username=goku');

    service.getDragonBallUser({});

    expect(get).toHaveBeenCalledTimes(1);
    const call = get.mock.calls[0];
    expect(call[1]).toBe('/kame-house-testmodule/api/v1/servlet/test-module/dragonball/users');
    expect(call[3]).toEqual({ username: 'goku' });

    const elements: any = (global as any).document.getElementById('input-id');
    call[4]({ id: 1, username: 'goku', email: 'e', age: 10, powerLevel: 20, stamina: 30 }, 200, '', {});
    expect(elements.value).toBe(1);
    expect(mock.util.dom.empty).toBeDefined();
  });

  it('handles an error getting a dragonball user', async () => {
    const { mock, service } = await importService();

    service.getDragonBallUser({});
    get.mock.calls[0][5](JSON.stringify({ message: 'not found' }), 404, 'Not Found', {});

    expect(mock.plugin.modal.basicModal.open).toHaveBeenCalledWith('Error getting dragonball user : not found');
    expect(mock.logger.error).toHaveBeenCalledWith('Error getting dragonball user : not found', null);
  });

  it('displays all dragonball users and wires the action buttons', async () => {
    const { mock, service } = await importService();

    service.getAllDragonBallUsers();
    get.mock.calls[0][4]([{ id: 1, username: 'goku', email: 'e', age: 10, powerLevel: 20, stamina: 30 }], 200, '', {});
    await flush();

    expect(mock.util.dom.empty).toHaveBeenCalled();
    expect(mock.util.fetch.loadHtmlSnippet).toHaveBeenCalledWith('/kame-house/html-snippets/test-module/dragonball-users-table-header.html');
    expect(mock.util.dom.append).toHaveBeenCalled();
    expect(buttons.length).toBe(2);

    buttons[0].click({}, null);
    expect(mock.core.windowLocation).toHaveBeenCalledWith('users-edit?username=goku');

    buttons[1].click({}, null);
    expect(del).toHaveBeenCalledTimes(1);
  });

  it('handles an error getting all dragonball users', async () => {
    const { mock, service } = await importService();

    service.getAllDragonBallUsers();
    get.mock.calls[0][5](JSON.stringify({ message: 'boom' }), 500, 'Server Error', {});

    expect(mock.util.dom.append).toHaveBeenCalled();
    expect(mock.plugin.modal.basicModal.open).toHaveBeenCalledWith('Error getting dragonball users from the backend : boom');
  });

  it('adds a dragonball user', async () => {
    const { mock, service } = await importService();

    service.addDragonBallUser();
    expect(post).toHaveBeenCalledTimes(1);
    const call = post.mock.calls[0];
    expect(call[3]).toEqual({ username: 'goku', email: 'goku@kh.com', age: '30', powerLevel: '9000', stamina: '100' });
    call[4]('', 200, '', {});
    expect(mock.core.windowLocation).toHaveBeenCalledWith('users-list');
  });

  it('handles an error adding a dragonball user', async () => {
    const { mock, service } = await importService();

    service.addDragonBallUser();
    post.mock.calls[0][5]('', 500, 'Server Error', {});
    expect(mock.plugin.modal.basicModal.open).toHaveBeenCalledWith('Error adding dragonball user');
  });

  it('updates a dragonball user', async () => {
    const { mock, service } = await importService();
    (global as any).document.getElementById('input-id').value = '5';

    service.updateDragonBallUser();
    const call = put.mock.calls[0];
    expect(call[3].id).toBe('5');
    call[4]('', 200, '', {});
    expect(mock.core.windowLocation).toHaveBeenCalledWith('users-list');
  });

  it('handles an error updating a dragonball user', async () => {
    const { mock, service } = await importService();

    service.updateDragonBallUser();
    put.mock.calls[0][5]('', 500, 'Server Error', {});
    expect(mock.plugin.modal.basicModal.open).toHaveBeenCalledWith('Error updating dragonball user');
  });

  it('deletes a dragonball user and reloads the list', async () => {
    const { service } = await importService();

    service.deleteDragonBallUser(7);
    const call = del.mock.calls[0];
    expect(call[3]).toEqual({ id: 7 });
    call[4]('', 200, '', {});
    expect(get).toHaveBeenCalledTimes(1);
  });

  it('handles an error deleting a dragonball user', async () => {
    const { mock, service } = await importService();

    service.deleteDragonBallUser(7);
    del.mock.calls[0][5]('', 500, 'Server Error', {});
    expect(mock.plugin.modal.basicModal.open).toHaveBeenCalledWith('Error deleting dragonball user');
  });

  it('logs an error when the error body is not parsable', async () => {
    const { mock, service } = await importService();

    service.getAllDragonBallUsers();
    get.mock.calls[0][5]('not-json', 500, 'Server Error', {});

    expect(mock.logger.error).toHaveBeenCalledWith(expect.stringContaining('Error parsing api error response body.'), null);
    expect(mock.plugin.modal.basicModal.open).toHaveBeenCalledWith('Error getting dragonball users from the backend');
  });
});
