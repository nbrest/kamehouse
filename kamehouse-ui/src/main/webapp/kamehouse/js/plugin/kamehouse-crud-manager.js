/**
 * Functionality to manage CRUD operations on any entity in the backend.
 */
function CrudManager() {

  this.load = load;
  this.init = init;

  this.read = read;
  this.readAll = readAll;
  this.create = create;
  this.update = update;
  this.delete = deleteEntity;

  this.clearForm = clearForm;
  this.filterRows = filterRows;
  this.refreshView = refreshView; 

  const TBODY_ID = "crud-manager-tbody";
  const ADD_INPUT_FIELDS_ID = "crud-add-input-fields";
  const EDIT_INPUT_FIELDS_ID = "crud-edit-input-fields";
  const DEFAULT_BANNER = "banner-goku-ssj4-earth";
  const NO_DATA_ROW_ID = "no-data-from-backend-row";

  let entityName = "Set EntityName";
  let entityNameJapanese = null;
  let icon = null;
  let url = "/kame-house-module/api/v1/override-url";
  let columns = [];
  let entities = [];
  let readOnly = false;
  let reverseDataOrder = false;
  let defaultSorting = null;
  let readAllMaxRows = null;
  let readAllSortColumn = null;
  let readAllSortAscending = null;
  
  /**
   * Load the crud manager module.
   */
  function load() {
    kameHouse.logger.info("Started initializing crudManager");
    kameHouse.util.dom.append($('head'), '<link rel="stylesheet" type="text/css" href="/kame-house/kamehouse/css/plugin/kamehouse-crud-manager.css">');
    kameHouse.util.dom.load($("#crud-manager-body-wrapper"), "/kame-house/kamehouse/html/plugin/kamehouse-crud-manager.html", () => {
      kameHouse.util.module.setModuleLoaded("crudManager");
      kameHouse.util.banner.setRandomAllBanner();
    });
  }

  /**
   * Initialize the crud manager. Configuration object is: 
   * {
   *    entityName: "EntityName",
   *    entityNameJapanese: "かめはうす",
   *    url: "/kame-house-module/etc",
   *    banner: "banner-fuji",
   *    icon: "/kame-house/img/prince-of-tennis/fuji-icon.png",
   *    infoImage: {
   *      img: "/kame-house/img/banners/prince-of-tennis/banner-seigaku.jpg",
   *      title: "Join Tennis World?",
   *      titlePosition: "[top|bottom]", // default is center if not set
   *      desc: "Echizen Ryoma, Fuji Syuske, Tezuka Kunimitzu are already waiting for you",
   *      isReverse: true
   *    },
   *    readOnly: true,
   *    readAll: {
   *      maxRows: 200,
   *      sortColumn: "id",
   *      sortAscending: false,
   *    },
   *    reverseDataOrder: true, // Data is usually received by id asc, if my initial view is desc by id, set this to true. I can also request the data from the backend sorted desc by id with readAll.sortColumn and readAll.sortAscending configs.
   *    defaultSorting: {
   *     columnNumber: 11, // Column number starts with 0
   *     sortType: "timestamp",
   *     direction: "desc"
   *    },
   *    columns: [ 
   *      {
   *        name: "columnName",
   *        type: "columnType",
   *        values: ["val1", "val2"],
   *        displayValues: ["dispVal1", "dispVal2"],
   *        sortType: "number"
   *      },
   *      { 
   *        name: "columnName",
   *        type: "array",
   *        arrayType: "select",
   *        values: [ "VAL1", "VAL2" ],
   *        displayValues: [ "DIS1", "DIS2" ],
   *        buildEntity: (element) => { 
   *          // function to customize building the entity to send to the backend from the form in the select array
   *          return null;
   *        },
   *        buildFormField: (baseSelectElement, entity) => {
   *          // function to customize building the edit form field from the entity received in the backend in the select array
   *          return null;
   *        },
   *        buildListDisplay: (array) => {
   *          // function to customize the way the array is rendered in the list view
   *          return "";
   *        }
   *      },
   *      ...
   *    ]
   * }
   * 
   * - type is a custom definition of a type that I can then map to an input field
   * Types: 
   *  - [ array, boolean, date, email, id, hidden, number, object, password, select, text, time ]
   * 
   * Optional fields:
   * 
   * - arrayType (array)
   * - displayValues (select)
   * - min (number)
   * - max (number)
   * - values (select)
   * - sortType (select)
   */
  function init(config) {
    replaceBanner(config);
    setIcon(config);
    setInfoImage(config);
    setEntityName(config.entityName);
    setEntityNameJapanese(config.entityNameJapanese);
    setUrl(config.url);
    setColumns(config.columns);
    setReadOnly(config.readOnly);
    setDefaultSorting(config.defaultSorting);
    setReverseDataOrder(config.reverseDataOrder);
    setReadAllParameters(config.readAll);
    loadCustomSections(config);
    updateEntityNameInView();
    loadStateFromCookies();
    loadStateFromUrlParams();
    disableEditFunctionalityForReadOnly();
    kameHouse.util.module.waitForModules(["kameHouseModal", "kameHouseDebugger"], () => {
      readAll();
    });
  }

  /**
   * Replace default banner
   */
  function replaceBanner(config) {
    if (!kameHouse.core.isEmpty(config.banner)) {
      kameHouse.util.dom.removeClass($("#banner"), DEFAULT_BANNER);
      kameHouse.util.dom.addClass($("#banner"), config.banner);
    }
  }

  function setIcon(config) {
    if (!kameHouse.core.isEmpty(config.icon)) {
      icon = kameHouse.util.dom.getImgBtn({
        src: config.icon,
        className: "crud-icon",
        alt: "Icon",
        onClick: () => {return;}
      });
    }
  }

  function setInfoImage(config) {
    if (!kameHouse.core.isEmpty(config.infoImage)) {
      kameHouse.util.dom.removeClass($("#crud-info-image"), "hidden-kh");
      const infoImage = config.infoImage;
      const img = document.getElementById("info-image-img");
      kameHouse.util.dom.setAttribute(img, "src", infoImage.img); 
      kameHouse.util.dom.setHtml($("#info-image-title"), infoImage.title);
      if (!kameHouse.core.isEmpty(infoImage.titlePosition)) {
        if (infoImage.titlePosition.toUpperCase() == "TOP") {
          kameHouse.util.dom.addClass($("#info-image-title"), "info-image-title-top");
        }
        if (infoImage.titlePosition.toUpperCase() == "BOTTOM") {
          kameHouse.util.dom.addClass($("#info-image-title"), "info-image-title-bottom");
        }
      }
      kameHouse.util.dom.setHtml($("#info-image-desc"), infoImage.desc);
      if (infoImage.isReverse) {
        kameHouse.util.dom.addClass($("#crud-info-image"), "info-image-table-reverse");
      }
    }
  }

  function getIcon() {
    if (!kameHouse.core.isEmpty(icon)) {
      return kameHouse.util.dom.cloneNode(icon, false);
    }
    return "";
  }

  function getListTitle() {
    const span = kameHouse.util.dom.getSpan();
    kameHouse.util.dom.append(span, getIcon());
    kameHouse.util.dom.append(span, getEntityNames());
    return span;
  }

  function getAddTitle() {
    const span = kameHouse.util.dom.getSpan();
    kameHouse.util.dom.append(span, getIcon());
    kameHouse.util.dom.append(span, "Add " + entityName);
    return span;
  }

  function getEditTitle() {
    const span = kameHouse.util.dom.getSpan();
    kameHouse.util.dom.append(span, getIcon());
    kameHouse.util.dom.append(span, "Edit " + entityName);
    return span;
  }

  /**
   * Updates the view with the entity name.
   */
  function updateEntityNameInView() {
    kameHouse.util.dom.setHtml($("title"), "KameHouse - " + getEntityNames());
    kameHouse.util.dom.setHtml($("#crud-manager-banner-title"), getBannerTitle());
    kameHouse.util.dom.setHtml($("#crud-manager-list-title"), getListTitle());
    kameHouse.util.dom.setHtml($("#crud-manager-add-title"), getAddTitle());
    kameHouse.util.dom.setHtml($("#crud-manager-edit-title"), getEditTitle());
  }

  function getBannerTitle() {
    if (kameHouse.core.isEmpty(entityNameJapanese)) {
      return getEntityNames();
    }
    return entityNameJapanese;
  }

  /**
   * When set to read only, disable add and edit tabs.
   */
  function disableEditFunctionalityForReadOnly() {
    if (readOnly) {
      kameHouse.util.dom.addClass($("#crud-manager-tabs"), "hidden-kh");
      kameHouse.util.dom.addClass($("#tab-add-link"), "hidden-kh");
      kameHouse.util.dom.addClass($("#tab-edit-link"), "hidden-kh");
      kameHouse.util.tab.openTab('tab-list', 'kh-crud-manager');
    }
  }

  /**
   * Get the plural for entityName.
   */
  function getEntityNames() {
    return entityName + "s";
  }

  /**
   * Load the current state from the cookies.
   */
  function loadStateFromCookies() {
    kameHouse.util.tab.openTabFromCookies('kh-crud-manager', 'tab-list');
  }

  /**
   * Load the current state from the url parameters.
   */
  function loadStateFromUrlParams() {
    const urlParams = new URLSearchParams(window.location.search);
    const search = urlParams.get('search');
    if (!kameHouse.core.isEmpty(search)) {
      kameHouse.util.dom.setValue(document.getElementById('search-filter'), search);
    }
  }

  /**
   * Set the name of the entity managed by the crud manager.
   */
  function setEntityName(name) {
    entityName = name;
  }

  /**
   * Set the Japanese name of the entity managed by the crud manager.
   */
  function setEntityNameJapanese(name) {
    entityNameJapanese = name;
  }

  /**
   * Set the crud base url to connect to the backend.
   */
  function setUrl(crudUrl) {
    url = crudUrl;
  }

  /**
   * Set the array of columns to display in the table, excluding the actions column.
   */
  function setColumns(crudColumns) {
    columns = crudColumns;
  }

  /**
   * Set the crud manager as readOnly, to disable updates, only to query data.
   */
  function setReadOnly(crudReadOnly) {
    if (!kameHouse.core.isEmpty(crudReadOnly)) {
      readOnly = crudReadOnly;
    }
  }

  /**
   * Set the default sorting of table data.
   */
  function setDefaultSorting(crudDefaultSorting) {
    if (!kameHouse.core.isEmpty(crudDefaultSorting)) {
      defaultSorting = crudDefaultSorting;
    }
  }

  /**
   * Set the crud manager to reverse the received data's order.
   */
  function setReverseDataOrder(crudReverseDataOrder) {
    if (!kameHouse.core.isEmpty(crudReverseDataOrder)) {
      reverseDataOrder = crudReverseDataOrder;
    }
  }

  function setReadAllParameters(readAllParams) {
    if (kameHouse.core.isEmpty(readAllParams)) {
      return;
    }
    if (!kameHouse.core.isEmpty(readAllParams.maxRows)) {
      readAllMaxRows = readAllParams.maxRows;
    }

    if (!kameHouse.core.isEmpty(readAllParams.sortColumn)) {
      readAllSortColumn = readAllParams.sortColumn;
    }

    if (readAllParams.sortAscending != null && readAllParams.sortAscending != undefined) {
      readAllSortAscending = readAllParams.sortAscending;
    }
  }

  function loadCustomSections(config) {
    if (!kameHouse.core.isEmpty(config.customListSection)) {
      kameHouse.util.dom.load($("#custom-list-section"), config.customListSection);
    }
  }

  /**
   * Get an entity by it's id.
   */
  function read(id) {
    kameHouse.logger.info("read");
    const getUrl = url + "/" + id;
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.get(config, getUrl, null, null,
      (responseBody, responseCode, responseDescription, responseHeaders) => {
        setEditFormValues(responseBody, responseCode, responseDescription, responseHeaders);
      },
      (responseBody, responseCode, responseDescription, responseHeaders) => {
        kameHouse.logger.logApiError(responseBody, responseCode, responseDescription, responseHeaders, "Error getting entity");
        kameHouse.plugin.modal.basicModal.openApiError(responseBody, responseCode, responseDescription, responseHeaders);
      });
  }

  /**
   * Get all entities.
   */
  function readAll() {
    kameHouse.logger.info("readAll");
    const requestParam = {};
    if (readAllMaxRows) {
      requestParam.maxRows = readAllMaxRows;
    }
    if (readAllSortColumn) {
      requestParam.sortColumn = readAllSortColumn;
    }
    if (readAllSortAscending != null && readAllSortAscending != undefined) {
      requestParam.sortAscending = readAllSortAscending;
    }
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.get(config, url, kameHouse.http.getUrlEncodedHeaders(), requestParam,
      (responseBody, responseCode, responseDescription, responseHeaders) => {
        entities = responseBody;
        reloadView();
      },
      (responseBody, responseCode, responseDescription, responseHeaders) => {
        kameHouse.logger.logApiError(responseBody, responseCode, responseDescription, responseHeaders, "Error getting all entities");
        kameHouse.plugin.modal.basicModal.openApiError(responseBody, responseCode, responseDescription, responseHeaders);
        displayErrorGettingEntities();
      });
  }

  /**
   * Create an entity.
   */
  function create() {
    kameHouse.logger.info("create");
    if (readOnly) {
      kameHouse.plugin.modal.basicModal.openAutoCloseable("This crud manager is set to read-only. Can't execute updates", 5000);
      return;
    }
    const entity = getEntityFromForm(ADD_INPUT_FIELDS_ID);
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.post(config, url, kameHouse.http.getApplicationJsonHeaders(), entity,
      (responseBody, responseCode, responseDescription, responseHeaders) => {
        kameHouse.logger.info("Created entity successfully. Id: " + responseBody);
        readAll();
        kameHouse.util.tab.openTab('tab-list', 'kh-crud-manager');
      },
      (responseBody, responseCode, responseDescription, responseHeaders) => {
        kameHouse.logger.logApiError(responseBody, responseCode, responseDescription, responseHeaders, "Error creating entity");
        kameHouse.plugin.modal.basicModal.openApiError(responseBody, responseCode, responseDescription, responseHeaders);
        readAll();
      });
  }

  /**
   * Update an entity.
   */
  function update() {
    kameHouse.logger.info("update");
    if (readOnly) {
      kameHouse.plugin.modal.basicModal.openAutoCloseable("This crud manager is set to read-only. Can't execute updates", 5000);
      return;
    }
    const entity = getEntityFromForm(EDIT_INPUT_FIELDS_ID);
    const updateUrl = url + "/" + entity.id;
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.put(config, updateUrl, kameHouse.http.getApplicationJsonHeaders(), entity,
      (responseBody, responseCode, responseDescription, responseHeaders) => {
        kameHouse.logger.info("Updated entity successfully. Id: " + entity.id);
        readAll();
        kameHouse.util.tab.openTab('tab-list', 'kh-crud-manager');
      },
      (responseBody, responseCode, responseDescription, responseHeaders) => {
        kameHouse.logger.logApiError(responseBody, responseCode, responseDescription, responseHeaders, "Error updating entity");
        kameHouse.plugin.modal.basicModal.openApiError(responseBody, responseCode, responseDescription, responseHeaders);
        readAll();
      });
  }

  /**
   * Delete entity from the server.
   */
  function deleteEntity(event) {
    const id = event.data.id;
    kameHouse.logger.info("deleteEntity");
    if (readOnly) {
      kameHouse.plugin.modal.basicModal.openAutoCloseable("This crud manager is set to read-only. Can't execute updates", 5000);
      return;
    }
    const deleteUrl = url + "/" + id;
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.delete(config, deleteUrl, null, null,
      (responseBody, responseCode, responseDescription, responseHeaders) => {
        kameHouse.logger.info("Deleted entity successfully. Id: " + responseBody.id);
        kameHouse.plugin.modal.basicModal.close();
        readAll();
      },
      (responseBody, responseCode, responseDescription, responseHeaders) => {
        kameHouse.logger.logApiError(responseBody, responseCode, responseDescription, responseHeaders, "Error deleting entity");
        kameHouse.plugin.modal.basicModal.close();
        kameHouse.plugin.modal.basicModal.openApiError(responseBody, responseCode, responseDescription, responseHeaders);
        readAll();
      });
  }

  /**
   * Default setEditFormValues. Override to set custom functionality. 
   * This should usually be used to load the form fields to edit an entity.
   * Probably needs to be overriden if custom columns are set or the forms are loaded from a snippet.
   */
  function setEditFormValues(responseBody, responseCode, responseDescription, responseHeaders) { 
    reloadForm(EDIT_INPUT_FIELDS_ID);
    updateEditFormFieldValues(responseBody, columns, null);
  }

  /**
   * Set the input fields in the edit form from the entity.
   */
  function updateEditFormFieldValues(entity, currentNodeColumns, parentNodeChain) {
    parentNodeChain = initParentNodeChain(parentNodeChain);
    for (const currentNodeColumn of currentNodeColumns) {
      const column = currentNodeColumn;
      const type = column.type;
      const name = column.name;
      if (isObjectField(type)) {
        updateEditFormFieldValues(entity[name], column.columns, parentNodeChain + name);
        continue;
      }
      updateEditFormFieldValue(entity, column, parentNodeChain);
    }
  }

  function updateEditFormFieldValue(entity, column, parentNodeChain) {
    const type = column.type;
    const name = column.name;
    const inputFieldId = EDIT_INPUT_FIELDS_ID + "-" + parentNodeChain + name;
    const inputField = $(document.getElementById(inputFieldId));
    kameHouse.util.dom.setVal(inputField, entity[name]); 

    if (isDateField(type)) {
      kameHouse.util.dom.setVal(inputField, getFormattedDateFieldValue(entity[name]));
    }
    if (isArrayField(type)) {
      updateEditFormFieldArrayValue(entity, column, inputFieldId, inputField);
    }
    if (isBooleanField(type)) {
      if (entity[name]) {
        kameHouse.util.dom.setAttr(inputField, "checked", "true"); 
      }
    }
  }

  function updateEditFormFieldArrayValue(entity, column, inputFieldId, inputField) {
    const name = column.name;
    const arrayType = column.arrayType;
    kameHouse.util.dom.setVal(inputField, null);
    const array = entity[name];
    const arraySourceNode = document.getElementById(inputFieldId);
    let i = 0;
    for (const arrayElement of array) {
      if (arrayType == "select") {
        if (kameHouse.core.isFunction(column.buildFormField)) {
          const formField = column.buildFormField(arraySourceNode, arrayElement);
          if (!kameHouse.core.isEmpty(formField)) {
            kameHouse.util.dom.insertBefore(arraySourceNode.parentNode, formField, arraySourceNode.nextSibling);
          } else {
            kameHouse.logger.warn("Unable to build form field from entity " + kameHouse.json.stringify(arrayElement));
          }
          i++;
          continue;
        } else {
          kameHouse.logger.warn("No buildFormField function defined for column " + name);
        }
      }
      const newNode = kameHouse.util.dom.cloneNode(arraySourceNode, false);
      kameHouse.util.dom.setValue(newNode, kameHouse.json.stringify(arrayElement, null, 4));
      kameHouse.util.dom.setId(newNode, arraySourceNode.id + "-" + i);
      kameHouse.util.dom.classListAdd(newNode, "m-5-t-d-kh");
      kameHouse.util.dom.insertBefore(arraySourceNode.parentNode, newNode, arraySourceNode.nextSibling);
      i++;
    }
    if (i > 0) {
      // Only remove the arraySourceNode when the entity's array from the backend came with data
      kameHouse.util.dom.removeChild(arraySourceNode.parentNode, arraySourceNode);
    }
  }

  /**
   * Initialize the parentNodeChain used in recursive function calls.
   */
  function initParentNodeChain(parentNodeChain) {
    if (parentNodeChain) {
      return parentNodeChain + ".";
    } else {
      return "";
    }
  }

  /**
   * Display all entities and reload forms.
   */
  function reloadView() {
    kameHouse.logger.trace("reloadView");
    const crudTbody = $('#' + TBODY_ID);
    kameHouse.util.dom.empty(crudTbody);
    if (entities.length == 0 || entities.length == null || entities.length == undefined) {
      kameHouse.logger.info("No data received from the backend");
      const noDataTd = kameHouse.util.dom.getTrTd("No data received from the backend");
      kameHouse.util.dom.setAttr($(noDataTd), "id", NO_DATA_ROW_ID);
      kameHouse.util.dom.append(crudTbody, noDataTd);
    } else {
      const updatedCrudTbody = kameHouse.util.dom.getTbody({
        id: TBODY_ID
      }, null);
      kameHouse.util.dom.append(updatedCrudTbody, getCrudTableHeader());
      kameHouse.logger.info("Received " + entities.length + " entities from the backend");
      if (!reverseDataOrder) {
        for (const entity of entities) {
          kameHouse.util.dom.append(updatedCrudTbody, getEntityTr(entity));
        }
      } else {
        for (let i = entities.length -1; i >= 0; i--) {
          kameHouse.util.dom.append(updatedCrudTbody, getEntityTr(entities[i]));
        }
      }
      kameHouse.util.dom.replaceWith(crudTbody, updatedCrudTbody);
    }
    reloadForm(ADD_INPUT_FIELDS_ID);
    reloadForm(EDIT_INPUT_FIELDS_ID);
    sortAndFilterTable();
  }

  /**
   * Reload form view.
   */
  function reloadForm(formFieldsId) {
    const formFields = $('#' + formFieldsId);
    kameHouse.util.dom.empty(formFields);
    getFormFields(formFields, formFieldsId, columns, null);
  }

  /**
   * Display error getting entities.
   */
  function displayErrorGettingEntities() {
    const crudTbody = $('#' + TBODY_ID);
    kameHouse.util.dom.empty(crudTbody);
    kameHouse.util.dom.append(crudTbody, kameHouse.util.dom.getTrTd("Error getting data from the backend"));
  }

  /**
   * Get the entire table row for the entity.
   */
  function getEntityTr(entity) {
    const tr = kameHouse.util.dom.getTr({}, null);
    createEntityRow(tr, entity, columns, null);
    if (!readOnly) {
      kameHouse.util.dom.append(tr, getActionButtonsTd(entity.id));
    }
    return tr;
  }

  /**
   * Override this function with setCreateEntityTd() to create the data row for the entity
   * this crud manager will display in the table if the fields to show need to be customized.
   * 
   * If entity[property] is an object and not an array, then it's called recursively to add each subobject 
   * property as a separate column. 
   * For arrays it just stringifies the array to display it's content because for every row of data the array 
   * can be of different lengths, so I can't split the array into separate columns.
   * This behaviour should be consistent with the generation of table header columns, generation of form fields
   * and with the generation of the entity to pass to the backend for create and update.
   */
  function createEntityRow(tr, entity, currentNodeColumns, parentNodeChain) {
    parentNodeChain= initParentNodeChain(parentNodeChain);
    for (const column of currentNodeColumns) {
      const type = column.type;
      const name = column.name;
      if (isObjectField(type)) {
        createEntityRow(tr, entity[name], column.columns, parentNodeChain + name);
        continue;
      }
      setColumnValue(tr, type, entity[name], column);
    }
  }

  /**
   * Set the column value formatted depending on it's type.
   */
  function setColumnValue(tr, type, value, column) {
    if (isMaskedField(type)) {
      kameHouse.util.dom.append(tr, getMaskedFieldTd());
      return;
    }
    if (isDateField(type)) {
      kameHouse.util.dom.append(tr, kameHouse.util.dom.getTd({}, getFormattedDateFieldValue(value)));
      return;
    }
    if (isTimestampField(type)) {
      kameHouse.util.dom.append(tr, kameHouse.util.dom.getTd({}, getFormattedTimestampFieldValue(value)));
      return;
    }
    if (isArrayField(type)) {
      if (kameHouse.core.isFunction(column.buildListDisplay)) {
        kameHouse.util.dom.append(tr, kameHouse.util.dom.getTd({}, column.buildListDisplay(value)));
      } else {
        kameHouse.util.dom.append(tr, kameHouse.util.dom.getTd({}, kameHouse.json.stringify(value)));
      }
      return;
    }
    if (isBooleanField(type)) {
      let booleanValue;
      if (value) {
        booleanValue = "true";
      } else {
        booleanValue = "false";
      }
      kameHouse.util.dom.append(tr, kameHouse.util.dom.getTd({}, booleanValue));
      return;
    }
    kameHouse.util.dom.append(tr, kameHouse.util.dom.getTd({}, value));
  }

  /**
   * Check if it's a masked field.
   */
  function isMaskedField(type) {
    return isPasswordField(type) || isHiddenField(type);
  }

  /**
   * Returns a date field formatted.
   */
  function getFormattedDateFieldValue(value) {
    try {
      const date = kameHouse.util.time.getDateFromEpoch(value);
      if (kameHouse.util.time.isValidDate(date)) {
        return kameHouse.util.time.getDateWithTimezoneOffset(date).toISOString().substring(0,10);
      } else {
        return value;
      }
    } catch (error) {
      kameHouse.logger.warn("Unable to parse " + value + " as a date");
      return value;
    }
  }

  /**
   * Returns a timestamp field formatted.
   */
   function getFormattedTimestampFieldValue(value) {
    try {
      const date = kameHouse.util.time.getDateFromEpoch(value);
      if (kameHouse.util.time.isValidDate(date)) {
        return kameHouse.util.time.getTimestamp(date);
      } else {
        kameHouse.logger.warn("Invalid timestamp " + value);
        return value;
      }
    } catch (error) {
      kameHouse.logger.warn("Unable to parse " + value + " as a date");
      return value;
    }
  }  

  /**
   * Returns a masked field td. Used for passwords for example.
   */
  function getMaskedFieldTd() {
    return kameHouse.util.dom.getTd({}, "****");
  }

  /**
   * Get the action buttons for the entity.
   */
  function getActionButtonsTd(id) {
    const td = kameHouse.util.dom.getTd({}, null);
    kameHouse.util.dom.append(td, getEditButton(id));
    kameHouse.util.dom.append(td, getConfirmDeleteButton(id));
    return td;
  }

  /**
   * Get the edit button for the entity.
   */
  function getEditButton(id) {
    return kameHouse.util.dom.getImgBtn({
      src: "/kame-house/img/other/edit.png",
      className: "img-btn-kh m-15-d-r-kh",
      alt: "Edit",
      onClick: () => { 
        kameHouse.util.tab.openTab('tab-edit', 'kh-crud-manager');
        read(id);
      }
    });
  }

  /**
   * Get the button to open a modal to delete the entity.
   */
  function getConfirmDeleteButton(id) {
    return kameHouse.util.dom.getImgBtn({
      src: "/kame-house/img/other/delete.png",
      className: "img-btn-kh",
      alt: "Delete",
      onClick: () => confirmDelete(id)
    });
  }

  /**
   * Get the delete button for the entity.
   */
  function getDeleteButton(id) {
    return kameHouse.util.dom.getButton({
      attr: {
        class: "form-submit-btn-kh",
      },
      html: "Yes",
      clickData: {
        id: id
      },
      click: deleteEntity
    });
  }
  
  /**
   * Open modal to confirm reboot.
   */
   function confirmDelete(id) {
    kameHouse.plugin.modal.basicModal.setHtml(getDeleteModalMessage(id));
    kameHouse.plugin.modal.basicModal.appendHtml(getDeleteButton(id));
    kameHouse.plugin.modal.basicModal.open();
  }

  /**
   * Get delete modal message.
   */
  function getDeleteModalMessage(id) {
    const message = kameHouse.util.dom.getSpan({}, "Are you sure you want to delete the " + entityName + " with id " + id + " ?");
    kameHouse.util.dom.append(message, kameHouse.util.dom.getBr());
    kameHouse.util.dom.append(message, kameHouse.util.dom.getBr());
    return message;
  }

  /**
   * Get the table header row.
   */
  function getCrudTableHeader() {
    const tr = kameHouse.util.dom.getTr({
      class: "table-kh-header"
    }, null);
    setHeaderColumns(tr, columns, null, 0);
    if (!readOnly) {
      kameHouse.util.dom.append(tr, kameHouse.util.dom.getTd({
        class: "table-kh-actions"
      }, "actions"));
    }
    return tr;
  }

  /**
   * Set the table header columns. Returns the 
   */
  function setHeaderColumns(tr, currentNodeColumns, parentNodeChain, columnIndex) {
    parentNodeChain= initParentNodeChain(parentNodeChain);
    let addedObjectColumnIndexes = 0;
    for (const column of currentNodeColumns) {
      const type = column.type;
      const name = column.name;
      if (isObjectField(type)) {
        let newColumnIndexes = 0;
        newColumnIndexes = setHeaderColumns(tr, column.columns, parentNodeChain + name, columnIndex);
        addedObjectColumnIndexes = addedObjectColumnIndexes + newColumnIndexes;
        continue;
      }
      let currentColumnIndex = columnIndex + addedObjectColumnIndexes;
      const td = kameHouse.util.dom.getTd({
        id: TBODY_ID + "-col-" + currentColumnIndex,
        class: "clickable",
        alt: "Sort by " + parentNodeChain + name,
        title: "Sort by " + parentNodeChain + name
      }, parentNodeChain + name);
      const sortType = getSortType(column);
      kameHouse.logger.trace("Setting sort for column name: " + parentNodeChain + name + ", column index: " + currentColumnIndex + ", sort type: " + sortType);
      kameHouse.util.dom.setClick(td, null,
        () => {
          kameHouse.util.table.sortTable("crud-manager-table", currentColumnIndex, sortType, null, filterRows);
        }
      );
      kameHouse.util.dom.append(tr, td);
      columnIndex++;
    }
    const lastAddedColumnIndex = columnIndex - 1;
    return lastAddedColumnIndex;
  }

  /**
   * Get the sort type based on the column type.
   */
  function getSortType(column) {
    const type = column.type;
    if (type == "select" && !kameHouse.core.isEmpty(column.sortType)) {
      return column.sortType;
    }
    return type;
  }

  /**
   * Get all the form fields.
   */
   function getFormFields(div, formFieldsId, currentNodeColumns, parentNodeChain) {
    parentNodeChain= initParentNodeChain(parentNodeChain);
    for (const column of currentNodeColumns) {
      const type = column.type;
      if (isObjectField(type)) {
        getFormFields(div, formFieldsId, column.columns, parentNodeChain + column.name);
        continue;
      }

      const name = parentNodeChain + column.name;
      const fieldId = formFieldsId + "-" + name;
      const fieldClassList = "form-input-kh " + formFieldsId + "-field";
      
      addFieldLabel(div, type, name);
      kameHouse.util.dom.append(div, getFormInputField(column, fieldId, fieldClassList));
      addArrayRowButtons(div, column, fieldId);
      addShowPasswordCheckbox(div, type, fieldId);
      addBreak(div, type);
    }
  }

  /**
   * Gets an input field for the form of the correct type.
   */
  function getFormInputField(column, fieldId, fieldClassList) {
    const type = column.type;
    const inputFieldType = getInputFieldType(type);
    const config = {
      id: fieldId,
      class: fieldClassList,
      type: inputFieldType,
      name: column.name
    };

    if (isSelectField(type)) {
      return getSelectField(config, column);
    }

    if (isNumberField(type)) {
      if (!kameHouse.core.isEmpty(column.min) || column.min == 0) {
        config.min = column.min;
      }
      if (!kameHouse.core.isEmpty(column.max) || column.max == 0) {
        config.max = column.max;
      }
    }

    if (isArrayField(type)) {
      config.name = fieldId + "[]";
      const arrayType = column.arrayType;
      if (arrayType == "object") {
        return kameHouse.util.dom.getTextArea(config, null);
      }
      if (arrayType == "select") {
        return getSelectField(config, column);
      }
    }

    return kameHouse.util.dom.getInput(config, null);
  }

  function getSelectField(config, column) {
    const select = kameHouse.util.dom.getSelect(config, null);
    const values = column.values;
    const displayValues = column.displayValues;
    kameHouse.util.dom.append(select, kameHouse.util.dom.getOption({
      value: ""
    }, ""));
    for (let i = 0; i < values.length; i++) { 
      kameHouse.util.dom.append(select, kameHouse.util.dom.getOption({
        value: values[i]
      }, displayValues[i]));
    }
    return select;
  }

  /**
   * Map the type of the column to an input field type.
   */
  function getInputFieldType(columnType) {
    if (columnType == "boolean") {
      return "checkbox";
    }
    if (columnType == "date") {
      return "date";
    }
    if (columnType == "email") {
      return "email";
    }
    if (columnType == "hidden") {
      return "hidden";
    }
    if (columnType == "id") {
      return "hidden";
    }
    if (columnType == "number") {
      return "number";
    }
    if (columnType == "password") {
      return "password";
    }
    if (columnType == "select") {
      return "select";
    }
    if (columnType == "time") {
      return "time";
    }
    return "text";
  }

  /**
   * Add label to field.
   */
  function addFieldLabel(div, type, name) {
    if (!isIdField(type) && !isHiddenField(type)) {
      kameHouse.util.dom.append(div, kameHouse.util.dom.getLabel({}, name));
    }
  }

  /**
   * Add break after input field.
   */
  function addBreak(div, type) {
    if (!isIdField(type) && !isHiddenField(type)) {
      kameHouse.util.dom.append(div, kameHouse.util.dom.getBr());
    }
  }

  /**
   * Add button to add extra rows for arrays.
   */
  function addArrayRowButtons(div, column, fieldId) {
    if (!isArrayField(column.type)) {
      return;
    }
    const addButtonId = fieldId + "-add";
    const addButton = kameHouse.util.dom.getImgBtn({
      id: addButtonId,
      src: "/kame-house/img/other/add-gray-dark.png",
      className: "img-btn-kh p-7-d-kh m-7-d-kh",
      alt: "Add",
      onClick: () => addArrayInputFieldElement(addButtonId, fieldId, column.type)
    });
    kameHouse.util.dom.append(div, addButton);
    const removeButtonId = fieldId + "-remove";
    const removeButton = kameHouse.util.dom.getImgBtn({
      id: removeButtonId,
      src: "/kame-house/img/other/remove-gray-dark.png",
      className: "img-btn-kh p-7-d-kh m-7-d-kh",
      alt: "Remove",
      onClick: () => removeArrayInputFieldElement(removeButtonId, fieldId)
    });
    kameHouse.util.dom.append(div, removeButton);
  }

  /**
   * Add a new entry to the array input field.
   */
  function addArrayInputFieldElement(buttonId, fieldId, columnType) {
    kameHouse.logger.debug("Adding array element");
    const arraySourceNode = document.getElementById(buttonId).previousSibling; 
    let deepClone = false;
    if (isArrayField(columnType)) {
      deepClone = true;
    }
    if (arraySourceNode.name != fieldId + "[]") {
      kameHouse.logger.error("Trying to clone a node that isn't an array element of the expected name. Something's wrong. Name of node to clone is: " + arraySourceNode.name + " and the expected value is " + fieldId + "[]");
      return;
    }
    const newNode = kameHouse.util.dom.cloneNode(arraySourceNode, deepClone);
    newNode.value = "";
    newNode.id = "";
    Array.from(newNode.attributes).forEach((attribute) => {
      if (attribute.name.startsWith("data-kamehouse-")) {
        attribute.value = "";
      }
    });
    kameHouse.util.dom.classListAdd(newNode, "m-5-t-d-kh");
    kameHouse.util.dom.insertBefore(arraySourceNode.parentNode, newNode, arraySourceNode.nextSibling);
  }

  function removeArrayInputFieldElement(buttonId, fieldId) {
    kameHouse.logger.debug("Removing array element");
    const arrayNodes = document.getElementsByName(fieldId + "[]");
    if (kameHouse.core.isEmpty(arrayNodes) || arrayNodes.length <= 1) {
      kameHouse.logger.warn("Trying to remove the last node of the array. Skipping...");
      return;
    }
    const nodeToRemove = document.getElementById(buttonId).previousSibling.previousSibling; 
    if (nodeToRemove.name != fieldId + "[]") {
      kameHouse.logger.error("Trying to remove a node that isn't an array element of the expected name. Something's wrong. Name of node to remove is: " + nodeToRemove.name + " and the expected value is " + fieldId + "[]");
      return;
    }
    kameHouse.util.dom.removeElement(nodeToRemove);
  }

  /**
   * Add checkbox to show password.
   */
  function addShowPasswordCheckbox(div, type, fieldId) {
    if (!isPasswordField(type)) {
      return;
    }
    const checkbox = kameHouse.util.dom.getInput({
      type: "checkbox",
      class: "m-7-d-kh"
     }, null);
    kameHouse.util.dom.setClick(checkbox, () => toggleShowHidePassword(fieldId));
    kameHouse.util.dom.append(div, checkbox);
  }

  /**
   * Toggle show or hide password.
   */
  function toggleShowHidePassword(passwordFieldId) {
    const passwordField = document.getElementById(passwordFieldId);
    if (passwordField.type === "password") {
      kameHouse.util.dom.setAttribute(passwordField, "type", "text");
    } else {
      kameHouse.util.dom.setAttribute(passwordField, "type", "password");
    }
  }

  /**
   * Check for id field.
   */
  function isIdField(type) {
    return type == "id";
  }

  /**
   * Check if it's a password field.
   */
  function isPasswordField(type) {
    return type == "password";
  }

  /**
   * Check if it's a hidden field.
   */
  function isHiddenField(type) {
    return type == "hidden";
  }

  /**
   * Check if it's a date field.
   */
  function isDateField(type) {
    return type == "date";
  }

  /**
   * Check if it's a date field.
   */
  function isTimestampField(type) {
    return type == "timestamp";
  }
  
  /**
   * Check if it's an object field.
   */
   function isObjectField(type) {
    return type == "object";
  }

  /**
   * Check if it's a array field.
   */
   function isArrayField(type) {
    return type == "array";
  }

  /**
   * Check if it's a boolean field.
   */
   function isBooleanField(type) {
    return type == "boolean";
  }  
  
  /**
   * Check if it's a array field.
   */
   function isSelectField(type) {
    return type == "select";
  }
 
  /**
   * Check if it's a array field.
   */
   function isNumberField(type) {
    return type == "number";
  }

  /**
   * Build the entity to pass to the backend from the form data.
   */
  function getEntityFromForm(formFieldsId) {
    const entity = {};
    setEntityProperties(entity, formFieldsId, columns, null);
    return entity;
  }

  /**
   * Set the properties to the entity from the form.
   */
  function setEntityProperties(entity, formFieldsId, currentNodeColumns, parentNodeChain) {
    parentNodeChain = initParentNodeChain(parentNodeChain);
    for (const column of currentNodeColumns) {
      const type = column.type;
      const name = column.name;
      if (isObjectField(type)) {
        if (kameHouse.core.isEmpty(entity[name])) {
          entity[name] = {};
        }
        setEntityProperties(entity[name], formFieldsId, column.columns, parentNodeChain + name);
        continue;
      }
      setEntityPropertyValue(entity, formFieldsId, parentNodeChain, column);
    }
  }

  /**
   * Set the value of the property on the entity for the current column.
   */
  function setEntityPropertyValue(entity, formFieldsId, parentNodeChain, column) {
    const type = column.type;
    const name = column.name;
    const inputFieldId = formFieldsId + "-" + parentNodeChain + name;
    const inputField = document.getElementById(inputFieldId);
    let val = null;
    if (inputField) {
      val = inputField.value;
    }
     
    if (isBooleanField(type)) {
      val = inputField.checked;
    }

    entity[name] = val;

    if (kameHouse.core.isEmpty(val)) {
      entity[name] = null;
    }

    if (isArrayField(type)) {
      entity[name] = getArrayFieldValue(column, formFieldsId);
    }
  }

  /**
   * Get the value from an array field.
   */
  function getArrayFieldValue(column, formFieldsId) {
    const name = column.name;
    const array = document.getElementsByName(formFieldsId + "-" + name + "[]");
    const arrayVal = [];
    for (const arrayElement of array) {
      let arrayElementValue = buildArrayElementValue(column, arrayElement);
      if (arrayElementValue) {
        arrayVal.push(arrayElementValue);
      }
    }
    return arrayVal;
  }

  function buildArrayElementValue(column, arrayElement) {
    const arrayType = column.arrayType;
    const name = column.name;
    if (kameHouse.core.isEmpty(arrayElement.value)) {
      return null;
    }
    if (isObjectField(arrayType)) {
      return kameHouse.json.parse(arrayElement.value);
    }
    if (isSelectField(arrayType)) {
      if (kameHouse.core.isFunction(column.buildEntity)) {
        const entityArrayElement = column.buildEntity(arrayElement);
        if (!kameHouse.core.isEmpty(entityArrayElement)) {
          return entityArrayElement;
        }
      } else {
        kameHouse.logger.warn("No buildEntity function defined in config for " + name);
      }
    }
    return arrayElement.value;
  }

  /**
   * Clear all the form fields.
   */
  function clearForm(formFieldsId) {
    reloadForm(formFieldsId);
  }

  /**
   * Filter the table rows based on all the filters registered in the crud manager.
   * 
   * Register filters tagging them with the classes 
   * 'crud-manager-filter' for filters that apply across all columns
   * 'crud-manager-column-filter' for filters that apply to a particular column
   * and they will be picked up here.
   * 
   * For 'crud-manager-column-filter' also specify the attribute data-column-number on the select
   * with the column number to apply the filter on.
   * 
   * All the filters tagged with those classes will be applied.
   */
  function filterRows() {
    // first show all rows, then apply sequentially each of the filters, ignoring hidden rows, then limit row number
    kameHouse.util.table.filterTableRows("", TBODY_ID, null);

    const noDataRow = document.getElementById(NO_DATA_ROW_ID);
    if (!kameHouse.core.isEmpty(noDataRow)) {
      kameHouse.logger.info("No data received from the backend, skipping filters");
      return;
    }

    const filters = document.getElementsByClassName("crud-manager-filter");
    for (const filter of filters) {
      const filterString = filter.value;
      kameHouse.logger.trace("Applying filter " + filter.id + " with string " + filterString);
      kameHouse.util.table.filterTableRows(filterString, TBODY_ID, null, true);
    }

    const columnFilters = document.getElementsByClassName("crud-manager-column-filter");
    for (const columnFilter of columnFilters) {
      const filterString = columnFilter.value;
      const columnNumber = columnFilter.dataset.columnNumber;
      kameHouse.logger.trace("Applying filter " + columnFilter.id + " with string " + filterString);
      kameHouse.util.table.filterTableRowsByColumn(filterString, TBODY_ID, columnNumber, null, true);
    }
    
    const numRows = document.getElementById('num-rows').value;
    kameHouse.util.table.limitRows('crud-manager-table', numRows, true);
  }

  /**
   * Sort the table data if default sorting is specified.
   * Column numbers start with 0.
   * Then apply filters.
   */
  function sortAndFilterTable() {
    if (kameHouse.core.isEmpty(defaultSorting)) {
      filterRows();
      return;
    }
    kameHouse.logger.trace("Sorting table data with default sorting config: " + kameHouse.json.stringify(defaultSorting));
    kameHouse.util.table.sortTable("crud-manager-table", defaultSorting.columnNumber, defaultSorting.sortType, defaultSorting.direction, filterRows);
  }

  function refreshView() {
    kameHouse.util.dom.setValue(document.getElementById('num-rows'), "");
    
    const filters = document.getElementsByClassName("crud-manager-filter");
    for (const filter of filters) {
      filter.selectedIndex = -1;
    }

    const columnFilters = document.getElementsByClassName("crud-manager-column-filter");
    for (const columnFilter of columnFilters) {
      columnFilter.selectedIndex = -1;
    }
    
    readAll();
  }
}

$(document).ready(() => {kameHouse.addPlugin("crudManager", new CrudManager())});