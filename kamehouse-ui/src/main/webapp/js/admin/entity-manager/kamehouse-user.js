$(document).ready(() => {
  kameHouse.util.module.waitForModules(["crudManager"], () => {    
    kameHouse.plugin.crudManager.init({
      entityName: "KameHouse User",
      url: "/kame-house-admin/api/v1/admin/kamehouse/users",
      banner: "banner-gohan-ssj2-3",
      icon: "/kame-house/img/dbz/goku-icon.png",
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
            kameHouse.logger.debug("Building kamehouse user role entity");
            for (let i = 0; i < element.options.length; ++i) {
              if (element.options[i].selected == true && element.options[i].value != "") {
                const role = {
                  name: element.options[i].value
                };
                const id = element.getAttribute("data-kamehouse-id");
                if (!kameHouse.core.isEmpty(id)) {
                  kameHouse.logger.trace("Building role from element with data-kamehouse-id: " + id);
                }
                return role;
              }
            }
            kameHouse.logger.warn("Unable to build kamehouse user role");
            return null;
          },
          buildFormField: (baseRoleSelectElement, roleEntity) => {
            kameHouse.logger.debug("Building kamehouse user role form field");
            const formField = kameHouse.util.dom.cloneNode(baseRoleSelectElement, true);
            kameHouse.util.dom.classListAdd(formField, "m-5-t-d-kh");
            kameHouse.util.dom.setAttribute(formField, "data-kamehouse-id", "");
            for (let i = 0; i < formField.options.length; ++i) {
              if (formField.options[i].value == roleEntity.name) {
                formField.options[i].selected = true;
              } else {
                formField.options[i].selected = false;
              }
            }
            if (!kameHouse.core.isEmpty(roleEntity.id)) {
              kameHouse.util.dom.setAttribute(formField, "data-kamehouse-id", roleEntity.id);
            }
            return formField;
          },
          buildListDisplay: (roles) => {
            if (kameHouse.core.isEmpty(roles)) {
              return "[]";
            }
            const rolesToPrint = [];
            roles.forEach((role) => {
              rolesToPrint.push(role.name);
            });
            return JSON.stringify(rolesToPrint);
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
