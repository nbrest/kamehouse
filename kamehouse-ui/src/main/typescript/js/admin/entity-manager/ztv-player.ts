kameHouse.ready(() => {
  kameHouse.util.module.waitForModules(["crudManager"], () => {    
    kameHouse.plugin.crudManager.init({
      entityName: "Ztv Player",
      entityNameJapanese: "ZTVプレーヤー",
      url: "/kame-house-ztvrc/api/v1/ztv-rc/players",
      banner: "banner-pegasus-ryu-sei-ken",
      icon: "/kame-house/img/mplayer/yandex_music_blue.png",
      infoImage: {
        img: "/kame-house/img/banners/saint-seiya/banner-athena-saints.jpg",
        title: "KameHouse Media Player",
        titlePosition: "bottom",
        desc: "Control all the media stored in this KameHouse server with ztv player",
        isReverse: false
      },
      columns: [
        { 
          name: "id",
          type: "id"
        },
        { 
          name: "hostname",
          type: "text"
        }, 
        { 
          name: "port",
          type: "number"
        }, 
        { 
          name: "username",
          type: "text"
        }, 
        { 
          name: "password",
          type: "password"
        }
      ]
    });
  });
});
