$(document).ready(() => {
  kameHouse.util.module.waitForModules(["crudManager"], () => {    
    kameHouse.plugin.crudManager.init({
      readOnly: true,
      entityName: "DragonBall User",
      url: "/kame-house-testmodule/api/v1/test-module/dragonball/users",
      banner: "banner-goku-ssj1",
      icon: "/kame-house/img/dbz/gohan-ssj2-icon.png",
      infoImage: {
        img: "/kame-house/img/dbz/shen-long-dragonballs.jpg",
        title: "Raise your Ki to the limit",
        desc: "Join Goku and Bulma to find the 7 dragonballs and make your wishes come true with Shen Long",
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
          name: "email",
          type: "email"
        }, 
        { 
          name: "age",
          type: "number"
        }, 
        { 
          name: "powerLevel",
          type: "number"
        }, 
        { 
          name: "stamina",
          type: "number"
        }
      ]
    });
  });
});
