window.onload = () => {
  moduleUtils.waitForModules(["debuggerHttpClient", "crudManager"], () => {    
    crudManager.init({
      entityName: "TennisWorld User",
      url: "/kame-house-tennisworld/api/v1/tennis-world/users",
      columns: ["id", "email_custom", "password_custom"],
      createEntityRow: (tr, entity) => {
        logger.info("Custom createEntityRow()");
        domUtils.append(tr, domUtils.getTd({}, entity["id"])); 
        domUtils.append(tr, domUtils.getTd({}, entity["email"])); 
        domUtils.append(tr, domUtils.getTd({}, entity["password"])); 
      },
      setEditFormValues: (entity) => {
        logger.info("Custom setEditFormValues()");
        domUtils.setVal($("#crud-edit-input-fields-id"), entity["id"]); 
        domUtils.setVal($("#crud-edit-input-fields-email_custom"), entity["email"]); 
        domUtils.setVal($("#crud-edit-input-fields-password_custom"), entity["password"]); 
      },
      getEntityFromForm: (formFieldsId) => {
        logger.info("Custom getEntityFromForm()");
        const entity = {};
        entity["id"] = document.getElementById(formFieldsId + "-id").value;
        entity["email"] = document.getElementById(formFieldsId + "-email_custom").value;
        entity["password"] = document.getElementById(formFieldsId + "-password_custom").value;
        return entity;
      }
    });
  });
};
