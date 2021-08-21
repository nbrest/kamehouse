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

  const tbodyId = "crud-manager-tbody";
  const addInputFieldsId = "crud-add-input-fields";
  const editInputFieldsId = "crud-edit-input-fields";

  let entityName = "Set EntityName";
  let url = "/kame-house-module/api/v1/override-url";
  let columns = [];
  
  /**
   * Load the crud manager module.
   */
  function load() {
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
   *    columns: [ 
   *      {
   *        name: "columnName",
   *        type: "columnType",
   *        values: ["val1", "val2"],
   *        displayValues: ["dispVal1", "dispVal2"]
   *      },
   *      ...
   *    ]
   * }
   * 
   * - type is a custom definition of a type that I can then map to an input field
   * - types: 
   *    [ array, boolean, calendar, date, email, id, hidden, number, object, password, select, text ]
   * 
   * - values and displayValues are optional to be used in certain types such as select
   */
  function init(config) {
    setEntityName(config.entityName);
    setUrl(config.url);
    setColumns(config.columns);
    updateEntityNameInView();
    loadStateFromCookies();
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
   * Get an entity by it's id.
   */
  function read(id) {
    logger.trace("read");
    const getUrl = url + "/" + id;
    debuggerHttpClient.get(getUrl,
      (responseBody, responseCode, responseDescription) => {
        setEditFormValues(responseBody, responseCode, responseDescription);
      },
      (responseBody, responseCode, responseDescription) => {
        logger.error("Error getting entity: " + responseBody + responseCode + responseDescription);
        basicKamehouseModal.openApiError(responseBody, responseCode, responseDescription);
      }, null);
  }

  /**
   * Get all entities.
   */
  function readAll() {
    logger.trace("readAll");
    debuggerHttpClient.get(url,
      (responseBody, responseCode, responseDescription) => {
        reloadView(responseBody)
      },
      (responseBody, responseCode, responseDescription) => {
        logger.error("Error getting all entities: " + responseBody + responseCode + responseDescription);
        basicKamehouseModal.openApiError(responseBody, responseCode, responseDescription);
        displayErrorGettingEntities();
      }, null);
  }

  /**
   * Create an entity.
   */
  function create() {
    logger.trace("create");
    const entity = getEntityFromForm(addInputFieldsId);
    debuggerHttpClient.post(url, entity,
      (responseBody, responseCode, responseDescription) => {
        logger.info("Created entity successfully. Id: " + responseBody);
        readAll();
        tabUtils.openTab('tab-list', 'kh-crud-manager');
      },
      (responseBody, responseCode, responseDescription) => {
        logger.error("Error creating entity: " + responseBody + responseCode + responseDescription);
        basicKamehouseModal.openApiError(responseBody, responseCode, responseDescription);
        readAll();
      });
  }

  /**
   * Update an entity.
   */
  function update() {
    logger.trace("update");
    const entity = getEntityFromForm(editInputFieldsId);
    const updateUrl = url + "/" + entity.id;
    debuggerHttpClient.put(updateUrl, entity,
      (responseBody, responseCode, responseDescription) => {
        logger.info("Updated entity successfully. Id: " + entity.id);
        readAll();
        tabUtils.openTab('tab-list', 'kh-crud-manager');
      },
      (responseBody, responseCode, responseDescription) => {
        logger.error("Error updating entity: " + responseBody + responseCode + responseDescription);
        basicKamehouseModal.openApiError(responseBody, responseCode, responseDescription);
        readAll();
      }, null);
  }

  /**
   * Delete entity from the server.
   */
  function deleteEntity(id) {
    logger.trace("deleteEntity");
    const deleteUrl = url + "/" + id;
    debuggerHttpClient.delete(deleteUrl, null,
      (responseBody, responseCode, responseDescription) => {
        logger.info("Deleted entity successfully. Id: " + responseBody.id);
        readAll();
      },
      (responseBody, responseCode, responseDescription) => {
        logger.error("Error deleting entity: " + responseBody + responseCode + responseDescription);
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
    updateEditFormFieldValues(responseBody, columns, null);
  }

  /**
   * Set the input fields in the edit form from the entity.
   */
  function updateEditFormFieldValues(entity, currentNodeColumns, parentNodeChain) {
    parentNodeChain = initParentNodeChain(parentNodeChain);
    for (let i = 0; i < currentNodeColumns.length; i++) {
      const column = currentNodeColumns[i];
      const type = column.type;
      const name = column.name;
      if (type == "object") {
        updateEditFormFieldValues(entity[name], column.columns, parentNodeChain + name);
        continue;
      }
      const inputFieldId = editInputFieldsId + "-" + parentNodeChain + name;
      let inputField = $(document.getElementById(inputFieldId));
      domUtils.setVal(inputField, entity[name]); 

      if (isDateField(type)) {
        domUtils.setVal(inputField, getFormattedDateFieldValue(entity[name]));
      }
      if (isArrayField(type)) {
        domUtils.setVal(inputField, JSON.stringify(entity[name], null, 6)); 
      }
      if (isBooleanField(type)) {
        if (entity[name] == true || entity[name] == "true") {
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
  function reloadView(entities) {
    logger.trace("reloadView");
    const crudTbody = $('#' + tbodyId);
    domUtils.empty(crudTbody);
    domUtils.append(crudTbody, getCrudTableHeader());
    for (let i = 0; i < entities.length; i++) {
      domUtils.append(crudTbody, getEntityTr(entities[i]));
    }
    reloadForm(addInputFieldsId);
    reloadForm(editInputFieldsId);
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
    domUtils.append(tr, getActionButtonsTd(entity.id));
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
    for (let i = 0; i < currentNodeColumns.length; i++) {
      const column = currentNodeColumns[i];
      const type = column.type;
      const name = column.name;
      if (type == "object") {
        createEntityRow(tr, entity[name], column.columns, parentNodeChain + name);
        continue;
      }

      if (isPasswordField(type) || isHiddenField(type)) {
        domUtils.append(tr, getMaskedFieldTd());
        continue;
      }
      if (isDateField(type)) {
        domUtils.append(tr, domUtils.getTd({}, getFormattedDateFieldValue(entity[name])));
        continue;
      }
      if (isArrayField(type)) {
        domUtils.append(tr, domUtils.getTd({}, JSON.stringify(entity[name])));
        continue;
      }
      domUtils.append(tr, domUtils.getTd({}, entity[name]));
    }
  };

  /**
   * Returns a date field formatted.
   */
  function getFormattedDateFieldValue(value) {
    try {
      const date = new Date(parseInt(value));
      if (isValidDate(date)) {
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
   * Checks if it's a valid date.
   */
  function isValidDate(date) {
    return date instanceof Date && !isNaN(date);
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
    domUtils.append(td, getDeleteButton(id));
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
   * Get the delete button for the entity.
   */
  function getDeleteButton(id) {
    return domUtils.getImgBtn({
      src: "/kame-house/img/other/delete-red.png",
      className: "img-btn-kh",
      alt: "Delete",
      onClick: () => deleteEntity(id)
    });
  }

  /**
   * Get the table header row.
   */
  function getCrudTableHeader() {
    const tr = domUtils.getTr({
      class: "table-kh-header"
    }, null);
    setHeaderColumns(tr, columns, null);
    domUtils.append(tr, domUtils.getTd({
      class: "table-kh-actions"
    }, "actions"));
    return tr;
  }

  /**
   * Set the table header columns.
   */
  function setHeaderColumns(tr, currentNodeColumns, parentNodeChain) {
    parentNodeChain= initParentNodeChain(parentNodeChain);
    for (let i = 0; i < currentNodeColumns.length; i++) {
      const column = currentNodeColumns[i];
      const type = column.type;
      if (type == "object") {
        setHeaderColumns(tr, column.columns, parentNodeChain + column.name);
        continue;
      }

      domUtils.append(tr, domUtils.getTd(null, parentNodeChain + column.name)); 
    }
  }

  /**
   * Get all the form fields.
   */
   function getFormFields(div, formFieldsId, currentNodeColumns, parentNodeChain) {
    parentNodeChain= initParentNodeChain(parentNodeChain);
    for (let i = 0; i < currentNodeColumns.length; i++) {
      const column = currentNodeColumns[i];
      const type = column.type;
      if (type == "object") {
        getFormFields(div, formFieldsId, column.columns, parentNodeChain + column.name);
        continue;
      }

      const name = parentNodeChain + column.name;
      const fieldId = formFieldsId + "-" + name;
      const fieldClassList = "form-input-kh " + formFieldsId + "-field";
      
      addFieldLabel(div, type, name);
      domUtils.append(div, getFormInputField(column, fieldId, fieldClassList));
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

    if (isArrayField(type)) {
      return domUtils.getTextArea(config, null);
    }

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

    return domUtils.getInput(config, null);
  }

  /**
   * Map the type of the column to an input field type.
   */
  function getInputFieldType(columnType) {
    if (columnType == "array") {
      return "textarea"
    }
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
    if (columnType == "password") {
      return "password";
    }
    if (columnType == "select") {
      return "select";
    }
    return "input";
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
   * Add checkbox to show password.
   */
  function addShowPasswordCheckbox(div, type, fieldId) {
    if (isPasswordField(type)) {
      const checkbox = domUtils.getInput({
        type: "checkbox",
       }, null);
      domUtils.setClick(checkbox, () => toggleShowHidePassword(fieldId));
      domUtils.append(div, checkbox);
    }
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
   * Build the entity to pass to the backend from the form data.
   */
  function getEntityFromForm(formFieldsId) {
    const entity = {};
    setEntityProperties(entity, formFieldsId, columns, null);
    return entity;
  }

  function setEntityProperties(entity, formFieldsId, currentNodeColumns, parentNodeChain) {
    parentNodeChain = initParentNodeChain(parentNodeChain);
    for (let i = 0; i < currentNodeColumns.length; i++) {
      const column = currentNodeColumns[i];
      const type = column.type;
      const name = column.name;
      if (type == "object") {
        if (isEmpty(entity[name])) {
          entity[name] = {};
        }
        setEntityProperties(entity[name], formFieldsId, column.columns, parentNodeChain + name);
        continue;
      }

      const inputFieldId = formFieldsId + "-" + parentNodeChain + name;
      let val = document.getElementById(inputFieldId).value;
      if (isBooleanField(type)) {
        val = document.getElementById(inputFieldId).checked;
      }

      entity[name] = val;

      if (isEmpty(val) || val == "") {
        entity[name] = null;
      }
      if (isArrayField(type)) {
        entity[name] = JSON.parse(val);
      }
    }
  }


  /**
   * Clear all the form fields.
   */
  function clearForm(formFieldsId) {
    const fieldsClass = formFieldsId + "-field";
    const intputFields = document.getElementsByClassName(fieldsClass);
    for (let i = 0; i < intputFields.length; i++) {
      domUtils.setVal($("#" + intputFields[i].id), "");
    }
  }
}

$(document).ready(mainCrudManager);