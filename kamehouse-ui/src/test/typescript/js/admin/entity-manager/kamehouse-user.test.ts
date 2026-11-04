/// <reference types="vitest/globals" />

import { describe, it, expect, vi, beforeEach } from 'vitest';
import { installKameHouseMock, createDocumentMock } from '../../../helpers/kamehouse-mock';

describe('admin entity-manager kamehouse-user', () => {
  beforeEach(() => {
    vi.resetModules();
  });

  function install() {
    return installKameHouseMock({
      plugin: {
        crudManager: { init: vi.fn() }
      },
      util: {
        dom: {
          cloneNode: vi.fn((node: any) => node)
        }
      }
    }, {
      window: { location: { search: '' } },
      document: createDocumentMock()
    });
  }

  function getManager(mock: any) {
    return mock.extension.kameHouseUserCrudManager;
  }

  it('initializes the crud manager configuration and extension', async () => {
    const mock = install();
    await import('@/js/admin/entity-manager/kamehouse-user');

    expect(mock.plugin.crudManager.init).toHaveBeenCalledTimes(1);
    const config = mock.plugin.crudManager.init.mock.calls[0][0];
    expect(config.entityName).toBe('KameHouse User');
    expect(config.url).toBe('/kame-house-admin/api/v1/admin/kamehouse/users');
    expect(config.columns.find((c: any) => c.name === 'authorities')).toBeDefined();
    expect(mock.logger.info).toHaveBeenCalledWith('Started initializing kameHouseUserCrudManager', null);
  });

  it('builds a roles entity from a selected option', async () => {
    const mock = install();
    await import('@/js/admin/entity-manager/kamehouse-user');
    const manager = getManager(mock);

    const element = {
      options: [{ selected: true, value: 'ROLE_SAIYAJIN' }],
      getAttribute: vi.fn(() => 'role-id-1')
    };
    expect(manager.buildRolesEntity(element)).toEqual({ name: 'ROLE_SAIYAJIN' });
    expect(mock.logger.trace).toHaveBeenCalledWith('Building role from element with data-kamehouse-id: role-id-1', null);
  });

  it('returns null when no role option is selected', async () => {
    const mock = install();
    await import('@/js/admin/entity-manager/kamehouse-user');
    const manager = getManager(mock);

    const element = { options: [{ selected: false, value: 'ROLE_SAIYAJIN' }], getAttribute: vi.fn(() => null) };
    expect(manager.buildRolesEntity(element)).toBeNull();
    expect(mock.logger.warn).toHaveBeenCalledWith('Unable to build kamehouse user role', null);
  });

  it('builds a roles form field', async () => {
    const mock = install();
    await import('@/js/admin/entity-manager/kamehouse-user');
    const manager = getManager(mock);

    const base = { options: [{ value: 'ROLE_A' }, { value: 'ROLE_B' }] };
    const field = manager.buildRolesFormField(base, { name: 'ROLE_B', id: 'id-2' });

    expect(mock.util.dom.cloneNode).toHaveBeenCalledWith(base, true);
    expect(mock.util.dom.classListAdd).toHaveBeenCalledWith(field, 'm-5-t-d-kh');
    expect(field.options[0].selected).toBe(false);
    expect(field.options[1].selected).toBe(true);
    expect(mock.util.dom.setAttribute).toHaveBeenCalledWith(field, 'data-kamehouse-id', 'id-2');
  });

  it('builds a roles form field without an id', async () => {
    const mock = install();
    await import('@/js/admin/entity-manager/kamehouse-user');
    const manager = getManager(mock);

    const base = { options: [{ value: 'ROLE_A' }] };
    manager.buildRolesFormField(base, { name: 'ROLE_A' });

    expect(mock.util.dom.setAttribute).toHaveBeenCalledTimes(1);
    expect(mock.util.dom.setAttribute).toHaveBeenCalledWith(base, 'data-kamehouse-id', '');
  });

  it('builds the roles list display', async () => {
    const mock = install();
    await import('@/js/admin/entity-manager/kamehouse-user');
    const manager = getManager(mock);

    expect(manager.buildRolesListDisplay([])).toBe('[]');
    expect(manager.buildRolesListDisplay([{ name: 'ROLE_A' }, { name: 'ROLE_B' }])).toBe('["ROLE_A","ROLE_B"]');
  });

  it('wires the crud manager column callbacks to the extension', async () => {
    const mock = install();
    await import('@/js/admin/entity-manager/kamehouse-user');
    const manager = getManager(mock);
    const config = mock.plugin.crudManager.init.mock.calls[0][0];
    const authorities = config.columns.find((c: any) => c.name === 'authorities');

    const element = { options: [{ selected: true, value: 'ROLE_KAMISAMA' }], getAttribute: vi.fn(() => '') };
    expect(authorities.buildEntity(element)).toEqual({ name: 'ROLE_KAMISAMA' });

    const base = { options: [{ value: 'ROLE_KAMISAMA' }] };
    expect(authorities.buildFormField(base, { name: 'ROLE_KAMISAMA' })).toBe(base);

    expect(authorities.buildListDisplay([{ name: 'ROLE_KAMISAMA' }])).toBe('["ROLE_KAMISAMA"]');
  });
});
