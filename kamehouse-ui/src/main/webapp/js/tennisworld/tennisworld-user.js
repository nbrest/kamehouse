$(document).ready(() => {
  kameHouse.util.module.waitForModules(["crudManager"], () => {    
    kameHouse.plugin.crudManager.init({
      entityName: "TennisWorld User",
      entityNameJapanese: "テニスワールド ユーザー",
      url: "/kame-house-tennisworld/api/v1/tennis-world/users",
      banner: "banner-fuji",
      icon: "/kame-house/img/prince-of-tennis/fuji-icon.png",
      infoImage: {
        img: "/kame-house/img/banners/prince-of-tennis/banner-seigaku.jpg",
        title: "Join Tennis World?",
        titlePosition: "bottom",
        desc: "Echizen Ryoma, Fuji Syuske, Tezuka Kunimitzu are already waiting for you",
        isReverse: true
      },
      columns: [
        { 
          name: "id",
          type: "id"
        },
        { 
          name: "email",
          type: "email"
        }, 
        { 
          name: "password",
          type: "password"
        }
      ]
    });
  });
});
