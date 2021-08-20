window.onload = () => {
  moduleUtils.waitForModules(["debuggerHttpClient", "crudManager"], () => {    
    crudManager.init({
      entityName: "KameHouse User",
      url: "/kame-house-admin/api/v1/admin/kamehouse/users",
      columns: ["id", "username", "password", "email", "firstName", "lastName", "lastLogin", "authorities", "accountNonExpired", "accountNonLocked", "credentialsNonExpired", "enabled"],
      createEntityRow: (tr, entity) => {
        logger.info("Custom createEntityRow()");
        domUtils.append(tr, domUtils.getTd({}, entity["id"])); 
        domUtils.append(tr, domUtils.getTd({}, entity["username"])); 
        domUtils.append(tr, domUtils.getTd({}, entity["password"])); 
        domUtils.append(tr, domUtils.getTd({}, entity["email"])); 
        domUtils.append(tr, domUtils.getTd({}, entity["firstName"])); 
        domUtils.append(tr, domUtils.getTd({}, entity["lastName"])); 
        domUtils.append(tr, domUtils.getTd({}, entity["lastLogin"])); 
        domUtils.append(tr, domUtils.getTd({}, JSON.stringify(entity["authorities"]))); 
        domUtils.append(tr, domUtils.getTd({}, entity["accountNonExpired"])); 
        domUtils.append(tr, domUtils.getTd({}, entity["accountNonLocked"])); 
        domUtils.append(tr, domUtils.getTd({}, entity["credentialsNonExpired"])); 
        domUtils.append(tr, domUtils.getTd({}, entity["enabled"])); 
      },
      setEditFormValues: (entity) => {
        logger.info("Custom setEditFormValues()");
        domUtils.setVal($("#crud-edit-input-fields-id"), entity["id"]); 
        domUtils.setVal($("#crud-edit-input-fields-username"), entity["username"]); 
        domUtils.setVal($("#crud-edit-input-fields-password"), entity["password"]); 
        domUtils.setVal($("#crud-edit-input-fields-email"), entity["email"]); 
        domUtils.setVal($("#crud-edit-input-fields-firstName"), entity["firstName"]); 
        domUtils.setVal($("#crud-edit-input-fields-lastName"), entity["lastName"]); 
        domUtils.setVal($("#crud-edit-input-fields-authorities"), JSON.stringify(entity["authorities"])); 
        domUtils.setVal($("#crud-edit-input-fields-accountNonExpired"), entity["accountNonExpired"]); 
        domUtils.setVal($("#crud-edit-input-fields-accountNonLocked"), entity["accountNonLocked"]); 
        domUtils.setVal($("#crud-edit-input-fields-credentialsNonExpired"), entity["credentialsNonExpired"]); 
        domUtils.setVal($("#crud-edit-input-fields-enabled"), entity["enabled"]); 
      },
      getEntityFromForm: (formFieldsId) => {
        logger.info("Custom getEntityFromForm()");
        const entity = {};
        entity["id"] = document.getElementById(formFieldsId + "-id").value;
        entity["username"] = document.getElementById(formFieldsId + "-username").value;
        entity["password"] = document.getElementById(formFieldsId + "-password").value;
        entity["email"] = document.getElementById(formFieldsId + "-email").value;
        entity["firstName"] = document.getElementById(formFieldsId + "-firstName").value;
        entity["lastName"] = document.getElementById(formFieldsId + "-lastName").value;
        entity["authorities"] = JSON.parse(document.getElementById(formFieldsId + "-authorities").value);
        entity["accountNonExpired"] = document.getElementById(formFieldsId + "-accountNonExpired").value;
        entity["accountNonLocked"] = document.getElementById(formFieldsId + "-accountNonLocked").value;
        entity["credentialsNonExpired"] = document.getElementById(formFieldsId + "-credentialsNonExpired").value;
        entity["enabled"] = document.getElementById(formFieldsId + "-enabled").value;
        return entity;
      }
    });
  });
};
