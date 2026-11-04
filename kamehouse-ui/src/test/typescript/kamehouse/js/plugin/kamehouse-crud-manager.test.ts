/// <reference types="vitest/globals" />

import { describe, it, expect, vi, beforeEach } from 'vitest';
import { installKameHouseMock } from '../../../helpers/kamehouse-mock';

function flush() {
  return new Promise((resolve) => setTimeout(resolve, 0));
}

describe('kamehouse-crud-manager', () => {
  let mock: any;
  let elements: any;
  let arrays: any;
  let classFilters: any;
  let columnFilters: any;
  let showNoData: boolean;
  let buttons: any[];
  let inputs: any[];
  let debuggerGet: any;
  let debuggerPost: any;
  let debuggerPut: any;
  let debuggerDelete: any;

  function makeElement(id: string, extra: any = {}) {
    const element: any = {
      id,
      value: '',
      checked: false,
      name: '',
      type: 'text',
      dataset: {},
      options: [],
      selectedIndex: -1,
      attributes: [],
      style: {},
      classList: { add: vi.fn(), remove: vi.fn(), contains: vi.fn(() => false) },
      parentNode: { insertBefore: vi.fn(), removeChild: vi.fn() },
      nextSibling: { id: 'next' },
      previousSibling: { id: 'prev' },
      appendChild: vi.fn(),
      removeChild: vi.fn(),
      insertBefore: vi.fn(),
      setAttribute: vi.fn(),
      addEventListener: vi.fn(),
      ...extra
    };
    return element;
  }

  function getElement(id: string) {
    if (!elements[id]) {
      elements[id] = makeElement(id);
    }
    return elements[id];
  }

  function install() {
    elements = {};
    arrays = {};
    classFilters = [];
    columnFilters = [];
    showNoData = false;
    buttons = [];
    inputs = [];
    debuggerGet = vi.fn();
    debuggerPost = vi.fn();
    debuggerPut = vi.fn();
    debuggerDelete = vi.fn();

    const doc: any = {
      title: 'KameHouse',
      readyState: 'complete',
      addEventListener: vi.fn(),
      createElement: vi.fn((tag: string) => makeElement(tag)),
      querySelector: vi.fn(() => makeElement('query')),
      querySelectorAll: vi.fn(() => []),
      getElementById: vi.fn((id: string) => {
        if (id === 'no-data-from-backend-row' && !showNoData) return null;
        return getElement(id);
      }),
      getElementsByClassName: vi.fn((name: string) => {
        if (name === 'crud-manager-filter') return classFilters;
        if (name === 'crud-manager-column-filter') return columnFilters;
        return [];
      }),
      getElementsByName: vi.fn((name: string) => arrays[name] || []),
      getElementsByTagName: vi.fn(() => []),
      getElementsByNameAll: vi.fn(() => []),
      body: makeElement('body'),
      head: makeElement('head'),
      cookie: ''
    };

    mock = installKameHouseMock({
      plugin: {
        debugger: { http: { get: debuggerGet, post: debuggerPost, put: debuggerPut, delete: debuggerDelete } },
        modal: {
          basicModal: {
            openApiError: vi.fn(),
            openAutoCloseable: vi.fn(),
            setHtml: vi.fn(),
            appendHtml: vi.fn(),
            open: vi.fn(),
            close: vi.fn()
          }
        }
      },
      core: {
        isEmpty: (val: any) => val === undefined || val === null || val === '',
        isFunction: (f: any) => typeof f === 'function',
        configDynamicHtml: vi.fn()
      },
      util: {
        dom: {
          getHead: vi.fn(() => makeElement('head')),
          getImg: vi.fn((attrs: any) => ({ attrs })),
          getSpan: vi.fn(() => makeElement('span')),
          getBr: vi.fn(() => makeElement('br')),
          getTd: vi.fn((attrs: any, content: any) => makeElement('td', { attrs, content })),
          getTr: vi.fn(() => makeElement('tr')),
          getTrTd: vi.fn((content: any) => makeElement('td', { content })),
          getTbody: vi.fn((attrs: any) => makeElement('tbody', { attrs })),
          getLabel: vi.fn((attrs: any, text: any) => makeElement('label', { text })),
          getButton: vi.fn((options: any) => { buttons.push(options); return options; }),
          getInput: vi.fn((options: any) => { const input = makeElement('input', options); inputs.push(input); return input; }),
          getSelect: vi.fn((options: any) => makeElement('select', options)),
          getOption: vi.fn((options: any, text: any) => makeElement('option', { ...options, text })),
          getTextArea: vi.fn((options: any) => makeElement('textarea', options)),
          append: vi.fn(),
          empty: vi.fn(),
          setHtml: vi.fn(),
          setHtmlById: vi.fn(),
          setValue: vi.fn(),
          setValueById: vi.fn(),
          setId: vi.fn(),
          setAttribute: vi.fn(),
          setClick: vi.fn((el: any, event: any, handler: any) => { if (handler) handler(); }),
          classListAdd: vi.fn(),
          classListAddById: vi.fn(),
          classListRemoveById: vi.fn(),
          loadById: vi.fn((id: any, url: any, cb: any) => { if (cb) cb(); }),
          cloneNode: vi.fn((node: any) => makeElement(node && node.id ? node.id + '-clone' : 'clone', { name: node ? node.name : '', attributes: [] })),
          insertBefore: vi.fn(),
          removeChild: vi.fn(),
          remove: vi.fn(),
          replaceWith: vi.fn()
        },
        table: {
          filterTableRows: vi.fn(),
          filterTableRowsByColumn: vi.fn(),
          limitRows: vi.fn(),
          sortTable: vi.fn((table: any, col: any, type: any, direction: any, cb: any) => { if (cb) cb(); })
        },
        tab: {
          openTab: vi.fn(),
          openTabFromCookies: vi.fn()
        },
        time: {
          getDateFromEpoch: vi.fn((value: any) => value === 'invalid' ? null : new Date('2020-01-02T03:04:05Z')),
          isValidDate: vi.fn((date: any) => date instanceof Date),
          getDateWithTimezoneOffset: vi.fn((date: any) => date),
          getTimestamp: vi.fn(() => 'TS')
        },
        banner: { setRandomAllBanner: vi.fn() },
        module: {
          setModuleLoaded: vi.fn(),
          waitForModules: vi.fn((modules: any, cb: any) => { if (cb) cb(); })
        }
      }
    }, {
      window: { location: { search: '?search=abc' } },
      document: doc
    });
    return mock;
  }

  const columns = [
    { name: 'id', type: 'id' },
    { name: 'name', type: 'text' },
    { name: 'email', type: 'email' },
    { name: 'password', type: 'password' },
    { name: 'secret', type: 'hidden' },
    { name: 'age', type: 'number', min: 0, max: 100 },
    { name: 'active', type: 'boolean' },
    { name: 'birth', type: 'date' },
    { name: 'updated', type: 'timestamp' },
    { name: 'startTime', type: 'time' },
    { name: 'role', type: 'select', values: ['ADMIN', 'USER'], displayValues: ['Admin', 'User'], sortType: 'text' },
    {
      name: 'roles', type: 'array', arrayType: 'select', values: ['ADMIN'], displayValues: ['Admin'],
      buildListDisplay: (a: any) => 'roles:' + a.length,
      buildFormField: (source: any, element: any) => ({ form: element }),
      buildEntity: (element: any) => ({ value: element.value })
    },
    { name: 'permissions', type: 'array', arrayType: 'select', values: ['READ'], displayValues: ['Read'], buildListDisplay: (a: any) => a },
    { name: 'tags', type: 'array', arrayType: 'object' },
    { name: 'address', type: 'object', columns: [{ name: 'city', type: 'text' }, { name: 'zip', type: 'number' }] }
  ];

  function config(overrides: any = {}) {
    return {
      entityName: 'TennisPlayer',
      entityNameJapanese: 'テニス',
      banner: 'banner-fuji',
      icon: '/kame-house/img/icon.png',
      infoImage: { img: '/banner.jpg', title: 'Join', titlePosition: 'TOP', desc: 'desc', isReverse: true },
      url: '/api/players',
      readOnly: false,
      reverseDataOrder: false,
      defaultSorting: { columnNumber: 0, sortType: 'text', direction: 'asc' },
      readAll: { maxRows: 100, sortColumn: 'id', sortAscending: false },
      customListSection: '/custom-section.html',
      columns,
      ...overrides
    };
  }

  async function importCrud() {
    vi.resetModules();
    install();
    await import('@/kamehouse/js/plugin/kamehouse-crud-manager');
    await flush();
    return mock.plugin.crudManager;
  }

  function lastCall(spy: any) {
    return spy.mock.calls[spy.mock.calls.length - 1];
  }

  it('loads the crud manager plugin', async () => {
    const crud = await importCrud();
    expect(crud).toBeDefined();
    expect(mock.logger.info).toHaveBeenCalledWith('Started initializing crudManager', null);
    expect(mock.util.dom.append).toHaveBeenCalledWith(expect.anything(), expect.stringContaining('kamehouse-crud-manager.css'));
    expect(mock.util.module.setModuleLoaded).toHaveBeenCalledWith('crudManager');
    expect(mock.util.banner.setRandomAllBanner).toHaveBeenCalledWith(null);
    expect(mock.core.configDynamicHtml).toHaveBeenCalled();
  });

  it('initializes the crud manager and renders entities', async () => {
    const crud = await importCrud();
    crud.init(config());

    expect(mock.util.dom.classListRemoveById).toHaveBeenCalledWith('banner', 'banner-goku-ssj4-earth');
    expect(mock.util.dom.classListAddById).toHaveBeenCalledWith('banner', 'banner-fuji');
    expect(mock.util.dom.classListRemoveById).toHaveBeenCalledWith('crud-info-image', 'hidden-kh');
    expect(mock.util.dom.classListAddById).toHaveBeenCalledWith('info-image-title', 'info-image-title-top');
    expect(mock.util.dom.classListAddById).toHaveBeenCalledWith('crud-info-image', 'info-image-table-reverse');
    expect(mock.util.dom.setValueById).toHaveBeenCalledWith('search-filter', 'abc');
    expect(mock.util.tab.openTabFromCookies).toHaveBeenCalledWith('kh-crud-manager', 'tab-list');
    expect(mock.util.dom.loadById).toHaveBeenCalledWith('custom-list-section', '/custom-section.html', null);
    expect(debuggerGet).toHaveBeenCalled();

    const readAllCall = lastCall(debuggerGet);
    expect(readAllCall[1]).toBe('/api/players');
    expect(readAllCall[3]).toEqual({ maxRows: 100, sortColumn: 'id', sortAscending: false });

    readAllCall[4]([
      {
        id: 1, name: 'Ryoma', email: 'r@x.com', password: 'p', secret: 's', age: 12,
        active: true, birth: '2020-01-02', updated: '2020-01-02', startTime: '10:00',
        role: 'ADMIN', roles: ['ADMIN'], permissions: ['READ'], tags: [{ a: 1 }],
        address: { city: 'Tokyo', zip: 1 }
      },
      {
        id: 2, name: 'Fuji', email: 'f@x.com', password: 'p', secret: 's', age: 13,
        active: false, birth: 'invalid', updated: 'invalid', startTime: '11:00',
        role: 'USER', roles: [], permissions: [], tags: [],
        address: { city: 'Osaka', zip: 2 }
      }
    ], 200, 'OK', {});

    expect(mock.logger.info).toHaveBeenCalledWith('Received 2 entities from the backend', null);
    expect(mock.util.table.sortTable).toHaveBeenCalledWith('crud-manager-table', 0, 'text', 'asc', expect.any(Function));
    expect(mock.util.table.filterTableRows).toHaveBeenCalled();
    expect(mock.util.table.limitRows).toHaveBeenCalledWith('crud-manager-table', '', true);

    const editButton = buttons.find((b) => b.backgroundImg && b.backgroundImg.includes('edit.png'));
    editButton.click({}, { id: 1 });
    expect(mock.util.tab.openTab).toHaveBeenCalledWith('tab-edit', 'kh-crud-manager');
    const readCall = lastCall(debuggerGet);
    expect(readCall[1]).toBe('/api/players/1');
    readCall[4]({ id: 1, name: 'Ryoma', roles: [], permissions: [], tags: [], address: { city: 'Tokyo' } }, 200, 'OK', {});
    expect(mock.util.dom.setValue).toHaveBeenCalled();

    const confirmDelete = buttons.find((b) => b.backgroundImg && b.backgroundImg.includes('cancel.png'));
    confirmDelete.click({}, { id: 1 });
    expect(mock.plugin.modal.basicModal.open).toHaveBeenCalled();

    const yes = buttons.find((b) => b.html === 'Yes');
    yes.click({}, { id: 1 });
    const deleteCall = lastCall(debuggerDelete);
    expect(deleteCall[1]).toBe('/api/players/1');
    deleteCall[4]({ id: 1 }, 200, 'OK', {});
    expect(mock.plugin.modal.basicModal.close).toHaveBeenCalled();
  });

  it('handles read, readAll and mutation errors', async () => {
    const crud = await importCrud();
    crud.init(config());
    const readAllCall = lastCall(debuggerGet);
    readAllCall[5]('err', 500, 'ERR', {});
    expect(mock.plugin.modal.basicModal.openApiError).toHaveBeenCalled();
    expect(mock.logger.error).toHaveBeenCalledWith('Error reading all entities', null);

    crud.read(1);
    lastCall(debuggerGet)[5]('err', 500, 'ERR', {});
    expect(mock.logger.error).toHaveBeenCalledWith('Error reading entity', null);

    crud.create();
    lastCall(debuggerPost)[5]('err', 500, 'ERR', {});
    expect(mock.logger.error).toHaveBeenCalledWith('Error creating entity', null);

    crud.update();
    lastCall(debuggerPut)[5]('err', 500, 'ERR', {});
    expect(mock.logger.error).toHaveBeenCalledWith('Error updating entity', null);

    crud.delete({}, { id: 1 });
    lastCall(debuggerDelete)[5]('err', 500, 'ERR', {});
    expect(mock.logger.error).toHaveBeenCalledWith('Error deleting entity', null);
  });

  it('creates and updates entities', async () => {
    const crud = await importCrud();
    crud.init(config());
    getElement('crud-add-input-fields-active').checked = true;
    getElement('crud-add-input-fields-id').value = '1';
    arrays['crud-add-input-fields-roles[]'] = [{ value: 'ADMIN' }, { value: '' }];
    arrays['crud-add-input-fields-tags[]'] = [{ value: '{"a":1}' }];

    crud.create();
    const createCall = lastCall(debuggerPost);
    expect(createCall[3].active).toBe(true);
    expect(createCall[3].roles).toEqual([{ value: 'ADMIN' }]);
    expect(createCall[3].tags).toEqual([{ a: 1 }]);
    createCall[4](99, 201, 'Created', {});
    expect(mock.util.tab.openTab).toHaveBeenCalledWith('tab-list', 'kh-crud-manager');

    getElement('crud-edit-input-fields-id').value = '5';
    crud.update();
    const updateCall = lastCall(debuggerPut);
    expect(updateCall[1]).toBe('/api/players/5');
    updateCall[4](5, 200, 'OK', {});
  });

  it('sets read-only mode and skips mutations', async () => {
    const crud = await importCrud();
    crud.init(config({ readOnly: true }));
    expect(mock.util.dom.classListAddById).toHaveBeenCalledWith('crud-manager-tabs', 'hidden-kh');
    expect(mock.util.tab.openTab).toHaveBeenCalledWith('tab-list', 'kh-crud-manager');

    debuggerPost.mockClear();
    debuggerPut.mockClear();
    debuggerDelete.mockClear();
    crud.create();
    crud.update();
    crud.delete({}, { id: 1 });
    expect(mock.plugin.modal.basicModal.openAutoCloseable).toHaveBeenCalledTimes(3);
    expect(debuggerPost).not.toHaveBeenCalled();
    expect(debuggerPut).not.toHaveBeenCalled();
    expect(debuggerDelete).not.toHaveBeenCalled();
  });

  it('filters and refreshes the view', async () => {
    const crud = await importCrud();
    crud.init(config({ reverseDataOrder: true, defaultSorting: null }));
    lastCall(debuggerGet)[4]([{ id: 1, name: 'a', roles: [], tags: [], permissions: [], address: {} }], 200, 'OK', {});

    classFilters = [{ value: 'a' }, { value: 'b' }];
    columnFilters = [{ value: 'c', dataset: { columnNumber: '2' } }];
    getElement('num-rows').value = '10';
    crud.filterRows();
    expect(mock.util.table.filterTableRows).toHaveBeenCalledWith('a', 'crud-manager-tbody', null, true);
    expect(mock.util.table.filterTableRowsByColumn).toHaveBeenCalledWith('c', 'crud-manager-tbody', '2', null, true);
    expect(mock.util.table.limitRows).toHaveBeenCalledWith('crud-manager-table', '10', true);

    showNoData = true;
    mock.util.table.filterTableRows.mockClear();
    crud.filterRows();
    expect(mock.logger.info).toHaveBeenCalledWith('No data received from the backend, skipping filters', null);

    crud.refreshView();
    expect(mock.util.dom.setValueById).toHaveBeenCalledWith('num-rows', '');

    crud.clearForm('crud-edit-input-fields');
    expect(mock.util.dom.empty).toHaveBeenCalledWith(expect.anything());
  });

  it('handles empty entities and the no data row', async () => {
    const crud = await importCrud();
    crud.init(config());
    lastCall(debuggerGet)[4]([], 200, 'OK', {});
    expect(mock.logger.info).toHaveBeenCalledWith('No data received from the backend', null);
    expect(mock.util.dom.setAttribute).toHaveBeenCalledWith(expect.anything(), 'id', 'no-data-from-backend-row');

    mock.util.dom.empty.mockClear();
    lastCall(debuggerGet)[5]('err', 500, 'ERR', {});
    expect(mock.util.dom.append).toHaveBeenCalledWith(expect.anything(), expect.anything());
  });

  it('adds and removes array input fields', async () => {
    const crud = await importCrud();
    crud.init(config());
    lastCall(debuggerGet)[4]([], 200, 'OK', {});

    const addButton = buttons.find((b) => b.attr && b.attr.id && b.attr.id.endsWith('-add'));
    const arrayName = 'crud-add-input-fields-roles[]';
    const previousSibling = makeElement('array-source', { name: arrayName });
    getElement(addButton.attr.id).previousSibling = previousSibling;
    addButton.click();
    expect(mock.util.dom.insertBefore).toHaveBeenCalled();

    previousSibling.name = 'wrong-name';
    addButton.click();
    expect(mock.logger.error).toHaveBeenCalledWith(expect.stringContaining("Trying to clone a node that isn't an array element"), null);

    const removeButton = buttons.find((b) => b.attr && b.attr.id && b.attr.id.endsWith('-remove'));
    const nodeToRemove = makeElement('node', { name: arrayName });
    getElement(removeButton.attr.id).previousSibling = { previousSibling: nodeToRemove };
    arrays[arrayName] = [{}, {}];
    removeButton.click();
    expect(mock.util.dom.remove).toHaveBeenCalledWith(nodeToRemove);

    arrays[arrayName] = [{}];
    removeButton.click();
    expect(mock.logger.warn).toHaveBeenCalledWith('Trying to remove the last node of the array. Skipping...', null);
  });

  it('toggles password visibility fields', async () => {
    const crud = await importCrud();
    crud.init(config());
    lastCall(debuggerGet)[4]([], 200, 'OK', {});
    const passwordField = getElement('crud-add-input-fields-password');
    const passwordCheckbox = inputs.find((i) => i.type === 'checkbox' && i.class && i.class.includes('m-7-d-kh'));
    expect(passwordCheckbox).toBeDefined();

    const toggleHandler = mock.util.dom.setClick.mock.calls
      .map((c) => c[2])
      .find((h) => typeof h === 'function');
    mock.util.dom.setAttribute.mockClear();
    passwordField.type = 'password';
    toggleHandler();
    expect(mock.util.dom.setAttribute).toHaveBeenCalledWith(passwordField, 'type', 'text');

    passwordField.type = 'text';
    toggleHandler();
    expect(mock.util.dom.setAttribute).toHaveBeenCalledWith(passwordField, 'type', 'password');
  });
});
