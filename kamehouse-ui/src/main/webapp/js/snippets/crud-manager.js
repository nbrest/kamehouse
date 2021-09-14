var crudManager;

function mainCrudManager() {
  logger.info("Started initializing crudManager");
  crudManager = new CrudManager();
  crudManager.load();
}

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

  const tbodyId = "crud-manager-tbody";
  const addInputFieldsId = "crud-add-input-fields";
  const editInputFieldsId = "crud-edit-input-fields";

  let entityName = "Set EntityName";
  let url = "/kame-house-module/api/v1/override-url";
  let columns = [];
  let entities = [];
  let readOnly = false;
  let defaultSorting = null;
  
  /**
   * Load the crud manager module.
   */
  function load() {
    domUtils.append($('head'), '<link rel="stylesheet" type="text/css" href="/kame-house/css/snippets/crud-manager.css">');
    domUtils.load($("#crud-manager-body-wrapper"), "/kame-house/html-snippets/crud-manager.html", () => {
      moduleUtils.setModuleLoaded("crudManager");
      bannerUtils.setRandomPrinceOfTennisBanner();
    });
  }

  /**
   * Initialize the crud manager. Configuration object is: 
   * {
   *    entityName: "EntityName",
   *    url: "/kame-house-module/etc",
   *    readOnly: true,
   *    defaultSorting: {
   *     columnNumber: 11,
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
    setEntityName(config.entityName);
    setUrl(config.url);
    setColumns(config.columns);
    setReadOnly(config.readOnly);
    setDefaultSorting(config.defaultSorting);
    loadCustomSections(config);
    updateEntityNameInView();
    loadStateFromCookies();
    loadStateFromUrlParams();
    disableEditFunctionalityForReadOnly();
    readAll();
  }

  /**
   * Updates the view with the entity name.
   */
  function updateEntityNameInView() {
    domUtils.setHtml($("title"), "KameHouse - " + getEntityNames());
    domUtils.setHtml($("#crud-manager-banner-title"), getEntityNames());
    domUtils.setHtml($("#crud-manager-list-title"), "List " + getEntityNames());
    domUtils.setHtml($("#crud-manager-add-title"), "Add " + entityName);
    domUtils.setHtml($("#crud-manager-edit-title"), "Edit " + entityName);
  }

  /**
   * When set to read only, disable add and edit tabs.
   */
  function disableEditFunctionalityForReadOnly() {
    if (readOnly) {
      domUtils.addClass($("#crud-manager-tabs"), "hidden-kh");
      domUtils.addClass($("#tab-add-link"), "hidden-kh");
      domUtils.addClass($("#tab-edit-link"), "hidden-kh");
      tabUtils.openTab('tab-list', 'kh-crud-manager');
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
    tabUtils.openTabFromCookies('kh-crud-manager', 'tab-list');
  }

  /**
   * Load the current state from the url parameters.
   */
  function loadStateFromUrlParams() {
    const urlParams = new URLSearchParams(window.location.search);
    const search = urlParams.get('search');
    if (!isEmpty(search)) {
      domUtils.setValue(document.getElementById('search-filter'), search);
    }
  }

  /**
   * Set the name of the entity managed by the crud manager.
   */
  function setEntityName(name) {
    entityName = name;
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
    if (!isEmpty(crudReadOnly)) {
      readOnly = crudReadOnly;
    }
  }

  /**
   * Set the default sorting of table data.
   */
  function setDefaultSorting(crudDefaultSorting) {
    if (!isEmpty(crudDefaultSorting)) {
      defaultSorting = crudDefaultSorting;
    }
  }

  function loadCustomSections(config) {
    if (!isEmpty(config.customListSection)) {
      domUtils.load($("#custom-list-section"), config.customListSection);
    }
  }

  /**
   * Get an entity by it's id.
   */
  function read(id) {
    logger.info("read");
    const getUrl = url + "/" + id;
    debuggerHttpClient.get(getUrl,
      (responseBody, responseCode, responseDescription) => {
        setEditFormValues(responseBody, responseCode, responseDescription);
      },
      (responseBody, responseCode, responseDescription) => {
        logger.logApiError(responseBody, responseCode, responseDescription, "Error getting entity");
        basicKamehouseModal.openApiError(responseBody, responseCode, responseDescription);
      }, null);
  }

  /**
   * Get all entities.
   */
  function readAll() {
    logger.info("readAll");
    debuggerHttpClient.get(url,
      (responseBody, responseCode, responseDescription) => {
        entities = responseBody;
        reloadView();
      },
      (responseBody, responseCode, responseDescription) => {
        logger.logApiError(responseBody, responseCode, responseDescription, "Error getting all entities");
        basicKamehouseModal.openApiError(responseBody, responseCode, responseDescription);
        displayErrorGettingEntities();
      }, null);
  }

  /**
   * Create an entity.
   */
  function create() {
    logger.info("create");
    if (readOnly) {
      basicKamehouseModal.openAutoCloseable("This crud manager is set to read-only. Can't execute updates", 5000);
      return;
    }
    const entity = getEntityFromForm(addInputFieldsId);
    debuggerHttpClient.post(url, entity,
      (responseBody, responseCode, responseDescription) => {
        logger.info("Created entity successfully. Id: " + responseBody);
        readAll();
        tabUtils.openTab('tab-list', 'kh-crud-manager');
      },
      (responseBody, responseCode, responseDescription) => {
        logger.logApiError(responseBody, responseCode, responseDescription, "Error creating entity");
        basicKamehouseModal.openApiError(responseBody, responseCode, responseDescription);
        readAll();
      });
  }

  /**
   * Update an entity.
   */
  function update() {
    logger.info("update");
    if (readOnly) {
      basicKamehouseModal.openAutoCloseable("This crud manager is set to read-only. Can't execute updates", 5000);
      return;
    }
    const entity = getEntityFromForm(editInputFieldsId);
    const updateUrl = url + "/" + entity.id;
    debuggerHttpClient.put(updateUrl, entity,
      (responseBody, responseCode, responseDescription) => {
        logger.info("Updated entity successfully. Id: " + entity.id);
        readAll();
        tabUtils.openTab('tab-list', 'kh-crud-manager');
      },
      (responseBody, responseCode, responseDescription) => {
        logger.logApiError(responseBody, responseCode, responseDescription, "Error updating entity");
        basicKamehouseModal.openApiError(responseBody, responseCode, responseDescription);
        readAll();
      }, null);
  }

  /**
   * Delete entity from the server.
   */
  function deleteEntity(event) {
    const id = event.data.id;
    logger.info("deleteEntity");
    if (readOnly) {
      basicKamehouseModal.openAutoCloseable("This crud manager is set to read-only. Can't execute updates", 5000);
      return;
    }
    const deleteUrl = url + "/" + id;
    debuggerHttpClient.delete(deleteUrl, null,
      (responseBody, responseCode, responseDescription) => {
        logger.info("Deleted entity successfully. Id: " + responseBody.id);
        basicKamehouseModal.close();
        readAll();
      },
      (responseBody, responseCode, responseDescription) => {
        logger.logApiError(responseBody, responseCode, responseDescription, "Error deleting entity");
        basicKamehouseModal.close();
        basicKamehouseModal.openApiError(responseBody, responseCode, responseDescription);
        readAll();
      }, null);
  }

  /**
   * Default setEditFormValues. Override to set custom functionality. 
   * This should usually be used to load the form fields to edit an entity.
   * Probably needs to be overriden if custom columns are set or the forms are loaded from a snippet.
   */
  function setEditFormValues(responseBody, responseCode, responseDescription) { 
    logger.debug("readCallback: override this with setReadCallback when required");
    reloadForm(editInputFieldsId);
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
      const inputFieldId = editInputFieldsId + "-" + parentNodeChain + name;
      const inputField = $(document.getElementById(inputFieldId));
      domUtils.setVal(inputField, entity[name]); 

      if (isDateField(type)) {
        domUtils.setVal(inputField, getFormattedDateFieldValue(entity[name]));
      }
      if (isArrayField(type)) {
        domUtils.setVal(inputField, null);
        const array = entity[name];
        const arraySourceNode = document.getElementById(inputFieldId); 
        for (const arrayElement of array) {
          const newNode = domUtils.cloneNode(arraySourceNode, false);
          domUtils.setValue(newNode, JSON.stringify(arrayElement, null, 4));
          domUtils.setId(newNode, arraySourceNode.id + "-" + i);
          domUtils.classListAdd(newNode, "m-5-t-d-kh");
          domUtils.insertBefore(arraySourceNode.parentNode, newNode, arraySourceNode.nextSibling);
        }
        domUtils.removeChild(arraySourceNode.parentNode, arraySourceNode);
      }
      if (isBooleanField(type)) {
        if (entity[name]) {
          domUtils.setAttr(inputField, "checked", "true"); 
        }
      }
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
    logger.trace("reloadView");
    const crudTbody = $('#' + tbodyId);
    domUtils.empty(crudTbody);
    domUtils.append(crudTbody, getCrudTableHeader());
    for (const entity of entities) {
      domUtils.append(crudTbody, getEntityTr(entity));
    }
    filterRows();
    reloadForm(addInputFieldsId);
    reloadForm(editInputFieldsId);
    sortTable();
  }

  /**
   * Reload form view.
   */
  function reloadForm(formFieldsId) {
    const formFields = $('#' + formFieldsId);
    domUtils.empty(formFields);
    getFormFields(formFields, formFieldsId, columns, null);
  }

  /**
   * Display error getting entities.
   */
  function displayErrorGettingEntities() {
    const crudTbody = $('#' + tbodyId);
    domUtils.empty(crudTbody);
    domUtils.append(crudTbody, domUtils.getTrTd("Error getting data from the backend"));
  }

  /**
   * Get the entire table row for the entity.
   */
  function getEntityTr(entity) {
    const tr = domUtils.getTr({}, null);
    createEntityRow(tr, entity, columns, null);
    if (!readOnly) {
      domUtils.append(tr, getActionButtonsTd(entity.id));
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
      setColumnValue(tr, type, entity[name]);
    }
  }

  /**
   * Set the column value formatted depending on it's type.
   */
  function setColumnValue(tr, type, value) {
    if (isMaskedField(type)) {
      domUtils.append(tr, getMaskedFieldTd());
      return;
    }
    if (isDateField(type)) {
      domUtils.append(tr, domUtils.getTd({}, getFormattedDateFieldValue(value)));
      return;
    }
    if (isTimestampField(type)) {
      domUtils.append(tr, domUtils.getTd({}, getFormattedTimestampFieldValue(value)));
      return;
    }
    if (isArrayField(type)) {
      domUtils.append(tr, domUtils.getTd({}, JSON.stringify(value)));
      return;
    }
    if (isBooleanField(type)) {
      let booleanValue;
      if (value) {
        booleanValue = "true";
      } else {
        booleanValue = "false";
      }
      domUtils.append(tr, domUtils.getTd({}, booleanValue));
      return;
    }
    domUtils.append(tr, domUtils.getTd({}, value));
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
      const date = timeUtils.getDateFromEpoch(value);
      if (timeUtils.isValidDate(date)) {
        return timeUtils.getDateWithTimezoneOffset(date).toISOString().substring(0,10);
      } else {
        return value;
      }
    } catch (error) {
      logger.warn("Unable to parse " + value + " as a date");
      return value;
    }
  }

  /**
   * Returns a timestamp field formatted.
   */
   function getFormattedTimestampFieldValue(value) {
    try {
      const date = timeUtils.getDateFromEpoch(value);
      if (timeUtils.isValidDate(date)) {
        return timeUtils.getTimestamp(date);
      } else {
        logger.warn("Invalid timestamp " + value);
        return value;
      }
    } catch (error) {
      logger.warn("Unable to parse " + value + " as a date");
      return value;
    }
  }  

  /**
   * Returns a masked field td. Used for passwords for example.
   */
  function getMaskedFieldTd() {
    return domUtils.getTd({}, "****");
  }

  /**
   * Get the action buttons for the entity.
   */
  function getActionButtonsTd(id) {
    const td = domUtils.getTd({}, null);
    domUtils.append(td, getEditButton(id));
    domUtils.append(td, getConfirmDeleteButton(id));
    return td;
  }

  /**
   * Get the edit button for the entity.
   */
  function getEditButton(id) {
    return domUtils.getImgBtn({
      src: "/kame-house/img/other/edit-green.png",
      className: "img-btn-kh m-15-d-r-kh",
      alt: "Edit",
      onClick: () => { 
        tabUtils.openTab('tab-edit', 'kh-crud-manager');
        read(id);
      }
    });
  }

  /**
   * Get the button to open a modal to delete the entity.
   */
  function getConfirmDeleteButton(id) {
    return domUtils.getImgBtn({
      src: "/kame-house/img/other/delete-red.png",
      className: "img-btn-kh",
      alt: "Delete",
      onClick: () => confirmDelete(id)
    });
  }

  /**
   * Get the delete button for the entity.
   */
  function getDeleteButton(id) {
    return domUtils.getButton({
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
    basicKamehouseModal.setHtml(getDeleteModalMessage(id));
    basicKamehouseModal.appendHtml(getDeleteButton(id));
    basicKamehouseModal.open();
  }

  /**
   * Get delete modal message.
   */
  function getDeleteModalMessage(id) {
    const message = domUtils.getSpan({}, "Are you sure you want to delete the " + entityName + " with id " + id + " ?");
    domUtils.append(message, domUtils.getBr());
    domUtils.append(message, domUtils.getBr());
    return message;
  }

  /**
   * Get the table header row.
   */
  function getCrudTableHeader() {
    const tr = domUtils.getTr({
      class: "table-kh-header"
    }, null);
    setHeaderColumns(tr, columns, null, 0);
    if (!readOnly) {
      domUtils.append(tr, domUtils.getTd({
        class: "table-kh-actions"
      }, "actions"));
    }
    return tr;
  }

  /**
   * Set the table header columns.
   */
  function setHeaderColumns(tr, currentNodeColumns, parentNodeChain, columnIndex) {
    parentNodeChain= initParentNodeChain(parentNodeChain);
    for (const column of currentNodeColumns) {
      const type = column.type;
      const name = column.name;
      if (isObjectField(type)) {
        setHeaderColumns(tr, column.columns, parentNodeChain + name, columnIndex);
        continue;
      }
      const currentColumnIndex = columnIndex;
      const td = domUtils.getTd({
        id: tbodyId + "-col-" + currentColumnIndex,
        class: "clickable",
        alt: "Sort by " + parentNodeChain + name,
        title: "Sort by " + parentNodeChain + name
      }, parentNodeChain + name);
      const sortType = getSortType(column);
      domUtils.setClick(td, null,
        () => {
          tableUtils.sortTable("crud-manager-table", currentColumnIndex, sortType);
          filterRows();
        }
      );
      domUtils.append(tr, td);
      columnIndex++;
    }
  }

  /**
   * Get the sort type based on the column type.
   */
  function getSortType(column) {
    const type = column.type;
    if (type == "select" && !isEmpty(column.sortType)) {
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
      domUtils.append(div, getFormInputField(column, fieldId, fieldClassList));
      addAddArrayRowButton(div, column, fieldId);
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
      const select = domUtils.getSelect(config, null);
      const values = column.values;
      const displayValues = column.displayValues;
      for (let i = 0; i < values.length; i++) { 
        domUtils.append(select, domUtils.getOption({
          value: values[i]
        }, displayValues[i]));
      }
      return select;
    }

    if (isNumberField(type)) {
      if (!isEmpty(column.min) || column.min == 0) {
        config.min = column.min;
      }
      if (!isEmpty(column.max) || column.max == 0) {
        config.max = column.max;
      }
    }

    if (isArrayField(type)) {
      config.name = fieldId + "[]";
      const arrayType = column.arrayType;
      if (arrayType == "object") {
        return domUtils.getTextArea(config, null);
      }
    }

    return domUtils.getInput(config, null);
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
      domUtils.append(div, domUtils.getLabel({}, name));
    }
  }

  /**
   * Add break after input field.
   */
  function addBreak(div, type) {
    if (!isIdField(type) && !isHiddenField(type)) {
      domUtils.append(div, domUtils.getBr());
    }
  }

  /**
   * Add button to add extra rows for arrays.
   */
  function addAddArrayRowButton(div, column, fieldId) {
    if (!isArrayField(column.type)) {
      return;
    }
    const buttonId = fieldId + "-add";
    const button = domUtils.getImgBtn({
      id: buttonId,
      src: "/kame-house/img/other/add-gray-dark.png",
      className: "img-btn-kh p-7-d-kh m-7-d-kh",
      alt: "Add",
      onClick: () => addArrayInputFieldElement(buttonId)
    });
    domUtils.append(div, button);
  }

  /**
   * Add a new entry to the array input field.
   */
  function addArrayInputFieldElement(buttonId) {
    const arraySourceNode = document.getElementById(buttonId).previousSibling; 
    const newNode = domUtils.cloneNode(arraySourceNode, false);
    newNode.value = "";
    newNode.id = "";
    domUtils.classListAdd(newNode, "m-5-t-d-kh");
    domUtils.insertBefore(arraySourceNode.parentNode, newNode, arraySourceNode.nextSibling);
  }

  /**
   * Add checkbox to show password.
   */
  function addShowPasswordCheckbox(div, type, fieldId) {
    if (!isPasswordField(type)) {
      return;
    }
    const checkbox = domUtils.getInput({
      type: "checkbox",
      class: "m-7-d-kh"
     }, null);
    domUtils.setClick(checkbox, () => toggleShowHidePassword(fieldId));
    domUtils.append(div, checkbox);
  }

  /**
   * Toggle show or hide password.
   */
  function toggleShowHidePassword(passwordFieldId) {
    const passwordField = document.getElementById(passwordFieldId);
    if (passwordField.type === "password") {
      domUtils.setAttribute(passwordField, "type", "text");
    } else {
      domUtils.setAttribute(passwordField, "type", "password");
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

  function setEntityProperties(entity, formFieldsId, currentNodeColumns, parentNodeChain) {
    parentNodeChain = initParentNodeChain(parentNodeChain);
    for (const column of currentNodeColumns) {
      const type = column.type;
      const name = column.name;
      if (isObjectField(type)) {
        if (isEmpty(entity[name])) {
          entity[name] = {};
        }
        setEntityProperties(entity[name], formFieldsId, column.columns, parentNodeChain + name);
        continue;
      }

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

      if (isEmpty(val) || val == "") {
        entity[name] = null;
      }
      if (isArrayField(type)) {
        const array = document.getElementsByName(formFieldsId + "-" + name + "[]");
        const arrayType = column.arrayType;
        const arrayVal = [];
        for (const arrayElement of array) {
          if (!isEmpty(arrayElement.value) && arrayElement.value != "") {
            if (isObjectField(arrayType)) {
              arrayVal.push(JSON.parse(arrayElement.value));
            } else {
              arrayVal.push(arrayElement.value);
            }
          }
        }
        entity[name] = arrayVal;
      }
    }
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
    tableUtils.filterTableRows("", 'crud-manager-tbody', null);

    const filters = document.getElementsByClassName("crud-manager-filter");
    for (const filter of filters) {
      const filterString = filter.value;
      logger.trace("Applying filter " + filter.id + " with string " + filterString);
      tableUtils.filterTableRows(filterString, 'crud-manager-tbody', null, true);
    }

    const columnFilters = document.getElementsByClassName("crud-manager-column-filter");
    for (const columnFilter of columnFilters) {
      const filterString = columnFilter.value;
      const columnNumber = columnFilter.dataset.columnNumber;
      logger.trace("Applying filter " + columnFilter.id + " with string " + filterString);
      tableUtils.filterTableRowsByColumn(filterString, 'crud-manager-tbody', columnNumber, null, true);
    }
    
    const numRows = document.getElementById('num-rows').value;
    tableUtils.limitRows('crud-manager-table', numRows, true);
  }

  /**
   * Sort the table data if default sorting is specified.
   * Column numbers start with 0.
   * 
   */
  function sortTable() {
    if (isEmpty(defaultSorting)) {
      return;
    }
    logger.trace("Sorting table data with default sorting config: " + JSON.stringify(defaultSorting));
    tableUtils.sortTable("crud-manager-table", defaultSorting.columnNumber, defaultSorting.sortType, defaultSorting.direction);
  }

  function refreshView() {
    domUtils.setValue(document.getElementById('num-rows'), "");
    
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

$(document).ready(mainCrudManager);