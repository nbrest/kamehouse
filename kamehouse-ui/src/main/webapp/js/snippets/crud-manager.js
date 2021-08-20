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
  let createEntityRow = defaultCreateEntityRow;
  let setEditFormValues = defaultSetEditFormValues;
  let getEntityFromForm = defaultGetEntityFromForm;
  
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
   * Initialize the crud manager. Configuration object can be: 
   * {
   *    entityName: "EntityName",
   *    url: "/kame-house-module/etc",
   *    columns: ["col1", "col2"],
   *    createEntityRow: () => {},
   *    setEditFormValues: () => {},
   *    getEntityFromForm: () => {}
   * }
   * It doesn't need to contail all elements, only the ones I need to override
   * from the default behavior.
   */
  function init(config) {
    if (!isEmpty(config.entityName)) {
      setEntityName(config.entityName);
    }
    if (!isEmpty(config.url)) {
      setUrl(config.url);
    }
    if (!isEmpty(config.columns)) {
      setColumns(config.columns);
    }
    if (!isEmpty(config.createEntityRow)) {
      setCreateEntityRow(config.createEntityRow);
    }
    if (!isEmpty(config.setEditFormValues)) {
      setSetEditFormValues(config.setEditFormValues);
    }
    if (!isEmpty(config.getEntityFromForm)) {
      setGetEntityFromForm(config.getEntityFromForm);
    }
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
   * Override the createEntityRow function called after readAll() to populate the table of all entities.
   */
   function setCreateEntityRow(createEntityRowFunction) {
    if (isFunction(createEntityRowFunction)) {
      createEntityRow = createEntityRowFunction;
    } else {
      logger.error("The parameter passed to createEntityRow is not a function. Using default function");
    }
  }

  /**
   * Override the function to set the edit form values before editing an entity.
   */
   function setSetEditFormValues(setEditFormValuesFunction) {
    if (isFunction(setEditFormValuesFunction)) {
      setEditFormValues = setEditFormValuesFunction;
    } else {
      logger.error("The parameter passed to setEditFormValues is not a function. Using default function");
    } 
  }
  
  /**
   * Override the function to create the entity to send the backend requests from the form fields.
   */
   function setGetEntityFromForm(getEntityFromFormFunction) {
    if (isFunction(getEntityFromFormFunction)) {
      getEntityFromForm = getEntityFromFormFunction;
    } else {
      logger.error("The parameter passed to getEntityFromForm is not a function. Using default function");
    } 
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
  function defaultSetEditFormValues(responseBody, responseCode, responseDescription) { 
    logger.debug("readCallback: override this with setReadCallback when required");
    updateEditFormFieldValues(responseBody, null);
  }

  /**
   * Set the input fields in the edit form from the entity. Supports one level of nested objects.
   */
  function updateEditFormFieldValues(entity, parentNode) {
    for (const property in entity) {
      if (!isObject(entity[property]) || isArray(entity[property])) {
        let inputField = $("#" + editInputFieldsId + "-" + property);
        if (parentNode) {
          inputField = $("#" + editInputFieldsId + "-" + parentNode + "\\." + property);
        }
        if (isDateField(property)) {
          domUtils.setVal(inputField, getFormattedDateFieldValue(entity[property]));
          continue;
        } 
        if (isArray(entity[property])) {
          domUtils.setVal(inputField, JSON.stringify(entity[property])); 
          continue;
        }
        domUtils.setVal(inputField, entity[property]); 
      } else {
        updateEditFormFieldValues(entity[property], property);
      }
    }
  }

  /**
   * Display all entities and reload forms.
   */
  function reloadView(entities) {
    logger.trace("reloadView");
    const crudTbody = $('#' + tbodyId);
    domUtils.empty(crudTbody);
    domUtils.append(crudTbody, getCrudTableHeader(entities[0]));
    for (let i = 0; i < entities.length; i++) {
      domUtils.append(crudTbody, getEntityTr(entities[i]));
    }
    reloadForm(addInputFieldsId, entities[0]);
    reloadForm(editInputFieldsId, entities[0]);
  }

  /**
   * Reload form view.
   */
  function reloadForm(formFieldsId, entity) {
    const formFields = $('#' + formFieldsId);
    domUtils.empty(formFields);
    domUtils.append(formFields, getFormFields(formFieldsId, entity, null));
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
    createEntityRow(tr, entity);
    domUtils.append(tr, getActionButtonsTd(entity.id));
    return tr;
  }

  /**
   * Override this function with setCreateEntityTd() to create the data row for the entity
   * this crud manager will display in the table if the fields to show need to be customized.
   */
   function defaultCreateEntityRow(tr, entity) {
    for (const property in entity) {
      if (!isObject(entity[property]) || isArray(entity[property])) {
        if (isPasswordField(property)) {
          domUtils.append(tr, getMaskedFieldTd());
          continue;
        }
        if (isDateField(property)) {
          domUtils.append(tr, domUtils.getTd({}, getFormattedDateFieldValue(entity[property])));
          continue;
        }
        if (isArray(entity[property])) {
          domUtils.append(tr, domUtils.getTd({}, JSON.stringify(entity[property])));
          continue;
        }
        domUtils.append(tr, domUtils.getTd({}, entity[property]));
      } else {
        defaultCreateEntityRow(tr, entity[property]);
      }
    }
  };

  /**
   * Returns a date field formatted.
   */
  function getFormattedDateFieldValue(value) {
    try {
      const date = new Date(parseInt(value));
      if (isValidDate(date)) {
        return timeUtils.getDateWithTimezoneOffset(date).toISOString();
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
  function getCrudTableHeader(entity) {
    const tr = domUtils.getTr({
      class: "table-kh-header"
    }, null);
    if ((isEmpty(columns) || columns.length == 0) && !isEmpty(entity)) {
      // create the column names from the entity object keys
      addColumnsFromEntityToHeader(tr, entity, null);
    } else {
      // create the column names from the columns property
      addColumnsFromPropertyToHeader(tr);
    }
    domUtils.append(tr, domUtils.getTd({
      class: "table-kh-actions"
    }, "actions"));
    return tr;
  }

  /**
   * Add columns to the header from the columns property
   */
  function addColumnsFromPropertyToHeader(tr) {
    columns.forEach((column) => {
      domUtils.append(tr, domUtils.getTd(null, column));
    });
  }

  /**
   * Add columns to table header. Supports one level of object nesting.
   */
  function addColumnsFromEntityToHeader(tr, entity, parentNode) {
    for (const property in entity) {
      if (!isObject(entity[property]) || isArray(entity[property])) {
        let propertyName = property;
        if (parentNode) {
          propertyName = parentNode + "." + property;
        }
        domUtils.append(tr, domUtils.getTd({}, propertyName));
      } else {
        addColumnsFromEntityToHeader(tr, entity[property], property);
      }
    }
  }

  /**
   * Check if the specified variable is an object.
   */
  function isObject(obj) {
    return obj === Object(obj);
  }

  /**
   * Check if the specified variable is an array.
   */
  function isArray(obj) {
    return !isEmpty(obj) && Array.isArray(obj);
  }

  /**
   * Checks if it's an array converted to a string.
   */
  function isStringArray(val) {
    try {
      return isArray(JSON.parse(val));
    } catch (error) {
      return false;
    }
  }

  /**
   * Get the table header row.
   */
   function getFormFields(formFieldsId, entity, parentNode) {
    const div = domUtils.getDiv({}, null);
    if ((isEmpty(columns) || columns.length == 0) && !isEmpty(entity)) {
      // create the input fields from the entity object keys
      getEntityFormFields(div, formFieldsId, entity, parentNode);
    } else {
      // create the input fields from the columns property
      getColumnsPropertyFormFields(div, formFieldsId);
    }
    return div;
  }

  /**
   * Get form fields from the columns property.
   */
  function getColumnsPropertyFormFields(div, formFieldsId) {
    columns.forEach((column) => {
      domUtils.append(div, getFormField(formFieldsId, column));
    });
  }

  /**
   * Get the form fields from the entity.
   */
  function getEntityFormFields(div, formFieldsId, entity, parentNode) {
    for (const property in entity) { 
      if (!isObject(entity[property]) || isArray(entity[property])) {
        let propertyName = property;
        if (parentNode) {
          propertyName = parentNode + "." + property;
        }
        domUtils.append(div, getFormField(formFieldsId, propertyName));
      } else {
        getEntityFormFields(div, formFieldsId, entity[property], property);
      }
    }
  }

  /**
   * Add a form field.
   */
  function getFormField(formFieldsId, fieldName) {
    const div = domUtils.getDiv({}, null);
    const type = getInputFieldType(fieldName);
    const fieldId = formFieldsId + "-" + fieldName;
    const fieldClassList = "form-input-kh " + formFieldsId + "-field";
    
    addFieldLabel(div, fieldName);
    addFormInputField(div, fieldId, fieldName, fieldClassList, type);
    addShowPasswordCheckbox(div, fieldId, fieldName);
    addBreak(div, fieldName);
    return div;
  }

  /**
   * Add an input field for the form.
   */
  function addFormInputField(div, fieldId, fieldName, fieldClassList, type) {
    domUtils.append(div, domUtils.getInput({
      id: fieldId,
      class: fieldClassList,
      type: type,
      name: fieldName
    }, null));
  }

  /**
   * Add label to field.
   */
  function addFieldLabel(div, fieldName) {
    if (!isIdField(fieldName)) {
      domUtils.append(div, domUtils.getLabel({}, fieldName));
    }
  }

  /**
   * Add break after input field.
   */
  function addBreak(div, fieldName) {
    if (!isIdField(fieldName)) {
      domUtils.append(div, domUtils.getBr());
    }
  }

  /**
   * Get the input field type.
   */
  function getInputFieldType(fieldName) {
    if (isPasswordField(fieldName)) {
      return "password";
    }
    if (isIdField(fieldName)) {
      return "hidden";
    }
    return "input";
  }

  /**
   * Add checkbox to show password.
   */
  function addShowPasswordCheckbox(div, fieldId, fieldName) {
    if (isPasswordField(fieldName)) {
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
  function isIdField(fieldName) {
    return fieldName == "id" || fieldName == "Id" || fieldName.includes(".id") || fieldName.includes(".Id");
  }

  /**
   * Check if it's a password field.
   */
  function isPasswordField(fieldName) {
    return fieldName == "password" || fieldName == "Password" || fieldName.includes(".password") || fieldName.includes(".Password");
  }

  /**
   * Check if it's a date field.
   */
  function isDateField(fieldName) {
    return fieldName.includes("date") || fieldName.includes("Date");
  }

  /**
   * Build the entity to pass to the backend from the form data.
   * This method can be overriden to build entities from custom forms.
   * By default it handles entities with up to one level of nested objects.
   */
  function defaultGetEntityFromForm(formFieldsId) {
    const fieldsClass = formFieldsId + "-field";
    const entity = {};
    const intputFields = document.getElementsByClassName(fieldsClass);
    for (let i = 0; i < intputFields.length; i++) {
      const name = intputFields[i].getAttribute("name");
      const val = intputFields[i].value;

      if (name.includes(".")) {
        const nameArray = name.split(".");
        const parentNode = nameArray[0];
        const propertyName = nameArray[1];
       
        if (isEmpty(entity[parentNode])) {
          entity[parentNode] = {};
        }

        addPropertyToEntity(entity[parentNode], propertyName, val);
      } else {
        addPropertyToEntity(entity, name, val);
      }
    }
    return entity;
  }

  /**
   * Add a property to the entity
   */
  function addPropertyToEntity(entity, propertyName, propertyValue) {
    if (!isEmpty(propertyValue) && propertyValue != "") {
      if (isArray(propertyValue) || isStringArray(propertyValue)) {
        entity[propertyName] = JSON.parse(propertyValue);
      } else {
        entity[propertyName] = propertyValue;
      }
    } else {
      entity[propertyName] = null;
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