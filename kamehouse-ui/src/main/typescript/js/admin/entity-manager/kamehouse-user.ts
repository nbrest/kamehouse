kameHouse.ready(() => {
  kameHouse.util.module.waitForModules(["crudManager"], () => {    
    kameHouse.plugin.crudManager.init({
      entityName: "KameHouse User",
      entityNameJapanese: "かめはうす ユーザー",
      url: "/kame-house-admin/api/v1/admin/kamehouse/users",
      banner: "banner-gohan-ssj2-3",
      icon: "/kame-house/img/dbz/goku-icon.png",
      infoImage: {
        img: "/kame-house/img/banners/dragonball/banner-z-senshi.jpg",
        title: "Enter KameHouse World",
        titlePosition: "bottom",
        desc: "Join the Z Senshi and become one of the most powerful beings in the universe by entering KameHouse",
        isReverse: true
      },
      columns: [
        { 
          name: "id",
          type: "id"
        },
        { 
          name: "username",
          type: "text"
        }, 
        { 
          name: "password",
          type: "password"
        }, 
        { 
          name: "email",
          type: "email"
        }, 
        { 
          name: "firstName",
          type: "text"
        }, 
        { 
          name: "lastName",
          type: "text"
        }, 
        { 
          name: "lastLogin",
          type: "date"
        }, 
        { 
          name: "authorities",
          type: "array",
          arrayType: "select",
          values: [
          	"ROLE_KAMISAMA",
          	"ROLE_SAIYAJIN",
          	"ROLE_NAMEKIAN"
          ],
          displayValues: [
          	"ROLE_KAMISAMA",
          	"ROLE_SAIYAJIN",
          	"ROLE_NAMEKIAN"
          ],
          buildEntity: (element) => {
            return kameHouse.extension.kameHouseUserCrudManager.buildRolesEntity(element);
          },
          buildFormField: (baseRoleSelectElement, roleEntity) => {
            return kameHouse.extension.kameHouseUserCrudManager.buildRolesFormField(baseRoleSelectElement, roleEntity);
          },
          buildListDisplay: (roles) => {
            return kameHouse.extension.kameHouseUserCrudManager.buildRolesListDisplay(roles);
          }
        },
        { 
          name: "accountNonExpired",
          type: "boolean"
        }, 
        { 
          name: "accountNonLocked",
          type: "boolean"
        }, 
        { 
          name: "credentialsNonExpired",
          type: "boolean"
        }, 
        { 
          name: "enabled",
          type: "boolean"
        }
      ]
    });
  });
});

/**
 * Custom build functions for kamehouse user CRUD.
 */
class KameHouseUserCrudManager {

  /**
   * Load the extension.
   */
  load() {
    kameHouse.logger.info("Started initializing kameHouseUserCrudManager", null);
  }

  /**
   * Build kamehouse user roles entity.
   */
  buildRolesEntity(element) {
    kameHouse.logger.debug("Building kamehouse user roles entity", null);
    for (const option of element.options) {
      if (option.selected && option.value != "") {
        const role = {
          name: option.value
        };
        const id = element.getAttribute("data-kamehouse-id");
        if (!kameHouse.core.isEmpty(id)) {
          kameHouse.logger.trace("Building role from element with data-kamehouse-id: " + id, null);
        }
        return role;
      }
    }
    kameHouse.logger.warn("Unable to build kamehouse user role", null);
    return null;
  }
  
  /**
   * Build kamehouse user roles form field.
   */
  buildRolesFormField(baseRoleSelectElement, roleEntity) {
    kameHouse.logger.debug("Building kamehouse user roles form field", null);
    const formField = kameHouse.util.dom.cloneNode(baseRoleSelectElement, true);
    kameHouse.util.dom.classListAdd(formField, "m-5-t-d-kh");
    kameHouse.util.dom.setAttribute(formField, "data-kamehouse-id", "");
    for (const option of formField.options) {
      if (option.value == roleEntity.name) {
        option.selected = true;
      } else {
        option.selected = false;
      }
    }
    if (!kameHouse.core.isEmpty(roleEntity.id)) {
      kameHouse.util.dom.setAttribute(formField, "data-kamehouse-id", roleEntity.id);
    }
    return formField;
  }
  
  /**
   * Build kamehouse user roles display list.
   */
  buildRolesListDisplay(roles) {
    kameHouse.logger.debug("Building kamehouse user roles display list", null);
    if (kameHouse.core.isEmpty(roles)) {
      return "[]";
    }
    const rolesToPrint = [];
    roles.forEach((role) => {
      rolesToPrint.push(role.name);
    });
    return kameHouse.json.stringify(rolesToPrint, null, null);
  }
  
}

kameHouse.ready(() => {kameHouse.addExtension("kameHouseUserCrudManager", new KameHouseUserCrudManager())});
